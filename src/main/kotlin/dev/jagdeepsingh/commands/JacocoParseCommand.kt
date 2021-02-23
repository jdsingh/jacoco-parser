package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.completion.CompletionCandidates
import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import dev.jagdeepsingh.parser.JacocoCoverage
import dev.jagdeepsingh.parser.JacocoParser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.system.measureTimeMillis

class JacocoParseCommand : CliktCommand(
    name = "jacoco-parser",
    printHelpOnEmptyArgs = true,
    help = """
        Parse JaCoCo xml coverage report.
    """.trimIndent(),
    autoCompleteEnvvar = "jagdeep"
) {

    private val debug by option(
        "--debug",
        help = "Enable debug logging"
    ).flag()

    private val format by option(
        "--output",
        help = "Console Output format",
        completionCandidates = CompletionCandidates.Fixed("TEXT", "JSON")
    ).enum<Format>()

    private val report by argument(name = "report.xml")
        .file(mustExist = true, canBeFile = true)
        .validate {
            require(it.extension == "xml") {
                "report should be a xml file"
            }
        }

    init {
        completionOption()
    }

    override fun run() {
        val parser = JacocoParser()

        val coverage: JacocoCoverage
        val time = measureTimeMillis {
            coverage = parser.parse(report)
        }

        when (format) {
            Format.JSON -> println(Json.encodeToString(coverage))
            Format.TEXT,
            null -> println(coverage.toString())
        }

        if (debug) {
            println("Execution time: $time ms")
        }
    }

    enum class Format { TEXT, JSON }
}
