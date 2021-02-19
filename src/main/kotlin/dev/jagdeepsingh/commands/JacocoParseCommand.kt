package dev.jagdeepsingh.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.NodeChild
import java.io.File
import java.text.DecimalFormat

class JacocoParseCommand : CliktCommand(name = "jacoco-report-parser") {

    private val debug by option("--debug", help = "Enable debug logging").flag()

    private val report by argument("report.xml")
        .file(mustExist = true, canBeFile = true)
        .validate {
            require(it.extension == "xml") {
                "report should be a xml file"
            }
        }

    override fun run() {
        println("\nReady\n")

        val inputStream = report.inputStream()

        val slurper = XmlSlurper()
        slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

        val xml: GPathResult = slurper.parse(inputStream)

        @Suppress("UNCHECKED_CAST")
        val nodes: List<NodeChild> = xml.children()
            .filter {
                it as NodeChild
                it.name() == "counter"
            } as List<NodeChild>

        nodes.forEach { printNode(it) }

        println("\nFinish\n")
    }

    private fun printNode(node: NodeChild) {
        @Suppress("UNCHECKED_CAST")
        val attributes: Map<String, Any> = node.attributes() as Map<String, Any>
        val type = node.attributes()["type"]
        val missed: Double = (attributes["missed"] as String).toDouble()
        val covered: Double = (attributes["covered"] as String).toDouble()
        val total: Double = missed + covered
        val percentage: Double = (covered / total * 100)
        val coverage = DecimalFormat("##.##").format(percentage)
        println("${type}: Covered: $covered Missed: $missed Total: $total Coverage: $coverage")
    }

    private fun currentFolder(): File {
        return File("").absoluteFile
    }

    private fun File.contents(): List<File> {
        return this.listFiles().toList()
    }

    private fun File.files(): List<File> {
        return this.contents().filter { it.isFile }
    }
}
