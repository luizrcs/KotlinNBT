object ModuleVersions {
	const val kotlinxCli = "0.3.2"
}

plugins {
	kotlin("jvm")
	
	application
	shadowJar
	`sonatype-publish`
}

version = "1.0.0$suffix"

application.mainClass.set("$group.nbt.cli.MainKt")

dependencies {
	implementation(project(":core"))
	
	kotlinx("cli", ModuleVersions.kotlinxCli)
}

tasks.shadowJar {
	archiveClassifier.set("")
	archiveVersion.set("")
}