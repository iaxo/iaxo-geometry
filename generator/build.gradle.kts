import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.lobis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // mavenLocal()
}

dependencies {
    testImplementation(kotlin("test"))
    // implementation("space.kscience:gdml:0.4.0")
    // implementation("local:gdml-jvm:0.4.0-dev") // this has to match the local published version

    implementation("space.kscience:gdml") {
        version {
            branch = "dev"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}