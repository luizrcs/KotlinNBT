import org.jetbrains.kotlin.gradle.dsl.*

plugins {
	`kotlin-jvm`
	// maven
}

group = "br.com.luizrcs"
version = "1.1.0"

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	kotlin("stdlib-jdk8", kotlinVersion)
}

val compileKotlinOptions: KotlinJvmOptions.() -> Unit = {
	jvmTarget = "1.8"
	suppressWarnings = true
	freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.compileKotlin {
	kotlinOptions(compileKotlinOptions)
}

tasks.compileTestKotlin {
	kotlinOptions(compileKotlinOptions)
}
