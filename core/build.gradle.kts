object ModuleVersions {
	const val kotlinxCollections = "0.3.5"
}

plugins {
	kotlinJvm
	`sonatype-publish`
}

dependencies {
	kotlinx("collections-immutable-jvm", ModuleVersions.kotlinxCollections)
}