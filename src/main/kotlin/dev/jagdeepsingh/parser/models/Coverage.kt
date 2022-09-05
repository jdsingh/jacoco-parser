package dev.jagdeepsingh.parser.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coverage(
    @SerialName("type") val type: String,
    @SerialName("covered") val covered: Long,
    @SerialName("missed") val missed: Long,
    @SerialName("total") val total: Long,
    @SerialName("coverage") val coverage: Double,
    @SerialName("coverage_text") val coverageInString: String
)
