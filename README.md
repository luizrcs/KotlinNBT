[![Build Status](https://travis-ci.com/luizrcs/KotlinNBT.svg?branch=dev)](https://travis-ci.com/github/luizrcs/KotlinNBT)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.10-green)](https://kotlinlang.org/)
[![License: MIT](https://img.shields.io/github/license/luizrcs/KotlinNBT)](https://opensource.org/licenses/MIT)
[![Donation](https://img.shields.io/badge/donate-DonorBox-blue)](https://donorbox.org/luizrcs)

# KotlinNBT

Type-safe Named Binary Tags (NBT) implementation in Kotlin for the JVM for reading and writing files/streams with a
simple and concise builder DSL. Supports [kotlinx.serialization][kotlinx-serialization] with
the [nbt-serialization](/serialization) module.

This project is based on the original NBT specification by Notch ([Wayback Machine][WebArchive]) with the most recent
additions by Mojang ([Minecraft Wiki][Gamepedia] and [Wiki.vg](https://wiki.vg/NBT)) for the Java game version.

## Installation

All projects under the `br.com.luizrcs.nbt` namespace are available on [Maven Central][Maven Central] and
follow the [Semantic Versioning 2.0.0](https://semver.org/) specification, but keep the same *major* and *minor*
versions across all modules (except [nbt-cli](/cli)). This means that a version bump in one module doesn't necessarily
imply updates were made to it, but should be treated like so compatibility-wise.

The dependencies can be added to your project using [Gradle](https://gradle.org/):

### Groovy DSL

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'br.com.luizrcs.nbt:nbt-core:2.0.0'
    implementation 'br.com.luizrcs.nbt:nbt-serialization:2.0.0'
}
```

### Kotlin DSL

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("br.com.luizrcs.nbt:nbt-core:2.0.0")
    implementation("br.com.luizrcs.nbt:nbt-serialization:2.0.0")
}
```

## Usage

### NbtIO

The object `NbtIO` is used similarly to `javax.imageio.ImageIO` to read/write an `NbtCompound`. The available
compression methods are: `GZIP`, `ZLIB` and `NONE` (no compression).

#### Reading from file

```kotlin
val file = File("test.nbt")
val compression = NbtIO.Compression.GZIP

val nbt = NbtIO.read(file, compression)
```

#### Writing to file

```kotlin
val file = File("test.nbt")
val compression = NbtIO.Compression.GZIP

NbtIO.write(nbt, file, compression)
```

### Builder DSL

The `buildNbt` function is used to create an `NbtCompound` with a simple and concise builder DSL
(based on the way [kotlinx-serialization-json][kotlinx-serialization-json] does it):

```kotlin
val nbt = buildNbt("root") {
    putNbtCompound("testCompound") {
        put("fibonacci", intArrayOf(1, 1, 2, 3, 5, 8, 13, 21))
    }
    putNbtList("testList") {
        addNbtCompound {
            put("firstString", "I'm the first String :)")
            put("secondString", "I'm the second String, but order is not guaranteed :/")
            put("justAnInteger", 1)
        }
    }
    put("timestamp", System.currentTimeMillis())
}
```

Which can be printed as a Kotlin-styled tree:

```kotlin
root: Compound = {
    testCompound: Compound = {
        fibonacci: IntArray = [1, 1, 2, 3, 5, 8, 13, 21]
    },
    testList: List<Compound> = [
        Compound = {
            firstString: String = "I'm the first String :)",
            justAnInteger: Int = 1,
            secondString: String = "I'm the second String, but order is not guaranteed :/"
        }
    ],
    timestamp: Long = 1605380400000L
}
```

**Important:** according to the NBT specification, the order of the displayed/read tags is not guaranteed. KotlinNBT
prints an `NbtCompound` using a schema based on the way [NBTExplorer][NBTExplorer] does it.

### Type safety

In the example above, the typed values can be obtained with type-safe accessors:

```kotlin
val fibonacci: IntArray = nbt.compound["testCompound"].compound["fibonacci"].intArray
val message: String = nbt.compound["testList"].list[0].compound["firstString"].string
val timestamp: Long = nbt.compound["timestamp"].long

println(fibonacci.toList())
println(message)
println(timestamp)
``` 

prints

```
[1, 1, 2, 3, 5, 8, 13, 21]
I'm the first String :)
1605380400000
```

To check the tag types before accessing them (whether they exist or not), properties `isNbtX` can be used, where `X` is
a tag type:

```kotlin
println(nbt["testCompound"].isNbtCompound)
println(nbt["timestamp"].isNbtFloat)
println(nbt["world"].isNbtString)
```

prints

```
true
false
false
```

### Cloning tags

The provided `NbtAny.clone()` functions deep copy the tag (that is, the tag itself and its children are cloned
recursively) while keeping the type safety:

```kotlin
nbt.clone() // to keep the same name, "root"
nbt.clone("rootClone") // to change the root tag name
nbt["testList"]?.clone("actualList")
```

### Serialization

KotlinNBT supports the [kotlinx-serialization][kotlinx-serialization] library to work with NBT and serializable classes.

> See [nbt-serialization](/serialization) module.

### Converters

KotlinNBT provides a way to extend its functionality and create converters for NBT to/from other formats, such as JSON
and SNBT (Stringified NBT).

#### JSON

> See [nbt-converter-json](/converter-json) module.

#### SNBT

> See [nbt-converter-snbt](/converter-snbt) module.

## Donate

KotlinNBT is free and open-source for everyone to enjoy ❤️

If you wish to support the continuous development of this and other projects, you
can [donate](https://donorbox.org/luizrcs)!

[WebArchive]: https://web.archive.org/web/20100124085747/http://www.minecraft.net/docs/NBT.txt

[Gamepedia]: https://minecraft.fandom.com/wiki/NBT_format

[Maven Central]: https://central.sonatype.com/namespace/br.com.luizrcs.nbt

[kotlinx-serialization]: https://github.com/Kotlin/kotlinx.serialization

[kotlinx-serialization-json]: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#json-element-builders

[NBTExplorer]: https://github.com/jaquadro/NBTExplorer