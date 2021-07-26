@file:OptIn(
	ExperimentalSerializationApi::class,
	InternalSerializationApi::class
)

package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.descriptors.StructureKind.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.modules.*

open class NbtEncoder(private val tagConsumer: (TagAny) -> Unit) : NamedValueEncoder() {
	private val compoundMap = MutableCompoundMap()
	
	override val serializersModule = EmptySerializersModule
	
	open fun currentTag(): TagAny = TagCompound(compoundMap)
	
	open fun putTag(key: String, tag: TagAny) {
		compoundMap[key] = tag
	}
	
	override fun encodeTaggedByte(tag: String, value: Byte) = putTag(tag, TagByte(value, tag))
	override fun encodeTaggedShort(tag: String, value: Short) = putTag(tag, TagShort(value, tag))
	override fun encodeTaggedInt(tag: String, value: Int) = putTag(tag, TagInt(value, tag))
	override fun encodeTaggedLong(tag: String, value: Long) = putTag(tag, TagLong(value, tag))
	override fun encodeTaggedFloat(tag: String, value: Float) = putTag(tag, TagFloat(value, tag))
	override fun encodeTaggedDouble(tag: String, value: Double) = putTag(tag, TagDouble(value, tag))
	override fun encodeTaggedString(tag: String, value: String) = putTag(tag, TagString(value, tag))
	
	// unsupported types
	override fun encodeTaggedBoolean(tag: String, value: Boolean) = putTag(tag, TagByte((if (value) 1 else 0).toByte()))
	override fun encodeTaggedChar(tag: String, value: Char) = putTag(tag, TagByte(value.toByte()))
	
	override fun beginStructure(descriptor: SerialDescriptor): NbtEncoder {
		val consumer =
			if (currentTagOrNull == null) tagConsumer
			else { tag -> putTag(popTag(), tag) }
		
		val serialName = descriptor.serialName
		return when (descriptor.kind) {
			CLASS -> NbtEncoder(consumer)
			LIST  -> when {
				serialName.endsWith("Array") -> PrimitiveArrayNbtEncoder(serialName.substringAfterLast('.'), consumer)
				serialName.endsWith("List")  -> ListNbtEncoder(consumer)
				else                         -> throw IllegalArgumentException("Nbt encoding for $serialName not implemented")
			}
			else  -> this
		}
	}
	
	override fun endEncode(descriptor: SerialDescriptor) {
		tagConsumer(currentTag())
	}
}

class PrimitiveArrayNbtEncoder(val serialName: String, tagConsumer: (TagAny) -> Unit) : NbtEncoder(tagConsumer) {
	private val list = mutableListOf<Any>()
	
	private fun putPrimitive(primitive: Any) {
		list += primitive
	}
	
	// fully supported types
	override fun encodeTaggedByte(tag: String, value: Byte) = putPrimitive(value)
	override fun encodeTaggedInt(tag: String, value: Int) = putPrimitive(value)
	override fun encodeTaggedLong(tag: String, value: Long) = putPrimitive(value)
	
	// barely supported types
	override fun encodeTaggedShort(tag: String, value: Short) = putPrimitive(value)
	override fun encodeTaggedFloat(tag: String, value: Float) = putPrimitive(value)
	override fun encodeTaggedDouble(tag: String, value: Double) = putPrimitive(value)
	
	// unsupported types
	override fun encodeTaggedBoolean(tag: String, value: Boolean) = putPrimitive(value)
	override fun encodeTaggedChar(tag: String, value: Char) = putPrimitive(value)
	
	override fun currentTag() = when (serialName) {
		// fully supported types
		"ByteArray"    -> TagByteArray((list as List<Byte>).toByteArray())
		"IntArray"     -> TagIntArray((list as List<Int>).toIntArray())
		"LongArray"    -> TagLongArray((list as List<Long>).toLongArray())
		
		// barely supported types
		"ShortArray"   -> TagIntArray((list as List<Short>).map { it.toInt() }.toIntArray())
		"FloatArray"   -> TagList(TAG_FLOAT, (list as List<Float>).map(::TagFloat))
		"DoubleArray"  -> TagList(TAG_DOUBLE, (list as List<Double>).map(::TagDouble))
		
		// unsupported types
		"BooleanArray" -> TagByteArray((list as List<Boolean>).map { (if (it) 1 else 0).toByte() }.toByteArray())
		"CharArray"    -> TagByteArray((list as List<Char>).map { it.toByte() }.toByteArray())
		else           -> throw IllegalArgumentException("Nbt encoding for $serialName not implemented")
	}
}

class ListNbtEncoder(tagConsumer: (TagAny) -> Unit) : NbtEncoder(tagConsumer) {
	private val list = mutableListOf<TagAny>()
	
	override fun currentTag() = TagList(list.first().type, list)
	
	override fun putTag(key: String, tag: TagAny) {
		list += tag
	}
}