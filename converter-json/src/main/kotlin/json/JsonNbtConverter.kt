package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.json.*

object JsonNbtConverter : NbtConverter<JsonElement>("json") {
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
	
	override fun convertFromTag(tag: TagAny) = tag.convert<JsonElement>("json")
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
		is JsonArray     -> value.mapNotNull { convertToTag(it) }.let { TagList(it.first().type, it) }
		is JsonObject    -> value.mapNotNull { (key, value) -> convertToTag(value)?.let { key to it } }.toMap().let(::TagCompound)
		else             -> null
	}
}