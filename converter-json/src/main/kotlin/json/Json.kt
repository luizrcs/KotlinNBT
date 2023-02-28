package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.json.*

object Json : NbtConverter<JsonElement>("json") {
	override val convertTagByte: Tag<Byte>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagShort: Tag<Short>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagInt: Tag<Int>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagLong: Tag<Long>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagFloat: Tag<Float>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagDouble: Tag<Double>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagByteArray: Tag<ByteArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertTagString: Tag<String>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertTagList: Tag<TagListList>.() -> JsonElement? = { JsonArray(value.mapNotNull { tag -> tag.convert("json") }) }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> JsonElement? = { JsonObject(value.mapValues { (_, value) -> value.convert<JsonElement>("json")!! }) }
	override val convertTagIntArray: Tag<IntArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertTagLongArray: Tag<LongArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	
	override fun convert(tag: TagAny) = tag.convert<JsonElement>("json")
	
	override fun parse(value: JsonElement): TagAny? = null
}