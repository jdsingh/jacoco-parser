package dev.jagdeepsingh.config

import java.io.File

@JvmInline
value class JacocoReportDir(private val dir: String) {

    fun toFile(module: ModuleName): File {
        return File("$dir/$module/jacocoDebugReport/jacocoDebugReport.xml")
    }
}

class ReportDirConfigTransformer : Transformer<String?, JacocoReportDir> {

    override fun invoke(input: String?): JacocoReportDir {
        if (input == null) {
            throw IllegalArgumentException("jacocoReportDir config is not set in config.json")
        }

        return JacocoReportDir(dir = input.trim('"'))
    }
}