plugins {
    kotlin("jvm") version "1.9.20"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "com.poisonedyouth.http4k-introduction"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val httpV4kVersion = project.properties["http4kVersion"]
val log4JVersion = project.properties["log4JVersion"]
val exposedVersion = project.properties["exposedVersion"]
val h2Version = project.properties["h2Version"]
val hikariVersion = project.properties["hikariVersion"]
val koinVersion = project.properties["koinVersion"]
val jdkVersion = project.properties["jdkVersion"]
val junit5Version = project.properties["junit5Version"]
val akkurateVersion = project.properties["akkurateVersion"]
val arrowVersion = project.properties["arrowVersion"]

dependencies {
    // http4k
    implementation(platform("org.http4k:http4k-bom:$httpV4kVersion"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-format-jackson")
    // koin
    implementation("io.insert-koin:koin-core-jvm:$koinVersion")
    // logging
    implementation("org.apache.logging.log4j:log4j-core:$log4JVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4JVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4JVersion")
    // exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    // h2
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    // akkurate
    implementation("dev.nesk.akkurate:akkurate-core:$akkurateVersion")
    implementation("dev.nesk.akkurate:akkurate-ksp-plugin:$akkurateVersion")
    ksp("dev.nesk.akkurate:akkurate-ksp-plugin:$akkurateVersion")
    // arrow
    implementation(platform("io.arrow-kt:arrow-stack:$arrowVersion"))
    implementation("io.arrow-kt:arrow-core")
    // testing
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("io.insert-koin:koin-test-jvm:$koinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(jdkVersion.toString().toInt())
}

ktlint {
    version = "1.0.1"

    ignoreFailures = true
}
