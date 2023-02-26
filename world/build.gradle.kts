plugins {
	kotlinJvm
	kotlinSerialization
	`sonatype-publish`
}

version = "${rootProject.version}$suffix"

dependencies {
	implementation(project(":core"))
	implementation(project(":serialization"))
}