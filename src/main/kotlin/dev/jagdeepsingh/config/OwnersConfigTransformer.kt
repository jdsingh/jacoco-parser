package dev.jagdeepsingh.config

@JvmInline
value class OwnerName(private val name: String) {
    override fun toString(): String {
        return name
    }
}

@JvmInline
value class PackageName(private val name: String) {
    override fun toString(): String {
        return name
    }
}

typealias RawOwnersConfig = Map<String, String>
typealias OwnersConfig = Map<PackageName, OwnerName>

/**
 * Creates map of package to owner.
 *
 * Example config:
 * ```
 * "owners": {
 *      "team-1": [
 *          "booking",
 *          "payments"
 *      ],
 *      "team-2": [
 *          "orders",
 *          "home-page"
 *      ]
 *  }
 * ```
 *
 * Map entries:
 * ```
 * {
 *      "booking": "team-1",
 *      "payments": "team-1",
 *      "orders": "team-2"
 *      "home-page": "team-2"
 * }
 */
class OwnersConfigTransformer : Transformer<RawOwnersConfig, OwnersConfig> {

    override fun invoke(input: RawOwnersConfig): OwnersConfig {
        val map = mutableMapOf<PackageName, OwnerName>()
        for (owner in input) {
            val ownerName = OwnerName(owner.key)
            val packages: List<String> = owner.value
                .trimStart('[')
                .trimEnd(']')
                .split(",")
                .map { it.trim('"') }

            for (packageName in packages) {
                map[PackageName(packageName)] = ownerName
            }
        }
        return map
    }
}
