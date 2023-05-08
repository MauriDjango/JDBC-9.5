import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.usuario"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.zaxxer:HikariCP:5.0.1")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP

    implementation("org.slf4j:slf4j-simple:2.0.7")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple

    implementation("com.h2database:h2:2.1.214")
    // https://mvnrepository.com/artifact/com.h2database/h2
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}