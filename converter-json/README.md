# NBT to JSON converter

KotlinNBT module for converting NBT data to JSON and vice versa. This module relies heavily
on [kotlinx-serialization-json][kotlinx-serialization-json], as the data is not converted to `String`, but rather
to `JsonElement` subclasses (which can be further converted to `String`).

## Installation

This module must be used alongside KotlinNBT's `nbt-core` and also `kotlinx-serialization-json` dependencies. The
dependencies can be added from Maven Central to your project using [Gradle](https://gradle.org/):

### Groovy DSL

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'br.com.luizrcs.nbt:nbt-core:2.0.0'
    implementation 'br.com.luizrcs.nbt:nbt-converter-json:2.0.0'
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion"
}
```

### Kotlin DSL

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("br.com.luizrcs.nbt:nbt-core:2.0.0")
    implementation("br.com.luizrcs.nbt:nbt-converter-json:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}
```

### 

## Usage

The default converter can be used with the `JsonNbtConverter` object, or you can use the `JsonNbtConverter { ... }`
builder to customize it.

Conversions can be done both ways with the `convertFromTag` and `convertToTag` methods:

```kotlin
val json = JsonNbtConverter.convertFromTag(nbt) // Convert NBT to JSON
val nbt = JsonNbtConverter.convertToTag(json) // Convert JSON to NBT
```

## Conversion rules

The conversion rules are based on the [Minecraft Wiki][Minecraft Wiki] description as follows:

### NBT to JSON

| KotlinNBT tag type | JSON type | kotlinx-serialization-json class                                                        |
|--------------------|-----------|-----------------------------------------------------------------------------------------|
| `TagByte`          | `number`  | `JsonPrimitive`                                                                         |
| `TagShort`         | `number`  | `JsonPrimitive`                                                                         |
| `TagInt`           | `number`  | `JsonPrimitive`                                                                         |
| `TagLong`          | `number`  | `JsonPrimitive`                                                                         |
| `TagFloat`         | `number`  | `JsonPrimitive`                                                                         |
| `TagDouble`        | `number`  | `JsonPrimitive`                                                                         |
| `TagByteArray`     | `array`   | `JsonArray`                                                                             |
| `TagString`        | `string`  | `JsonPrimitive`                                                                         |
| `TagList`          | `array`   | `JsonArray`                                                                             |
| `TagCompound`      | `object`  | `JsonObject`                                                                            |
| `TagIntArray`      | `array`   | `JsonArray`                                                                             |
| `TagLongArray`     | `array`   | `JsonArray`                                                                             |
| `TagEnd`           | `null`    | `JsonNull` (if explicitly requested) or ignored (if it's at the end of a `TagCompound`) |

### JSON to NBT

| JSON type | kotlinx-serialization-json class | KotlinNBT tag type                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|-----------|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `number`  | `JsonPrimitive`                  | If the number is an integer, it will be converted to the first NBT number type that fits it: `TagByte`, `TagShort`, `TagInt`, `TagLong`, in this order. <br> If the number is a floating point number, it will be converted to the first NBT number type that fits it: `TagFloat`, `TagDouble`, in this order.                                                                                                                                                                                                                                                              |
| `string`  | `JsonPrimitive`                  | `TagString`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| `array`   | `JsonArray`                      | If the array is empty, it won't be converted (by default), as no type can be inferred. This behavior can be replaced with the `convertEmptyJsonArrayToList` flag, which will convert an empty `JsonArray` to a non-standard `TagList<TagEnd>`, which is always empty). <br> If the array is not empty, each of its elements will be converted to NBT; if they're not all converted to the same type, the array will be returned as `null`; otherwise, the array will be converted to a `TagByteArray`, `TagIntArray`, or `TagLongArray`, if appropriate, or to a `TagList`. |
| `object`  | `JsonObject`                     | `TagCompound`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `boolean` | `JsonPrimitive`                  | `TagByte` with value `1` or `0`.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| `null`    | `JsonNull`                       | `JsonNull` instances are always ignored, but will be converted to `null` if explicitly requested.                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |

## Known issues

- Right now, the only conversion 'behavior' available is the default Minecraft one. However, this is not always
  desirable, as it makes a lot of assumptions which are not strict nor will fit everyone's data, as a more general use
  format.

[kotlinx-serialization-json]: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#json-element-builders

[Minecraft Wiki]: https://minecraft.fandom.com/wiki/NBT_format#JSON_and_NBT