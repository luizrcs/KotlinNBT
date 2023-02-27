plugins {
	kotlinJvm
	kotlinSerialization
	`sonatype-publish`
}

dependencies {
	implementation(project(":core"))
	implementation(project(":serialization"))
	kotlinx("serialization-core-jvm", Versions.kotlinxSerialization)
	kotlinx("serialization-json", Versions.kotlinxSerialization)
}