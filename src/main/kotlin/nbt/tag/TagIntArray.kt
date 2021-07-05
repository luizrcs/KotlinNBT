package br.com.luizrcs.nbt.tag

import br.com.luizrcs.nbt.tag.TagType.*
import java.nio.*

class TagIntArray private constructor(name: String? = null): Tag<IntArray>(TAG_INT_ARRAY, name) {
	
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Int.SIZE_BYTES
	
	constructor(value: IntArray, name: String? = null): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = IntArray(byteBuffer.int) { byteBuffer.int }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putInt(it) }
	}
	
	override fun clone(name: String?) = TagIntArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString()}]"
}
