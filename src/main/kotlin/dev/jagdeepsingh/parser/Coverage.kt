package dev.jagdeepsingh.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Coverage(
    @SerialName("type") val type: String,
    @SerialName("covered") val covered: Long,
    @SerialName("missed") val missed: Long,
    @SerialName("total") val total: Long,
    @SerialName("coverage") val coverage: Double,
    @SerialName("coverage_text") val coverageText: String
) {

    override fun toString(): String {
        return "Type: $type, Covered: $covered, Missed: $missed, Total: $total, Coverage: $coverageText"
    }
}

@Serializable
data class ModuleCoverage(
    @SerialName("module_name") val moduleName: String,
    @SerialName("instruction") val instruction: Coverage,
    @SerialName("branch") val branch: Coverage,
    @SerialName("line") val line: Coverage,
    @SerialName("complexity") val complexity: Coverage,
    @SerialName("method") val method: Coverage,
    @SerialName("class") val classCoverage: Coverage
) {

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    fun toShort() = ModuleCoverageShort(
        module = moduleName,
        type = instruction.type,
        coverage = instruction.coverageText
    )

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

@Serializable
data class ModuleCoverageShort(
    @SerialName("module_name") val module: String,
    @SerialName("type") val type: String,
    @SerialName("coverage") val coverage: String,
) {

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return buildString {
            append("Module: $module, Type: $type, Coverage: $coverage")
        }
    }
}
