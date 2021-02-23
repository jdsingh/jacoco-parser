package dev.jagdeepsingh.parser

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.NodeChild
import java.io.File
import java.text.DecimalFormat

class JacocoParser {

    private val slurper = XmlSlurper()

    init {
        slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    fun parse(file: File): JacocoCoverage {
        val result: GPathResult = slurper.parse(file)
        val formatter = DecimalFormat("##.##")
        val transformer = JacocoCoverageTransformer()

        val moduleName = (result as NodeChild).attributes()[ATTRIBUTE_NAME] as String
        val coverageList = mutableListOf<Coverage>()

        result.children()
            .filter {
                it as NodeChild
                it.name() == NODE_NAME_COUNTER
            }.mapTo(coverageList) {
                it as NodeChild
                it.toCoverageReport(formatter)
            }

        return transformer.transform(moduleName = moduleName, coverageList = coverageList)
    }

    @Suppress("UNCHECKED_CAST")
    private fun NodeChild.toCoverageReport(formatter: DecimalFormat): Coverage {
        val attributes: Map<String, Any> = attributes() as Map<String, Any>
        val type = attributes()[ATTRIBUTE_TYPE] as String
        val missed: Double = (attributes[ATTRIBUTE_MISSED] as String).toDouble()
        val covered: Double = (attributes[ATTRIBUTE_COVERED] as String).toDouble()
        val total: Double = missed + covered
        val percentage: Double = covered / total * 100

        return Coverage(
            type = type,
            missed = missed.toLong(),
            covered = covered.toLong(),
            total = total.toLong(),
            coverage = percentage,
            coverageText = formatter.format(percentage)
        )
    }

    companion object {
        private const val NODE_NAME_COUNTER = "counter"
        private const val ATTRIBUTE_TYPE = "type"
        private const val ATTRIBUTE_MISSED = "missed"
        private const val ATTRIBUTE_COVERED = "covered"
        private const val ATTRIBUTE_NAME = "name"
    }
}
