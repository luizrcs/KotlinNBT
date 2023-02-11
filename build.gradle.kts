plugins {
	kotlinJvm apply false
}

subprojects {
	group = "br.com.luizrcs.nbt"
	
	repositories {
		mavenCentral()
		mavenLocal()
	}
	
	dependencies {
		kotlin("stdlib", Versions.kotlin)
	}
	
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			apiVersion = "1.8"
			languageVersion = "1.8"
			
			suppressWarnings = true
			freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
		}
	}
}