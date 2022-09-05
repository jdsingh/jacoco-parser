package dev.jagdeepsingh.parser

import dev.jagdeepsingh.parser.models.Coverage
import dev.jagdeepsingh.parser.models.CoverageFactory
import dev.jagdeepsingh.parser.models.CoverageFactory.Companion.ATTRIBUTE_TYPE
import dev.jagdeepsingh.parser.models.ModuleCoverage
import dev.jagdeepsingh.parser.models.PackageCoverage
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.NodeChild
import java.io.File
import java.text.DecimalFormat

class JacocoParser {

    private val slurper = XmlSlurper()

    init {
        slurper.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        slurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    fun parse(file: File): ModuleCoverage {
        val formatter = DecimalFormat("##.##")
        val coverageFactory = CoverageFactory(formatter = formatter)
        val transformer = JacocoCoverageTransformer()

        val result: GPathResult = slurper.parse(file)
        val moduleName = (result as NodeChild).attributes()[ATTRIBUTE_NAME] as String
        val coverageList = mutableListOf<Coverage>()
        val packageCoverage = mutableListOf<PackageCoverage>()

        result.children()
            .map { it as NodeChild }
            .filter { node ->
                val name = node.name()
                val isCounter = name == NODE_NAME_COUNTER
                val isPackage = name == NODE_NAME_PACKAGE

                isCounter || isPackage
            }
            .map { node ->
                if (node.name() == NODE_NAME_PACKAGE) {
                    val packageName = node.attributes()[ATTRIBUTE_NAME] as String
                    node.children()
                        .map { it as NodeChild }
                        .filter { it.name() == NODE_NAME_COUNTER }
                        .filter { it.attributes()[ATTRIBUTE_TYPE] == "INSTRUCTION" }
                        .mapTo(packageCoverage) {
                            PackageCoverage(
                                packageName = packageName.replace("/", "."),
                                instruction = coverageFactory.from(it)
                            )
                        }
                } else if (node.name() == NODE_NAME_COUNTER) {
                    coverageList.add(coverageFactory.from(node))
                }
                node
            }

        return transformer.transform(
            moduleName = moduleName,
            coverageList = coverageList,
            packages = packageCoverage
        )
    }

    companion object {
        private const val NODE_NAME_COUNTER = "counter"
        private const val NODE_NAME_PACKAGE = "package"
        private const val ATTRIBUTE_NAME = "name"
    }
}
