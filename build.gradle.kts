plugins {
    kotlin("jvm") version "2.0.0" // Use the latest stable version of Kotlin
    id("org.openjfx.javafxplugin") version "0.1.0" // JavaFX plugin
    `maven-publish`
}

group = "io.github.talkarcabbage"
version = "0.17.2"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("io.github.talkarcabbage:talkarlogger:0.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

javafx {
    version = "22.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.talkarcabbage.rstimer.FXController"
    }

    // This block includes all dependencies in the JAR
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    // Ensure CSS and image files are included
    from("src/css") {
        into("css")
    }
    from("src/images") {
        into("images")
    }
    from("src/fxml") {
        into("fxml")
    }

    // Avoid duplicate files in the JAR
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}