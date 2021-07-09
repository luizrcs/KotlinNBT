@file:OptIn(
	ExperimentalSerializationApi::class,
	InternalSerializationApi::class
)

package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.internal.*

open class NbtDecoder(open val tag: TagAny) : NamedValueDecoder() {
	
	protected open var currentIndex = 0
	
	protected open fun currentTag(name: String) = tag.tagCompound[name]
	protected open fun currentTag() = currentTagOrNull?.let { currentTag(it) } ?: tag
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if (currentIndex < descriptor.elementsCount) currentIndex++
		else CompositeDecoder.DECODE_DONE
	
	override fun decodeTaggedByte(tag: String) = currentTag(tag).byte
	override fun decodeTaggedShort(tag: String) = currentTag(tag).short
	override fun decodeTaggedInt(tag: String) = currentTag(tag).int
	override fun decodeTaggedLong(tag: String) = currentTag(tag).long
	override fun decodeTaggedFloat(tag: String) = currentTag(tag).float
	override fun decodeTaggedDouble(tag: String) = currentTag(tag).double
	override fun decodeTaggedString(tag: String) = currentTag(tag).string
	
	override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
		val decoder = when (descriptor.kind) {
			StructureKind.CLASS -> NbtDecoder(currentTag())
			StructureKind.LIST  -> ListNbtDecoder(currentTag().tagList)
			else                -> this
		}
		
		return decoder
	}
}

class ListNbtDecoder(override val tag: TagList) : NbtDecoder(tag) {
	
	override var currentIndex = -1
	private val lastIndex = tag.value.lastIndex
	
	override fun currentTag(name: String) = tag[currentIndex]
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if (currentIndex < lastIndex) ++currentIndex
		else CompositeDecoder.DECODE_DONE
}