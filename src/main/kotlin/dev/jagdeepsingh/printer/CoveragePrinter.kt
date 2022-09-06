package dev.jagdeepsingh.printer

import de.m3y.kformat.Table
import de.m3y.kformat.table
import dev.jagdeepsingh.config.OwnerName
import dev.jagdeepsingh.config.OwnersConfig
import dev.jagdeepsingh.config.PackageName
import dev.jagdeepsingh.logger.Logger
import dev.jagdeepsingh.parser.models.ModuleCoverage
import java.text.DecimalFormat

interface CoveragePrinter {
    fun print(coverages: List<ModuleCoverage>)
}

class TextCoveragePrinter : CoveragePrinter {

    override fun print(coverages: List<ModuleCoverage>) {
        for (coverage in coverages) {
            println(coverage)
        }
    }
}

class JsonCoveragePrinter : CoveragePrinter {

    override fun print(coverages: List<ModuleCoverage>) {
        for (coverage in coverages) {
            println(coverage.toJson())
        }
    }
}

class ModuleTableCoveragePrinter(private val ownerConfig: OwnersConfig) : CoveragePrinter {

    override fun print(coverages: List<ModuleCoverage>) {
        table {
            header(COLUMN_MODULE, COLUMN_TEAM, COLUMN_PACKAGE, COLUMN_COVERAGE)

            for (coverage in coverages) {
                for (p in coverage.packages) {
                    val team = (ownerConfig[PackageName(p.packageName)] ?: OwnerName("")).toString()
                    row(coverage.moduleName, team, p.packageName, p.instruction.coverageInString)
                }
                header()
                header("Total", "", "", coverage.instruction.coverageInString)

                hints {
                    alignment(COLUMN_MODULE, Table.Hints.Alignment.LEFT)
                    alignment(COLUMN_TEAM, Table.Hints.Alignment.LEFT)
                    alignment(COLUMN_PACKAGE, Table.Hints.Alignment.LEFT)
                    alignment(COLUMN_COVERAGE, Table.Hints.Alignment.RIGHT)
                    postfix(COLUMN_COVERAGE, "%")
                    borderStyle = Table.BorderStyle.SINGLE_LINE
                }
            }
        }.render().let { println(it) }
    }

    companion object {
        private const val COLUMN_MODULE = "Module"
        private const val COLUMN_TEAM = "Team"
        private const val COLUMN_PACKAGE = "Packages"
        private const val COLUMN_COVERAGE = "Coverage"
    }
}

class TeamTableCoveragePrinter(
    private val ownerConfig: OwnersConfig,
    private val formatter: DecimalFormat,
    private val logger: Logger
) : CoveragePrinter {

    override fun print(coverages: List<ModuleCoverage>) {
        table {
            header(COLUMN_TEAM, COLUMN_COVERAGE)

            val (packagesWithoutTeam, packagesWithTeam) = mapCoverageToRows(coverages)
            val groupedByTeam = packagesWithTeam.groupBy { it.team }
            for (item in groupedByTeam) {
                val teamName = item.key
                val packages = item.value
                val coverage = packages.sumOf { it.coverage } / packages.size
                row(teamName, formatter.format(coverage))
            }

            header()

            logger.logInfo("Packages without teams: ${packagesWithoutTeam.size}")
            logger.logInfo("Packages with teams: ${packagesWithTeam.size}")
            logger.doIfDebug { packagesWithoutTeam.forEach(::logDebug) }

            hints {
                alignment(COLUMN_TEAM, Table.Hints.Alignment.LEFT)
                alignment(COLUMN_COVERAGE, Table.Hints.Alignment.RIGHT)
                postfix(COLUMN_COVERAGE, "%")
                borderStyle = Table.BorderStyle.SINGLE_LINE
            }
        }.render().let { println(it) }
    }

    private fun mapCoverageToRows(coverages: List<ModuleCoverage>): Pair<MutableList<String>, MutableList<TableRow>> {
        val packagesWithoutTeam = mutableListOf<String>()
        val packagesWithTeam = mutableListOf<TableRow>()

        coverages.map { moduleCoverage ->
            moduleCoverage.packages.map { p ->
                val team = ownerConfig[PackageName(p.packageName)]
                if (team != null) {
                    packagesWithTeam.add(
                        TableRow(
                            team = team.toString(),
                            coverage = p.instruction.coverage
                        )
                    )
                } else {
                    packagesWithoutTeam.add(p.packageName)
                }
            }
        }

        return packagesWithoutTeam to packagesWithTeam
    }

    private class TableRow(
        val team: String,
        val coverage: Double
    )

    companion object {
        private const val COLUMN_TEAM = "Team"
        private const val COLUMN_COVERAGE = "Coverage"
    }
}
