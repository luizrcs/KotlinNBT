package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.descriptors.StructureKind.*
import kotlinx.serialization.internal.*
import kotlinx.serialization.modules.*

@OptIn(InternalSerializationApi::class)
open class NbtEncoder(private val tagConsumer: (NbtAny) -> Unit) : NamedValueEncoder() {
	private val compoundMap = MutableNbtCompoundMap()
	
	override val serializersModule = EmptySerializersModule()
	
	open fun currentTag(): NbtAny = NbtCompound(compoundMap)
	
	open fun putTag(key: String, tag: NbtAny) {
		compoundMap[key] = tag
	}
	
	// Fully supported types -> standard encoding
	override fun encodeTaggedByte(tag: String, value: Byte) = putTag(tag, NbtByte(value, tag))
	override fun encodeTaggedShort(tag: String, value: Short) = putTag(tag, NbtShort(value, tag))
	override fun encodeTaggedInt(tag: String, value: Int) = putTag(tag, NbtInt(value, tag))
	override fun encodeTaggedLong(tag: String, value: Long) = putTag(tag, NbtLong(value, tag))
	override fun encodeTaggedFloat(tag: String, value: Float) = putTag(tag, NbtFloat(value, tag))
	override fun encodeTaggedDouble(tag: String, value: Double) = putTag(tag, NbtDouble(value, tag))
	override fun encodeTaggedString(tag: String, value: String) = putTag(tag, NbtString(value, tag))
	
	// Unsupported types -> non-standard encoding
	override fun encodeTaggedBoolean(tag: String, value: Boolean) = putTag(tag, NbtByte((if (value) 1 else 0).toByte()))
	override fun encodeTaggedChar(tag: String, value: Char) = putTag(tag, NbtByte(value.code.toByte()))
	
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

class PrimitiveArrayNbtEncoder(private val serialName: String, tagConsumer: (NbtAny) -> Unit) : NbtEncoder(tagConsumer) {
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
		"ByteArray"    -> NbtByteArray((list as List<Byte>).toByteArray())
		"IntArray"     -> NbtIntArray((list as List<Int>).toIntArray())
		"LongArray"    -> NbtLongArray((list as List<Long>).toLongArray())
		
		// Barely supported types -> unoptimized encoding
		"ShortArray"   -> NbtIntArray((list as List<Short>).map { it.toInt() }.toIntArray())
		"FloatArray"   -> NbtList(NbtType.FLOAT, (list as List<Float>).map(::NbtFloat))
		"DoubleArray"  -> NbtList(NbtType.DOUBLE, (list as List<Double>).map(::NbtDouble))
		
		// Unsupported types -> non-standard encoding
		"BooleanArray" -> NbtByteArray((list as List<Boolean>).map { (if (it) 1 else 0).toByte() }.toByteArray())
		"CharArray"    -> NbtByteArray((list as List<Char>).map { it.code.toByte() }.toByteArray())
		else           -> throw IllegalArgumentException("NBT encoding for $serialName not implemented")
	}
}

class ListNbtEncoder(tagConsumer: (NbtAny) -> Unit) : NbtEncoder(tagConsumer) {
	private val list = mutableListOf<NbtAny>()
	
	override fun currentTag() = NbtList(list.first().type, list)
	
	override fun putTag(key: String, tag: NbtAny) {
		list += tag
	}
}