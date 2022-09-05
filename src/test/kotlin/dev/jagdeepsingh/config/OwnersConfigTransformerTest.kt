package dev.jagdeepsingh.config

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class OwnersConfigTransformerTest {

    private val transformer = OwnersConfigTransformer()

    @Test
    fun invokeShouldReturnReversedMap() {
        val owners = mapOf(
            "team-1" to "[\"booking\",\"payments\"]",
            "team-2" to "[\"orders\",\"homepage\"]"
        )

        val actualOutput: OwnersConfig = transformer.invoke(owners)

        val expectedOutput: OwnersConfig = mapOf(
            PackageName("booking") to OwnerName("team-1"),
            PackageName("payments") to OwnerName("team-1"),
            PackageName("orders") to OwnerName("team-2"),
            PackageName("homepage") to OwnerName("team-2"),
        )
        assertEquals(expectedOutput, actualOutput)
    }
}
