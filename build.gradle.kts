plugins {
	kotlinJvm apply false
}

version = "2.0.0"

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
	
	tasks.withType<JavaCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
	}
}

fun Task.dependsOnProject(project: Project, taskName: String) {
	project.tasks.findByName(taskName)?.let { task ->
		dependsOn(task)
	}
}

tasks.register("publishAllToMavenLocal") {
	group = "publishing"
	subprojects.forEach { dependsOnProject(it, "publishToMavenLocal") }
}

tasks.register("publishAll") {
	group = "publishing"
	subprojects.forEach { dependsOnProject(it, "publish") }
}