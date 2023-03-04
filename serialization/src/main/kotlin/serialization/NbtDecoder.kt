package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.descriptors.StructureKind.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.internal.*

@OptIn(InternalSerializationApi::class)
internal open class NbtDecoder(open val tag: NbtAny) : NamedValueDecoder() {
	protected open var currentIndex = 0
	
	protected open fun currentTag(name: String) = tag.compound[name]
	protected open fun currentTag() = currentTagOrNull?.let { currentTag(it) } ?: tag
	
	@OptIn(ExperimentalSerializationApi::class)
	override fun decodeElementIndex(descriptor: SerialDescriptor) = if (currentIndex < descriptor.elementsCount) currentIndex++ else DECODE_DONE
	
	override fun decodeTaggedByte(tag: String) = currentTag(tag).byte
	override fun decodeTaggedShort(tag: String) = currentTag(tag).short
	override fun decodeTaggedInt(tag: String) = currentTag(tag).int
	override fun decodeTaggedLong(tag: String) = currentTag(tag).long
	override fun decodeTaggedFloat(tag: String) = currentTag(tag).float
	override fun decodeTaggedDouble(tag: String) = currentTag(tag).double
	override fun decodeTaggedString(tag: String) = currentTag(tag).string
	
	@Suppress("UNCHECKED_CAST")
	@OptIn(ExperimentalSerializationApi::class)
	override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
		val currentTag = currentTag()
		
		val decoder = when (descriptor.kind) {
			CLASS -> NbtDecoder(currentTag)
			LIST  ->
				if (currentTag.isNbtList) ListNbtDecoder(currentTag().nbtList)
				else PrimitiveArrayNbtDecoder(currentTag as NbtArrayAny)
			else  -> this
		}
		
		return decoder
	}
}

internal class PrimitiveArrayNbtDecoder(override val tag: NbtArrayAny) : NbtDecoder(tag) {
	override var currentIndex = -1
	private val lastIndex = tag.size - 1
	
	override fun currentTag(name: String) = tag
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if (currentIndex < lastIndex) ++currentIndex
		else DECODE_DONE
	
	// Fully supported types -> standard decoding
	override fun decodeTaggedByte(tag: String) = currentTag(tag).byteArray[currentIndex]
	override fun decodeTaggedInt(tag: String) = currentTag(tag).intArray[currentIndex]
	override fun decodeTaggedLong(tag: String) = currentTag(tag).longArray[currentIndex]
	
	// Barely supported types -> unoptimized decoding
	override fun decodeTaggedShort(tag: String) = currentTag(tag).intArray[currentIndex].toShort()
	
	// Unsupported types -> non-standard decoding
	override fun decodeTaggedBoolean(tag: String) = currentTag(tag).byteArray[currentIndex] == 1.toByte()
	override fun decodeTaggedChar(tag: String) = currentTag(tag).byteArray[currentIndex].toInt().toChar()
}

internal class ListNbtDecoder(override val tag: NbtList) : NbtDecoder(tag) {
	override var currentIndex = -1
	private val lastIndex = tag.value.lastIndex
	
	override fun currentTag(name: String) = tag.list[currentIndex]
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if (currentIndex < lastIndex) ++currentIndex
		else DECODE_DONE
}