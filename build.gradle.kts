plugins {
	java
	kotlin("jvm") version "1.5.10"
}

group = "io.github.mrpng"
version = "1.0.0"

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	implementation(kotlin("stdlib"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
	useJUnitPlatform()
}