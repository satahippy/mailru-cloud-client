package com.github.satahippy.mailru

import java.io.File
import java.util.Properties

object Settings {

    private val properties = Properties()

    init {
        load()
    }

    fun username(): String {
        return properties["username"].toString()
    }

    fun password(): String {
        return properties["password"].toString()
    }

    private fun load() {
        val configPath = "config.properties"
        val configResource = this.javaClass.classLoader.getResource(configPath)
        if (configResource == null) {
            throw RuntimeException("Cannot find $configPath")
        }
        val configFile = File(configResource.file)
        if (!configFile.canRead()) {
            throw RuntimeException("Cannot find $configPath")
        }
        properties.load(configFile.inputStream())
    }
}