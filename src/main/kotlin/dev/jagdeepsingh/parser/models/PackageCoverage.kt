package dev.jagdeepsingh.parser.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PackageCoverage(
    @SerialName("package_name") val packageName: String,
    @SerialName("instruction") val instruction: Coverage
) {

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    override fun toString(): String {
        return buildString {
            appendLine("Package: $packageName")
            appendLine(instruction)
        }
    }
}
