package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtIntArray private constructor(name: String? = null) : NbtArray<IntArray>(INT_ARRAY, name) {
	
	override val value get() = _value.copyOf()
	override val size get() = _value.size
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Int.SIZE_BYTES
	
	constructor(value: IntArray, name: String? = null) : this(name) {
		_value = value.copyOf()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = IntArray(byteBuffer.int) { byteBuffer.int }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putInt(it) }
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtIntArray(this)
	
	override fun clone(name: String?) = NbtIntArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString()}]"
}