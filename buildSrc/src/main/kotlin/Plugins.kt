import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

inline val PluginDependenciesSpec.`kotlin-jvm` get() = kotlin("jvm") version kotlinVersion
