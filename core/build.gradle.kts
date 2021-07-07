object ModuleVersions {
	const val kotlinxCollections = "0.3.4"
}

plugins {
	kotlin("jvm")
	
	`sonatype-publish`
}

version = "2.0.0$suffix"

dependencies {
	kotlinx("collections-immutable-jvm", ModuleVersions.kotlinxCollections)
}