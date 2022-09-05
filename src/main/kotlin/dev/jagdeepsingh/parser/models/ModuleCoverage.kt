package dev.jagdeepsingh.parser.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ModuleCoverage(
    @SerialName("module_name") val moduleName: String,
    @SerialName("instruction") val instruction: Coverage,
    @SerialName("packages") val packages: List<PackageCoverage>
) {

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return buildString {
            appendLine("Module: $moduleName, Coverage: ${instruction.coverageInString}")
            if (packages.isNotEmpty()) {
                appendLine("Packages:")
                for (p in packages) {
                    appendLine("${p.packageName}, Coverage: ${p.instruction.coverageInString}")
                }
            }
        }
    }
}
