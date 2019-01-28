import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask
import org.gradle.jvm.tasks.Jar

group = "com.github.marcbenedi"

plugins {
    kotlin("jvm") version "1.3.11"
    id("org.jetbrains.dokka") version "0.9.17"
    maven
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit:junit:4.12")
}

repositories {
    mavenCentral()
    jcenter()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(dokka)
}