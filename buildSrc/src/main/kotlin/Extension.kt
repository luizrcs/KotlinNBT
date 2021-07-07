import org.gradle.api.*

val Project.isSnapshot get() = properties["snapshot"].toString().toBoolean()
val Project.suffix get() = if (isSnapshot) "-SNAPSHOT" else ""