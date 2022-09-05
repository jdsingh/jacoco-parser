package dev.jagdeepsingh.parser.models

import groovy.xml.slurpersupport.NodeChild
import java.text.DecimalFormat

class CoverageFactory(private val formatter: DecimalFormat) {

    @Suppress("UNCHECKED_CAST")
    fun from(nodeChild: NodeChild): Coverage {
        val attributes: Map<String, Any> = nodeChild.attributes() as Map<String, Any>
        val type = attributes[ATTRIBUTE_TYPE] as String
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
            coverageInString = formatter.format(percentage)
        )
    }

    companion object {
        const val ATTRIBUTE_TYPE = "type"
        private const val ATTRIBUTE_MISSED = "missed"
        private const val ATTRIBUTE_COVERED = "covered"
    }
}
