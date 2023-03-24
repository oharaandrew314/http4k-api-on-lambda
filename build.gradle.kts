plugins {
    kotlin("jvm") version "1.8.10"
    id("com.google.devtools.ksp") version "1.8.10-1.0.9"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11) // Java 11 is the latest runtime available on AWS Lambda
}

val kotshiVersion = "2.10.2"

dependencies {
    implementation(platform("org.http4k:http4k-bom:4.41.0.0"))
    implementation(platform("org.http4k:http4k-connect-bom:3.35.0.0"))

    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("org.http4k:http4k-connect-amazon-dynamodb")
    implementation("se.ansman.kotshi:api:$kotshiVersion")
    ksp("se.ansman.kotshi:compiler:$kotshiVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("org.http4k:http4k-connect-amazon-dynamodb-fake")
}

tasks.test {
    useJUnitPlatform()
}