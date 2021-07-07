object ModuleVersions {
	const val kotlinxSerialization = "1.2.1"
}

plugins {
	kotlin("jvm")
	kotlin("plugin.serialization") version Versions.kotlin
	
	`sonatype-publish`
}

version = "1.0.0$suffix"

dependencies {
	implementation(project(":core"))
	
	kotlinx("serialization-core-jvm", ModuleVersions.kotlinxSerialization)
}