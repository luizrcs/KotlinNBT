import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.*

inline val PluginDependenciesSpec.shadowJar get() = id("com.github.johnrengelman.shadow") version Versions.shadowJar