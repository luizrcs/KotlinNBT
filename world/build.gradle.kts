plugins {
	kotlinJvm
	kotlinSerialization
	`sonatype-publish`
}

version = "1.0.0$suffix"

dependencies {
	implementation(project(":core"))
	implementation(project(":serialization"))
}