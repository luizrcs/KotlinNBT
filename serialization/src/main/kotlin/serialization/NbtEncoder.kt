package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.descriptors.StructureKind.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.modules.*

@OptIn(InternalSerializationApi::class)
open class NbtEncoder(private val tagConsumer: (TagAny) -> Unit) : NamedValueEncoder() {
	private val compoundMap = MutableTagCompoundMap()
	
	override val serializersModule = EmptySerializersModule()
	
	open fun currentTag(): TagAny = TagCompound(compoundMap)
	
	open fun putTag(key: String, tag: TagAny) {
		compoundMap[key] = tag
	}
	
	// Fully supported types -> standard encoding
	override fun encodeTaggedByte(tag: String, value: Byte) = putTag(tag, TagByte(value, tag))
	override fun encodeTaggedShort(tag: String, value: Short) = putTag(tag, TagShort(value, tag))
	override fun encodeTaggedInt(tag: String, value: Int) = putTag(tag, TagInt(value, tag))
	override fun encodeTaggedLong(tag: String, value: Long) = putTag(tag, TagLong(value, tag))
	override fun encodeTaggedFloat(tag: String, value: Float) = putTag(tag, TagFloat(value, tag))
	override fun encodeTaggedDouble(tag: String, value: Double) = putTag(tag, TagDouble(value, tag))
	override fun encodeTaggedString(tag: String, value: String) = putTag(tag, TagString(value, tag))
	
	// Unsupported types -> non-standard encoding
	override fun encodeTaggedBoolean(tag: String, value: Boolean) = putTag(tag, TagByte((if (value) 1 else 0).toByte()))
	override fun encodeTaggedChar(tag: String, value: Char) = putTag(tag, TagByte(value.code.toByte()))
	
	@OptIn(ExperimentalSerializationApi::class)
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
				else                         -> throw IllegalArgumentException("NBT encoding for $serialName not implemented")
			}
			else  -> this
		}
	}
	
	override fun endEncode(descriptor: SerialDescriptor) {
		tagConsumer(currentTag())
	}
}

class PrimitiveArrayNbtEncoder(private val serialName: String, tagConsumer: (TagAny) -> Unit) : NbtEncoder(tagConsumer) {
	private val list = mutableListOf<Any>()
	
	private fun addPrimitive(primitive: Any) {
		list += primitive
	}
	
	// Fully supported types -> standard encoding
	override fun encodeTaggedByte(tag: String, value: Byte) = addPrimitive(value)
	override fun encodeTaggedInt(tag: String, value: Int) = addPrimitive(value)
	override fun encodeTaggedLong(tag: String, value: Long) = addPrimitive(value)
	
	// Barely supported types -> unoptimized encoding
	override fun encodeTaggedShort(tag: String, value: Short) = addPrimitive(value)
	override fun encodeTaggedFloat(tag: String, value: Float) = addPrimitive(value)
	override fun encodeTaggedDouble(tag: String, value: Double) = addPrimitive(value)
	
	// Unsupported types -> non-standard encoding
	override fun encodeTaggedBoolean(tag: String, value: Boolean) = addPrimitive(value)
	override fun encodeTaggedChar(tag: String, value: Char) = addPrimitive(value)
	
	@Suppress("UNCHECKED_CAST")
	override fun currentTag() = when (serialName) {
		// Fully supported types -> standard encoding
		"ByteArray"    -> TagByteArray((list as List<Byte>).toByteArray())
		"IntArray"     -> TagIntArray((list as List<Int>).toIntArray())
		"LongArray"    -> TagLongArray((list as List<Long>).toLongArray())
		
		// Barely supported types -> unoptimized encoding
		"ShortArray"   -> TagIntArray((list as List<Short>).map { it.toInt() }.toIntArray())
		"FloatArray"   -> TagList(TAG_FLOAT, (list as List<Float>).map(::TagFloat))
		"DoubleArray"  -> TagList(TAG_DOUBLE, (list as List<Double>).map(::TagDouble))
		
		// Unsupported types -> non-standard encoding
		"BooleanArray" -> TagByteArray((list as List<Boolean>).map { (if (it) 1 else 0).toByte() }.toByteArray())
		"CharArray"    -> TagByteArray((list as List<Char>).map { it.code.toByte() }.toByteArray())
		else           -> throw IllegalArgumentException("NBT encoding for $serialName not implemented")
	}
}

class ListNbtEncoder(tagConsumer: (TagAny) -> Unit) : NbtEncoder(tagConsumer) {
	private val list = mutableListOf<TagAny>()
	
	override fun currentTag() = TagList(list.first().type, list)
	
	override fun putTag(key: String, tag: TagAny) {
		list += tag
	}
}