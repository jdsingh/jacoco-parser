package dev.jagdeepsingh.parser

import java.text.DecimalFormat

data class Coverage(
    val type: String,
    val covered: Long,
    val missed: Long,
    val total: Long,
    val coverage: Double
) {

    override fun toString(): String {
        val coverage = DecimalFormat("##.##").format(coverage)
        return "$type - Covered: $covered, Missed: $missed, Total: $total, Coverage: $coverage"
    }
}

class CoverageReport : ArrayList<Coverage>() {

    fun instruction(): Coverage? {
        return find { it.type == "INSTRUCTION" }
    }

    override fun toString(): String {
        return joinToString("\n")
    }
}
