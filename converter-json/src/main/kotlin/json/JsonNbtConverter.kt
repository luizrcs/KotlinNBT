@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
)

package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import br.com.luizrcs.nbt.json.JsonNbtConverter.*
import kotlinx.serialization.json.*

@ExperimentalJsonNbtConverter
inline fun JsonNbtConverter(builder: JsonNbtConverterBuilder.() -> Unit): JsonNbtConverter =
	JsonNbtConverterBuilder().apply(builder).build()

@ExperimentalJsonNbtConverter
open class JsonNbtConverter private constructor(
	private val conf: JsonNbtConverterBuilder = JsonNbtConverterBuilder(),
) : NbtConverter<JsonElement>("json") {
	
	override val convertTagByte: Tag<Byte>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagShort: Tag<Short>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagInt: Tag<Int>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagLong: Tag<Long>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagFloat: Tag<Float>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagDouble: Tag<Double>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagByteArray: Tag<ByteArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertTagString: Tag<String>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagList: Tag<TagListList>.() -> JsonElement? = { JsonArray(value.mapNotNull { tag -> tag.convert("json") }) }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> JsonElement? = { JsonObject(value.mapNotNull { (key, value) -> value.convert<JsonElement>("json")?.let { key to it } }.toMap()) }
	override val convertTagIntArray: Tag<IntArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertTagLongArray: Tag<LongArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	
	override fun convertFromTag(tag: TagAny): JsonElement? = tag.convert<JsonElement>("json")
	override fun convertToTag(value: JsonElement): TagAny? = when (value) {
		is JsonPrimitive -> when {
			value.isString              -> TagString(value.content)
			value.contentOrNull != null -> when {
				value.content.toBooleanStrictOrNull() != null -> TagByte(if (value.content.toBooleanStrict()) 1 else 0)
				value.content.toByteOrNull() != null          -> TagByte(value.content.toByte())
				value.content.toShortOrNull() != null         -> TagShort(value.content.toShort())
				value.content.toIntOrNull() != null           -> TagInt(value.content.toInt())
				value.content.toLongOrNull() != null          -> TagLong(value.content.toLong())
				value.content.toFloatOrNull() != null         -> TagFloat(value.content.toFloat())
				value.content.toDoubleOrNull() != null        -> TagDouble(value.content.toDouble())
				else                                          -> TagString(value.content)
			}
			else                        -> null
		}
		is JsonArray     -> {
			val converted = value.mapNotNull { convertToTag(it) }
			when {
				converted.isEmpty()             -> if (conf.convertEmptyJsonArrayToList) TagList(TAG_END, emptyList()) else null
				converted.all { it is TagByte } -> TagByteArray(converted.map { (it as TagByte).value }.toByteArray())
				converted.all { it is TagInt }  -> TagIntArray(converted.map { (it as TagInt).value }.toIntArray())
				converted.all { it is TagLong } -> TagLongArray(converted.map { (it as TagLong).value }.toLongArray())
				else                            -> TagList(converted.first().type, converted)
			}
		}
		is JsonObject    -> value.mapNotNull { (key, value) -> convertToTag(value)?.let { key to it } }.toMap().let(::TagCompound)
		else             -> null
	}
	
	class JsonNbtConverterBuilder @PublishedApi internal constructor() {
		var convertEmptyJsonArrayToList: Boolean = false
		
		fun build(): JsonNbtConverter = JsonNbtConverter(this)
	}
	
	@ExperimentalJsonNbtConverter
	companion object : JsonNbtConverter()
}

/**
 * This annotation marks the conversion between NBT and JSON as experimental.
 *
 * Beware using the annotated API especially if you're developing a library, since your library might become binary
 * incompatible with future versions of the `nbt-converter-json` module.
 *
 * Any usage of a declaration annotated with `@ExperimentalJsonNbtConverter` must be accepted either by annotating that
 * usage with the [OptIn] annotation, e.g. `@OptIn(ExperimentalJsonNbtConverter::class)`, or by using the compiler
 * argument `-opt-in=br.com.luizrcs.nbt.json.ExperimentalJsonNbtConverter`.
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalJsonNbtConverter