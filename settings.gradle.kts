val projectName: String by settings
rootProject.name = projectName

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.openjfx.javafxplugin") version "0.1.0"
    }

}