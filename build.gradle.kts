plugins {
	kotlin("jvm") version Versions.kotlin apply false
}

subprojects {
	group = "br.com.luizrcs"
	
	repositories {
		mavenCentral()
		mavenLocal()
	}
	
	dependencies {
		kotlin("stdlib-jdk8", Versions.kotlin)
	}
	
	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
			suppressWarnings = true
			freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
		}
	}
}