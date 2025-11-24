val jvmTargetVersion: String by project
val projectGroup: String by project
val projectVersion: String by project
val javaFxVersion: String by project
val mainClassName = "ru.vichukano.spicerate.gui.App"

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
    implementation("org.openjfx:javafx-controls:$javaFxVersion")
    implementation("org.openjfx:javafx-fxml:$javaFxVersion")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2:2.2.224")
}

application {
    mainClass.set(mainClassName)
}

kotlin {
    jvmToolchain(jvmTargetVersion.toInt())
}

javafx {
    version = javaFxVersion
    modules("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = mainClassName
    }
    archiveBaseName.set(project.name)
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}
