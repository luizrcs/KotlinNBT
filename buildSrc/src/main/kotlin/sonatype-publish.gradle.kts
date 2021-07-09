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
			
			pom {
				name.set("${rootProject.name} ${project.name.capitalize()}")
				url.set("https://github.com/luizrcs/${rootProject.name}")
				
				developers {
					developer {
						id.set("luizrcs")
						name.set("Luiz Rodrigo")
						email.set("luizrodrigo40@gmail.com")
					}
				}
				
				licenses {
					license {
						name.set("MIT License")
						url.set("https://opensource.org/licenses/MIT")
					}
				}
				
				scm {
					connection.set("scm:git:git://github.com/luizrcs/${rootProject.name}.git")
					url.set("https://github.com/luizrcs/${rootProject.name}")
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