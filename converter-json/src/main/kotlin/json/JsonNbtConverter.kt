@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
)

package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import br.com.luizrcs.nbt.json.JsonNbtConverter.*
import kotlinx.serialization.json.*

@ExperimentalJsonNbtConverter
inline fun JsonNbtConverter(builder: JsonNbtConverterBuilder.() -> Unit): JsonNbtConverter =
	JsonNbtConverterBuilder().apply(builder).build()

@ExperimentalJsonNbtConverter
open class JsonNbtConverter private constructor(
	private val conf: JsonNbtConverterBuilder = JsonNbtConverterBuilder(),
) : NbtConverter<JsonElement>() {
	
	override val convertNbtEnd: NbtBase<Nothing>.() -> JsonElement? = { JsonNull }
	override val convertNbtByte: NbtBase<Byte>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtShort: NbtBase<Short>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtInt: NbtBase<Int>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtLong: NbtBase<Long>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtFloat: NbtBase<Float>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtDouble: NbtBase<Double>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtByteArray: NbtBase<ByteArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertNbtString: NbtBase<String>.() -> JsonElement? = { JsonPrimitive(value) }
	override val convertNbtList: NbtBase<NbtListList>.() -> JsonElement? = { JsonArray(value.mapNotNull { tag -> convertFromNbt(tag) }) }
	override val convertNbtCompound: NbtBase<NbtCompoundMap>.() -> JsonElement? = { JsonObject(value.mapNotNull { (key, value) -> convertFromNbt(value)?.let { key to it } }.toMap()) }
	override val convertNbtIntArray: NbtBase<IntArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	override val convertNbtLongArray: NbtBase<LongArray>.() -> JsonElement? = { JsonArray(value.map { JsonPrimitive(it) }) }
	
	override fun convertFromNbt(tag: NbtAny): JsonElement? = tag.convert(this)
	override fun convertToNbt(value: JsonElement): NbtAny? = when (value) {
		is JsonPrimitive -> when {
			value.isString              -> NbtString(value.content)
			value.contentOrNull != null -> when {
				value.content.toBooleanStrictOrNull() != null -> NbtByte(if (value.content.toBooleanStrict()) 1 else 0)
				value.content.toByteOrNull() != null          -> NbtByte(value.content.toByte())
				value.content.toShortOrNull() != null         -> NbtShort(value.content.toShort())
				value.content.toIntOrNull() != null           -> NbtInt(value.content.toInt())
				value.content.toLongOrNull() != null          -> NbtLong(value.content.toLong())
				value.content.toFloatOrNull() != null         -> NbtFloat(value.content.toFloat())
				value.content.toDoubleOrNull() != null        -> NbtDouble(value.content.toDouble())
				else                                          -> NbtString(value.content)
			}
			else                        -> null
		}
		is JsonArray     -> {
			val converted = value.mapNotNull { convertToNbt(it) }
			when {
				converted.isEmpty()             -> if (conf.convertEmptyJsonArrayToList) NbtList(END, emptyList()) else null
				converted.all { it is NbtByte } -> NbtByteArray(converted.map { (it as NbtByte).value }.toByteArray())
				converted.all { it is NbtInt }  -> NbtIntArray(converted.map { (it as NbtInt).value }.toIntArray())
				converted.all { it is NbtLong } -> NbtLongArray(converted.map { (it as NbtLong).value }.toLongArray())
				else                            -> NbtList(converted.first().type, converted)
			}
		}
		is JsonObject    -> value.mapNotNull { (key, value) -> convertToNbt(value)?.let { key to it } }.toMap().let(::NbtCompound)
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