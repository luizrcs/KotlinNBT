import org.jetbrains.kotlin.gradle.dsl.*

plugins {
	`kotlin-jvm`
	maven
}

group = "io.github.mrpng"
version = "1.0.0"

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
	implementation(kotlin("stdlib-jdk8", kotlinVersion))
}

val compileKotlinOptions: KotlinJvmOptions.() -> Unit = {
	jvmTarget = "1.8"
	languageVersion = "1.4"
	
	suppressWarnings = true
}

tasks.compileKotlin {
	kotlinOptions(compileKotlinOptions)
}

tasks.compileTestKotlin {
	kotlinOptions(compileKotlinOptions)
}
