package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.versionOption
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
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
        completionCandidates = CompletionCandidates.Fixed("TEXT", "JSON")
    ).enum<Format>().default(Format.TEXT)

    private val report by argument(name = "jacoco-report.xml")
        .file(mustExist = true, canBeFile = true)
        .validate {
            require(it.extension == "xml") {
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
            println("Execution time: $time ms")
        }
    }

    private fun parseReport() {
        val parser = JacocoParser()
        val coverage: ModuleCoverage = parser.parse(report)
        when (format) {
            Format.JSON -> {
                println(coverage.toJson())
            }
            Format.TEXT -> {
                println(coverage)
            }
        }
    }
}

private enum class Format { TEXT, JSON }
