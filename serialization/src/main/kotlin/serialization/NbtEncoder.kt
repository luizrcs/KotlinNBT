@file:OptIn(
	ExperimentalSerializationApi::class,
	InternalSerializationApi::class
)

package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.modules.*

open class NbtEncoder : NamedValueEncoder() {
	private val map = mutableMapOf<String, TagAny>()
	
	override val serializersModule = EmptySerializersModule
	
	open fun putTag(key: String, tag: TagAny) {
		map[key] = tag
	}
	
	override fun encodeTaggedByte(tag: String, value: Byte) = putTag(tag, TagByte(value, tag))
	override fun encodeTaggedShort(tag: String, value: Short) = putTag(tag, TagShort(value, tag))
	override fun encodeTaggedInt(tag: String, value: Int) = putTag(tag, TagInt(value, tag))
	override fun encodeTaggedLong(tag: String, value: Long) = putTag(tag, TagLong(value, tag))
	override fun encodeTaggedFloat(tag: String, value: Float) = putTag(tag, TagFloat(value, tag))
	override fun encodeTaggedDouble(tag: String, value: Double) = putTag(tag, TagDouble(value, tag))
	override fun encodeTaggedString(tag: String, value: String) = putTag(tag, TagString(value, tag))
	
	override fun beginStructure(descriptor: SerialDescriptor) =
		when (descriptor.kind) {
			StructureKind.LIST -> ListNbtEncoder()
			else               -> this
		}
	
	open fun getTag(): TagAny = TagCompound(map)
}

class PrimitiveNbtEncoder : NbtEncoder() {
	private lateinit var tagAny: TagAny
	
	override fun putTag(key: String, tag: TagAny) {
		tagAny = tag
	}
	
	override fun getTag() = tagAny
}

class ListNbtEncoder : NbtEncoder() {
	private val list = mutableListOf<TagAny>()
	
	override fun elementName(descriptor: SerialDescriptor, index: Int) = index.toString()
	
	override fun putTag(key: String, tag: TagAny) {
		list.add(key.toInt(), tag)
	}
	
	override fun getTag() = TagList(TagType.TAG_BYTE, list)
}

fun <T> encodeToNbt(serializer: SerializationStrategy<T>, value: T): TagAny {
	val encoder = NbtEncoder()
	encoder.encodeSerializableValue(serializer, value)
	return encoder.getTag()
}

inline fun <reified T> encodeToNbt(value: T) = encodeToNbt(serializer(), value)