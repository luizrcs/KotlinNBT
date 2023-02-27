plugins {
	`java-library`
	`maven-publish`
	signing
}

tasks.create<Jar>("javadocJar") {
	archiveClassifier.set("javadoc")
	from(tasks.javadoc)
}

tasks.create<Jar>("sourcesJar") {
	archiveClassifier.set("sources")
	from(sourceSets["main"].allSource)
}

publishing {
	publications {
		create<MavenPublication>(project.name) {
			artifactId = "nbt-" + project.name
			
			val authorId = properties["authorId"].toString()
			val authorName = properties["authorName"].toString()
			val authorEmail = properties["authorEmail"].toString()
			
			pom {
				name.set("${rootProject.name}: ${project.name.capitalize()}")
				url.set("https://github.com/$authorId/${rootProject.name}")
				
				developers {
					developer {
						id.set(authorId)
						name.set(authorName)
						email.set(authorEmail)
					}
				}
				
				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}
				
				scm {
					connection.set("scm:git:git://github.com/$authorId/${rootProject.name}.git")
					url.set("https://github.com/$authorId/${rootProject.name}")
				}
			}
			
			artifact(tasks["javadocJar"])
			artifact(tasks["sourcesJar"])
			
			from(components["kotlin"])
		}
	}
	
	repositories {
		maven {
			val releasesRepository = "https://s01.oss.sonatype.org/content/repositories/releases/"
			val snapshotsRepository = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
			
			url = if (project.isSnapshot) uri(snapshotsRepository) else uri(releasesRepository)
			
			credentials {
				username = properties["ossrhUsername"].toString()
				password = properties["ossrhPassword"].toString()
			}
		}
	}
}

signing {
	sign(publishing.publications[project.name])
}