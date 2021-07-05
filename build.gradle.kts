import org.jetbrains.kotlin.gradle.dsl.*

plugins {
	`kotlin-jvm`
}

group = "io.github.mrpng"
version = "1.0.0"

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation(kotlin("stdlib", kotlinVersion))
}

val compileKotlinOptions: KotlinJvmOptions.() -> Unit = {
	jvmTarget = "1.8"
	suppressWarnings = true
}

tasks.compileKotlin {
	kotlinOptions(compileKotlinOptions)
}

tasks.compileTestKotlin {
	kotlinOptions(compileKotlinOptions)
}
