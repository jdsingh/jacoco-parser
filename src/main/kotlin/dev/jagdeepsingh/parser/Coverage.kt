package dev.jagdeepsingh.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coverage(
    val type: String,
    val covered: Long,
    val missed: Long,
    val total: Long,
    val coverage: Double,
    @SerialName("coverage_text") val coverageText: String
) {

    override fun toString(): String {
        return "Type: $type, Covered: $covered, Missed: $missed, Total: $total, Coverage: $coverageText"
    }
}

@Serializable
data class JacocoCoverage(
    @SerialName("module_name") val moduleName: String,
    val instruction: Coverage?,
    val branch: Coverage?,
    val line: Coverage?,
    val complexity: Coverage?,
    val method: Coverage?,
    @SerialName("class") val classCoverage: Coverage?
) {

    override fun toString(): String {
        return buildString {
            appendLine("Module: $moduleName")
            appendLine(instruction)
            appendLine(branch)
            appendLine(line)
            appendLine(complexity)
            appendLine(method)
            appendLine(classCoverage)
        }
    }
}

class JacocoCoverageTransformer {

    fun transform(moduleName: String, coverageList: List<Coverage>): JacocoCoverage {
        return JacocoCoverage(
            moduleName = moduleName,
            instruction = coverageList.find { it.type == "INSTRUCTION" },
            branch = coverageList.find { it.type == "BRANCH" },
            line = coverageList.find { it.type == "LINE" },
            complexity = coverageList.find { it.type == "COMPLEXITY" },
            method = coverageList.find { it.type == "METHOD" },
            classCoverage = coverageList.find { it.type == "CLASS" }
        )
    }
}
