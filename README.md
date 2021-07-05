[![Build Status](https://travis-ci.com/luizrcs/KotlinNBT.svg?branch=master)](https://travis-ci.com/luizrcs/KotlinNBT)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.20-green)](https://kotlinlang.org/)
[![JitPack](https://jitpack.io/v/luizrcs/KotlinNBT.svg)](https://jitpack.io/#luizrcs/KotlinNBT)
[![License: MIT](https://img.shields.io/github/license/luizrcs/KotlinNBT)](https://opensource.org/licenses/MIT)
[![Donation](https://img.shields.io/badge/donate-DonorBox-blue)](https://donorbox.org/luizrcs)


# KotlinNBT

Type-safe Named Binary Tags (NBT) implementation in Kotlin for the JVM for reading and writing files/streams with a simple and
concise builder API.

This project is based on the original NBT specification by Notch ([Wayback Machine][WebArchive]) with the most recent
additions by Mojang ([Minecraft Wiki][Gamepedia] and [Wiki.vg][WikiVG]).

## Installation

Maven:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.luizrcs</groupId>
    <artifactId>KotlinNBT</artifactId>
    <version>Tag</version>
</dependency>
```

Gradle Groovy:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
    implementation 'com.github.luizrcs:KotlinNBT:1.0.0'
}
```

Gradle Kotlin:

```kotlin
repositories {
    maven("https://jitpack.io")
}
```

```kotlin
dependencies {
    implementation("com.github.luizrcs:KotlinNBT:1.0.0")
}
```

## Usage

The object `NbtIO` is used similarly to `javax.imageio.ImageIO` to read/write a NBT `TagCompound`. The available
compression methods are: `GZIP`, `ZLIB` and `NONE` (no compression).

### Reading from file

```kotlin
val file = File("test.nbt")
val compression = NbtIO.Compression.GZIP

val tagCompound = NbtIO.read(file, compression)
```

### Writing to file

```kotlin
val file = File("test.nbt")
val compression = NbtIO.Compression.GZIP

NbtIO.write(someTagCompound, file, compression)
```

### NBT builder

```kotlin
val nbt = nbt("root") {
    compound("testCompound") {
        intArray["fibonacciWithoutZero"] = intArrayOf(1, 1, 2, 3, 5, 8, 13, 21)
    }
    list["testList"] = listOf(
        compound {
            string["firstString"] = "I'm the first String :)"
            string["secondString"] = "I'm the second String, but order is not guaranteed :/"
            int["justAnInteger"] = 1
        }
    )
    long["timestamp"] = System.currentTimeMillis()
}
```

Using `println(nbt)` will print a Kotlin-styled tree:

```
root: Compound = {
    testCompound: Compound = {
        fibonacciWithoutZero: IntArray = [1, 1, 2, 3, 5, 8, 13, 21]
    },
    testList: List<Compound> = [
        Compound = {
            firstString: String = "I'm the first String :)",
            justAnInteger: Int = 1,
            secondString: String = "I'm the second String, but order is not guaranteed :/"
        }
    ],
    timestamp: Long = 1591470914831L
}
```

**Important:** according to the NBT specification, the order of the displayed/read tags is not guaranteed. KotlinNBT
prints a `TagCompound` using a schema based on the way [NBTExplorer][NBTExplorer] does it.

### Type safety

In the example above, the typed values can be obtained with type-safe accessors:

```kotlin
val fibonacci: IntArray = nbt["testCompound"].tagCompound["fibonacciWithoutZero"].intArray
val message: String = nbt["testList"].tagList[0].tagCompound["firstString"].string
val timestamp: Long = nbt["timestamp"].long
	
println(fibonacci.toList())
println(message)
println(timestamp)
``` 

prints

```
[1, 1, 2, 3, 5, 8, 13, 21]
I'm the first String :)
1591470914831
```

To check the tag types before accessing them (whether they exist or not), properties `isTagX` can be used, where `X` is
a tag type:

```kotlin
println(nbt["testCompound"].isTagCompound)
println(nbt["timestamp"].isTagFloat)
println(nbt["world"].isTagString)
```

prints

```
true
false
false
```

### Cloning tags

The provided `Tag<T>.clone()` functions deep copy the tag (that is, the tag itself and its children are cloned
recursively) while keeping the type safety:

```kotlin
nbt.clone() // to keep the same name, "root"
nbt.clone("rootClone") // to change the root tag name
nbt["testList"]?.clone("actualList")
```

## Donate

KotlinNBT is free and open-source for everyone to enjoy.

If you wish to support the continuous development of this and other projects, you can [donate][Donation]! Of course,
I'm always providing support for anyone independently on the donations.

Everyone loves open-source <3  

[WebArchive]: https://web.archive.org/web/20100124085747/http://www.minecraft.net/docs/NBT.txt
[Gamepedia]: https://minecraft.gamepedia.com/NBT_format
[WikiVG]: https://wiki.vg/NBT
[NBTExplorer]: https://github.com/jaquadro/NBTExplorer
[Donation]: https://donorbox.org/luizrcs