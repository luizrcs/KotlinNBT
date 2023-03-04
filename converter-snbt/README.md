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

Conversions can be done both ways with the `convertFromNbt` and `convertToNbt` methods:

```kotlin
SnbtNbtConverter.register()

val snbt = SnbtNbtConverter.convertFromNbt(nbt) // Convert NBT to SNBT
val nbt = SnbtNbtConverter.convertToNbt(snbt) // Convert SNBT to NBT
```

### Customization

The converter can be customized by using the `SnbtNbtConverter { ... }` builder. Currently, there are no options
available.

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
| `NbtEnd`           | ` `                 | ` ` (empty string, if explicitly requested) or ignored (if it's at the end of a `NbtCompound`) |
| `NbtByte`          | `${value}b`         | `1b`                                                                                           |
| `NbtShort`         | `${value}s`         | `1s`                                                                                           |
| `NbtInt`           | `${value}`          | `1`                                                                                            |
| `NbtLong`          | `${value}L`         | `1L`                                                                                           |
| `NbtFloat`         | `${value}f`         | `1.0f`                                                                                         |
| `NbtDouble`        | `${value}d`         | `1.0d`                                                                                         |
| `NbtByteArray`     | `[B;${values}]`     | `[B;1b,2b,3b]`                                                                                 |
| `NbtString`        | `"${value}"`        | `"Hello"` or `'Hello'`                                                                         |
| `NbtList`          | `[${values}]`       | `[1,2,3]`                                                                                      |
| `NbtCompound`      | `{${values}}`       | `{a:1,b:2,c:3}` or `{'a':1,'b':2,'c':3}` or `{"a":1,"b":2,"c":3}`                              |
| `NbtIntArray`      | `[I;${values}]`     | `[I;1,2,3]`                                                                                    |
| `NbtLongArray`     | `[L;${values}]`     | `[L;1L,2L,3L]`                                                                                 |

### SNBT to NBT

| SNBT representation                   | SNBT example                      | KotlinNBT tag type |
|---------------------------------------|-----------------------------------|--------------------|
| `${value}b`                           | `1b`                              | `NbtByte`          |
| `${value}s`                           | `1s`                              | `NbtShort`         |
| `${value}`                            | `1`                               | `NbtInt`           |
| `${value}L`                           | `1L`                              | `NbtLong`          |
| `${value}f`                           | `1.0f`                            | `NbtFloat`         |
| `${value}d` or `x.y`                  | `1.0d` or `1.0`                   | `NbtDouble`        |
| `[B;${values}]`                       | `[B;1b,2b,3b]`                    | `NbtByteArray`     |
| `"${value}"` or `'$value'` or `value` | `"Hello"` or `'Hello'` or `Hello` | `NbtString`        |
| `[${values}]`                         | `[1,2,3]`                         | `NbtList`          |
| `{${values}}`                         | `{a:1,b:2,c:3}`                   | `NbtCompound`      |
| `[I;${values}]`                       | `[I;1,2,3]`                       | `NbtIntArray`      |
| `[L;${values}]`                       | `[L;1L,2L,3L]`                    | `NbtLongArray`     |

## Known issues

- The only conversion 'behavior' available is the default Minecraft one. However, this is not always
  desirable, as it makes a lot of assumptions which are not strict nor will fit everyone's data, as a more general use
  format.
- It's not clear yet if it's a good idea to support spaces on unquoted tag names or `NbtString`s. Currently, only spaces
  between other non-whitespace characters are parsed; leading and trailing whitespaces are ignored. This behaviour might
  be entirely removed in the future.

[Minecraft Wiki]: https://minecraft.fandom.com/wiki/NBT_format#SNBT_format