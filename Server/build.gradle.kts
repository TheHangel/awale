plugins {
    java
    application
}

group = "etu.ensicaen.server"
version = "1.0-SNAPSHOT"

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

application {
   mainClass.set("etu.ensicaen.server.Main")
}

dependencies {
    implementation("etu.ensicaen.shared:Shared")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Jackson is needed here because ChatMessage will be serialized/deserialized
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

}

tasks.test {
    useJUnitPlatform()
}