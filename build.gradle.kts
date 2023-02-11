plugins {
	kotlin("jvm") version Versions.kotlin apply false
}

subprojects {
	group = "br.com.luizrcs.nbt"
	
	repositories {
		mavenCentral()
		mavenLocal()
	}
	
	dependencies {
		kotlin("stdlib-jdk8", Versions.kotlin)
	}
	
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			apiVersion = "1.7"
			languageVersion = "1.7"
			
			suppressWarnings = true
			freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
		}
	}
}