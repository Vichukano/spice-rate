val jvmTargetVersion: String by project
val projectGroup: String by project
val projectVersion: String by project

plugins {
    kotlin("jvm")
    id("org.openjfx.javafxplugin")
    application
}

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("ch.obermuhlner:big-math:2.3.2")
    implementation("org.openjfx:javafx-controls:20.0.2")
    implementation("org.openjfx:javafx-fxml:20.0.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("ru.vichukano.spicerate.gui.App")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jvmTargetVersion.toInt())
}

javafx {
    version = "21.0.2"
    modules("javafx.controls", "javafx.fxml")
}
