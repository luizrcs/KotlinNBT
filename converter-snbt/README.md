# NBT to SNBT converter

KotlinNBT module for converting NBT data to SNBT (Stringified NBT) and vice versa.

## Installation

This module must be used alongside KotlinNBT's `nbt-core` dependency. The dependencies can be added from Maven Central
to your project using [Gradle](https://gradle.org/):

### Groovy DSL

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'br.com.luizrcs.nbt:nbt-core:2.0.0'
    implementation 'br.com.luizrcs.nbt:nbt-converter-snbt:2.0.0'
}
```

### Kotlin DSL

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("br.com.luizrcs.nbt:nbt-core:2.0.0")
    implementation("br.com.luizrcs.nbt:nbt-converter-snbt:2.0.0")
}
```

###                               

## Usage

The default converter can be used with the `SnbtNbtConverter` object, or you can use the `SnbtNbtConverter { ... }`
builder to customize it.

Conversions can be done both ways with the `convertFromTag` and `convertToTag` methods:

```kotlin
val snbt = SnbtNbtConverter.convertFromTag(nbt) // Convert NBT to SNBT
val nbt = SnbtNbtConverter.convertToTag(snbt) // Convert SNBT to NBT
```

### Experimental API

This module is still **highly** experimental, and the API or the logic behind it may **frequently** change in the
future, breaking both source and binary compatibility. For this reason, its uses must be accepted either by annotating
that usage with the `OptIn` annotation (e.g. `@OptIn(ExperimentalSnbtNbtConverter::class)`) or by using the compiler
argument `-opt-in=br.com.luizrcs.nbt.snbt.ExperimentalSnbtNbtConverter`.

## Conversion rules

The conversion rules are based on the [Minecraft Wiki][Minecraft Wiki] description as follows:

### NBT to SNBT

| KotlinNBT tag type | SNBT representation | SNBT example                                                                                   |
|--------------------|---------------------|------------------------------------------------------------------------------------------------|
| `TagEnd`           | ` `                 | ` ` (empty string, if explicitly requested) or ignored (if it's at the end of a `TagCompound`) |
| `TagByte`          | `${value}b`         | `1b`                                                                                           |
| `TagShort`         | `${value}s`         | `1s`                                                                                           |
| `TagInt`           | `${value}`          | `1`                                                                                            |
| `TagLong`          | `${value}L`         | `1L`                                                                                           |
| `TagFloat`         | `${value}f`         | `1.0f`                                                                                         |
| `TagDouble`        | `${value}d`         | `1.0d`                                                                                         |
| `TagByteArray`     | `[B;${values}]`     | `[B;1b,2b,3b]`                                                                                 |
| `TagString`        | `"${value}"`        | `"Hello"` or `'Hello'`                                                                         |
| `TagList`          | `[${values}]`       | `[1,2,3]`                                                                                      |
| `TagCompound`      | `{${values}}`       | `{a:1,b:2,c:3}` or `{'a':1,'b':2,'c':3}` or `{"a":1,"b":2,"c":3}`                              |
| `TagIntArray`      | `[I;${values}]`     | `[I;1,2,3]`                                                                                    |
| `TagLongArray`     | `[L;${values}]`     | `[L;1L,2L,3L]`                                                                                 |

### SNBT to NBT

| SNBT representation                   | SNBT example                      | KotlinNBT tag type |
|---------------------------------------|-----------------------------------|--------------------|
| `${value}b`                           | `1b`                              | `TagByte`          |
| `${value}s`                           | `1s`                              | `TagShort`         |
| `${value}`                            | `1`                               | `TagInt`           |
| `${value}L`                           | `1L`                              | `TagLong`          |
| `${value}f`                           | `1.0f`                            | `TagFloat`         |
| `${value}d` or `x.y`                  | `1.0d` or `1.0`                   | `TagDouble`        |
| `[B;${values}]`                       | `[B;1b,2b,3b]`                    | `TagByteArray`     |
| `"${value}"` or `'$value'` or `value` | `"Hello"` or `'Hello'` or `Hello` | `TagString`        |
| `[${values}]`                         | `[1,2,3]`                         | `TagList`          |
| `{${values}}`                         | `{a:1,b:2,c:3}`                   | `TagCompound`      |
| `[I;${values}]`                       | `[I;1,2,3]`                       | `TagIntArray`      |
| `[L;${values}]`                       | `[L;1L,2L,3L]`                    | `TagLongArray`     |

## Known issues

- The only conversion 'behavior' available is the default Minecraft one. However, this is not always
  desirable, as it makes a lot of assumptions which are not strict nor will fit everyone's data, as a more general use
  format.
- It's not clear yet if it's a good idea to support spaces on unquoted tag names or `TagString`s. Currently, only spaces
  between other non-whitespace characters are parsed; leading and trailing whitespaces are ignored. This behaviour might
  be entirely removed in the future.

[Minecraft Wiki]: https://minecraft.fandom.com/wiki/NBT_format#SNBT_format