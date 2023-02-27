import org.gradle.api.*

val Project.isSnapshot get() = properties["snapshot"]?.toString()?.toBoolean() ?: true
val Project.suffix get() = if (isSnapshot) "-SNAPSHOT" else ""