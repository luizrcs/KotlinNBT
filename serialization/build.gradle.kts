object ModuleVersions {
	const val kotlinxSerialization = "1.5.0"
}

plugins {
	kotlinJvm
	kotlin("plugin.serialization") version Versions.kotlin
	`sonatype-publish`
}

version = "${rootProject.version}$suffix"

dependencies {
	implementation(project(":core"))
	kotlinx("serialization-core-jvm", ModuleVersions.kotlinxSerialization)
	
	testImplementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", ModuleVersions.kotlinxSerialization)
}