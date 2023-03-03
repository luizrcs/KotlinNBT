package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagIntArray private constructor(name: String? = null) : TagArray<IntArray, Int>(name, TAG_INT_ARRAY, converters) {
	
	override val value get() = _value.copyOf()
	override val size get() = _value.size
	
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Int.SIZE_BYTES
	
	constructor(value: IntArray, name: String? = null) : this(name) {
		_value = value.copyOf()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun get(index: Int) = _value[index]
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = IntArray(byteBuffer.int) { byteBuffer.int }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putInt(it) }
	}
	
	override fun clone(name: String?) = TagIntArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString()}]"
	
	companion object : TagCompanion<IntArray>()
}