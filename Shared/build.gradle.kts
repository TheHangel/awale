plugins {
    id("java")
}

group = "etu.ensicaen.shared"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Jackson is needed here because ChatMessage will be serialized/deserialized
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")

}

tasks.test {
    useJUnitPlatform()
}