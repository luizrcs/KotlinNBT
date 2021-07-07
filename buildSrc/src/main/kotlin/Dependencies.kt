import org.gradle.api.artifacts.dsl.*

fun DependencyHandler.kotlinx(name: String, version: String = Versions.kotlin) =
	add("implementation", "org.jetbrains.kotlinx:kotlinx-$name:$version")