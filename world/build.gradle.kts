plugins {
	kotlin("jvm")
	kotlin("plugin.serialization") version Versions.kotlin
	
	`sonatype-publish`
}

version = "1.0.0$suffix"

dependencies {
	implementation(project(":core"))
	implementation(project(":serialization"))
}