package dev.jagdeepsingh.logger

interface Logger {
    fun logDebug(log: String)
    fun logInfo(log: String)
    fun logError(log: String)
    fun isDebug(): Boolean
    fun doIfDebug(block: Logger.() -> Unit)
}

enum class LogLevel {
    // Levels: 4, 3, 2, 1
    None, Error, Info, Debug
}

class DefaultLogger(private val level: LogLevel) : Logger {

    override fun isDebug(): Boolean {
        return level >= LogLevel.Debug
    }

    override fun doIfDebug(block: Logger.() -> Unit) {
        if (isDebug()) {
            block(this)
        }
    }

    override fun logDebug(log: String) {
        if (level >= LogLevel.Debug) {
            println("DEBUG: $log")
        }
    }

    override fun logInfo(log: String) {
        if (level >= LogLevel.Info) {
            println("INFO: $log")
        }
    }

    override fun logError(log: String) {
        if (level >= LogLevel.Error) {
            println("ERROR: $log")
        }
    }
}
