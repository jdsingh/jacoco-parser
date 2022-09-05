package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import dev.jagdeepsingh.config.*
import dev.jagdeepsingh.logger.DefaultLogger
import dev.jagdeepsingh.logger.LogLevel
import dev.jagdeepsingh.logger.Logger
import dev.jagdeepsingh.parser.JacocoParser
import dev.jagdeepsingh.parser.models.ModuleCoverage
import dev.jagdeepsingh.printer.JsonCoveragePrinter
import dev.jagdeepsingh.printer.TableCoveragePrinter
import dev.jagdeepsingh.printer.TeamTableCoveragePrinter
import dev.jagdeepsingh.printer.TextCoveragePrinter
import java.text.DecimalFormat
import kotlin.system.measureTimeMillis

private enum class Format { TEAM_TABLE, MODULE_TABLE, TEXT, JSON }

class JacocoParseCommand : CliktCommand(
    name = "jacoco-parser", help = "Parse JaCoCo xml coverage report."
) {

    private val logLevel: LogLevel by option(
        "-l", "--log", help = "Log level"
    ).enum<LogLevel>().default(LogLevel.None)

    private val format: Format by option(
        "-o",
        "--output",
        help = "Output format"
    ).enum<Format>().default(Format.TEAM_TABLE)

    private val modules: List<String> by option("--modules", hidden = true).multiple()

    private val jacocoReportDir: String? by option("--jacocoReportDir", hidden = true)

    private val owners: Map<String, String> by option("--owners", hidden = true).associate()

    init {
        completionOption()
        versionOption(
            version = "1.0.0", names = setOf("-v", "--version")
        )

        context {
            valueSources(
                JsonValueSource.from(System.getProperty("user.dir") + "/config.json", requireValid = true),
            )
        }
    }

    override fun run() {
        val logger: Logger = DefaultLogger(level = logLevel)

        val time = measureTimeMillis { execute(logger) }
        logger.logDebug("Execution time: $time ms")

    }

    private fun execute(logger: Logger) {
        val parser = JacocoParser()
        val modulesConfigTransformer = ModulesConfigTransformer()
        val reportDirConfigTransformer = ReportDirConfigTransformer()

        val jacocoReportDir = try {
            reportDirConfigTransformer.invoke(jacocoReportDir)
        } catch (e: IllegalArgumentException) {
            logger.logError(e.localizedMessage)
            return
        }

        val moduleNames: List<ModuleName> = modulesConfigTransformer.invoke(modules)
        logger.logDebug("modules(${moduleNames.size}): $moduleNames")

        val ownerConfig: OwnersConfig = OwnersConfigTransformer().invoke(owners)
        logger.logDebug("owners(${ownerConfig.size}): $ownerConfig")

        parseReport(parser, moduleNames, jacocoReportDir, ownerConfig)
    }

    private fun parseReport(
        parser: JacocoParser,
        modules: List<ModuleName>,
        jacocoReportDir: JacocoReportDir,
        ownerConfig: OwnersConfig
    ) {
        val coverage: List<ModuleCoverage> = modules.map { module ->
            val report = jacocoReportDir.toFile(module)
            parser.parse(report)
        }

        when (format) {
            Format.JSON -> JsonCoveragePrinter().print(coverage)
            Format.TEXT -> TextCoveragePrinter().print(coverage)
            Format.MODULE_TABLE -> TableCoveragePrinter(ownerConfig).print(coverage)
            Format.TEAM_TABLE -> TeamTableCoveragePrinter(ownerConfig, DecimalFormat("##.##")).print(coverage)
        }
    }
}
