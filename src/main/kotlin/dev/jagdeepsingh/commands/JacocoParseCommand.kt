package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import dev.jagdeepsingh.parser.CoverageReport
import dev.jagdeepsingh.parser.JacocoParser
import kotlin.system.measureTimeMillis

class JacocoParseCommand : CliktCommand(
    name = "jacoco-report-parser",
    printHelpOnEmptyArgs = true,
    help = """
        Parse JaCoCc xml coverage report.
    """.trimIndent()
) {

    private val debug by option(
        "--debug",
        help = "Enable debug logging"
    ).flag()

    private val report by argument(name = "report.xml")
        .file(mustExist = true, canBeFile = true)
        .validate {
            require(it.extension == "xml") {
                "report should be a xml file"
            }
        }

    override fun run() {
        val parser = JacocoParser()

        val coverage: CoverageReport
        val time = measureTimeMillis {
            coverage = parser.parse(report)
        }

        println(coverage.instruction())

        if (debug) {
            println("Execution time: $time ms")
        }
    }
}
