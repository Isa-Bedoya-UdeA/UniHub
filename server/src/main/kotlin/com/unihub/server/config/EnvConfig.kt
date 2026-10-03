package com.unihub.server.config

import java.io.File

object EnvConfig {
    fun get(key: String, defaultValue: String = ""): String {
        val sysEnv = System.getenv(key)
        if (!sysEnv.isNullOrBlank()) return sysEnv

        val potentialFiles = listOf(
            File(".env"),
            File("server/.env"),
            File("../server/.env"),
            File(System.getProperty("user.dir"), ".env"),
            File(System.getProperty("user.dir"), "server/.env")
        )

        for (file in potentialFiles) {
            if (file.exists() && file.isFile) {
                try {
                    file.useLines { lines ->
                        for (line in lines) {
                            val trimmed = line.trim()
                            if (trimmed.startsWith("$key=") && !trimmed.startsWith("#")) {
                                val value = trimmed.substringAfter("$key=").trim()
                                    .removeSurrounding("\"")
                                    .removeSurrounding("'")
                                if (value.isNotBlank()) return value
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore file read error and continue
                }
            }
        }
        return defaultValue
    }
}
