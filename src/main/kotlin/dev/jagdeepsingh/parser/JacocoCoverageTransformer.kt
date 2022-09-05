package dev.jagdeepsingh.parser

import dev.jagdeepsingh.parser.models.Coverage
import dev.jagdeepsingh.parser.models.ModuleCoverage
import dev.jagdeepsingh.parser.models.PackageCoverage

class JacocoCoverageTransformer {

    fun transform(
        moduleName: String,
        coverageList: List<Coverage>,
        packages: List<PackageCoverage>
    ): ModuleCoverage {
        return ModuleCoverage(
            moduleName = moduleName,
            instruction = coverageList.first { it.type == "INSTRUCTION" },
            packages = packages
        )
    }
}
