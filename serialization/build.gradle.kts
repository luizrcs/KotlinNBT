plugins {
	kotlinJvm
	kotlinSerialization
	`sonatype-publish`
}

dependencies {
	implementation(project(":core"))
	kotlinx("serialization-core-jvm", Versions.kotlinxSerialization)
	
	testImplementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.kotlinxSerialization)
}