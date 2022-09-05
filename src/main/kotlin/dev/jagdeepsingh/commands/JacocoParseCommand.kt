package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.output.defaultCliktConsole
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import de.m3y.kformat.Table
import de.m3y.kformat.table
import dev.jagdeepsingh.parser.JacocoParser
import dev.jagdeepsingh.parser.models.ModuleCoverage
import kotlin.system.measureTimeMillis

class JacocoParseCommand : CliktCommand(
    name = "jacoco-parser",
    printHelpOnEmptyArgs = true,
    help = "Parse JaCoCo xml coverage report."
) {

    private val debug by option(
        "-d", "--debug",
        help = "Enable debug logging"
    ).flag()

    private val format: Format by option(
        "-o", "--output",
        help = "Output format",
        completionCandidates = CompletionCandidates.Fixed("TABLE", "TEXT", "JSON")
    ).enum<Format>().default(Format.TABLE)

    private val reports by argument(name = "jacoco-report.xml")
        .file(mustExist = true, canBeFile = true)
        .multiple(required = true)
        .validate { files ->
            require(files.all { it.extension == "xml" }) {
                "report should be a xml file"
            }
        }

    init {
        completionOption()
        versionOption(
            version = "1.0.0",
            names = setOf("-v", "--version")
        )
    }

    override fun run() {
        val time = measureTimeMillis { parseReport() }
        if (debug) {
            echo("Execution time: $time ms")
        }
    }

    private fun parseReport() {
        val parser = JacocoParser()
        for (report in reports) {
            val coverage: ModuleCoverage = parser.parse(report)
            when (format) {
                Format.JSON -> {
                    echo(coverage.toJson())
                }

                Format.TEXT -> {
                    echo(coverage)
                }

                Format.TABLE -> {
                    table {
                        header(COLUMN_MODULE, COLUMN_PACKAGE, COLUMN_COVERAGE)
                        for (p in coverage.packages) {
                            row(coverage.moduleName, p.packageName, p.instruction.coverageInString)
                        }
                        header()
                        header("Total", "", coverage.instruction.coverageInString)

                        hints {
                            alignment(COLUMN_MODULE, Table.Hints.Alignment.LEFT)
                            alignment(COLUMN_PACKAGE, Table.Hints.Alignment.LEFT)
                            alignment(COLUMN_COVERAGE, Table.Hints.Alignment.RIGHT)
                            postfix(COLUMN_COVERAGE, "%")
                            borderStyle = Table.BorderStyle.SINGLE_LINE
                        }
                    }.render().let { echo(it) }
                }
            }
        }
    }
}

private const val COLUMN_MODULE = "Module"
private const val COLUMN_PACKAGE = "Packages"
private const val COLUMN_COVERAGE = "Coverage"

private enum class Format { TABLE, TEXT, JSON }
