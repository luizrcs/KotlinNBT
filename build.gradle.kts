import org.jetbrains.kotlin.gradle.dsl.*

plugins {
	`kotlin-jvm`
}

group = "me.mrpingu"
version = "1.0-SNAPSHOT"

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
