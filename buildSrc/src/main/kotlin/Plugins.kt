import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.*

inline val PluginDependenciesSpec.kotlinJvm get() = kotlin("jvm") version Versions.kotlin
inline val PluginDependenciesSpec.kotlinSerialization get() = kotlin("plugin.serialization") version Versions.kotlin
inline val PluginDependenciesSpec.shadowJar get() = id("com.github.johnrengelman.shadow") version Versions.shadowJar