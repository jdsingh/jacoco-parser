package dev.jagdeepsingh.parser

class JacocoCoverageTransformer {

    fun transform(moduleName: String, coverageList: List<Coverage>): ModuleCoverage {
        return ModuleCoverage(
            moduleName = moduleName,
            instruction = coverageList.first { it.type == "INSTRUCTION" },
            branch = coverageList.first { it.type == "BRANCH" },
            line = coverageList.first { it.type == "LINE" },
            complexity = coverageList.first { it.type == "COMPLEXITY" },
            method = coverageList.first { it.type == "METHOD" },
            classCoverage = coverageList.first { it.type == "CLASS" }
        )
    }
}
