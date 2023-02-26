object ModuleVersions {
	const val kotlinxCollections = "0.3.5"
}

plugins {
	kotlinJvm
	`sonatype-publish`
}

version = "${rootProject.version}$suffix"

dependencies {
	kotlinx("collections-immutable-jvm", ModuleVersions.kotlinxCollections)
}