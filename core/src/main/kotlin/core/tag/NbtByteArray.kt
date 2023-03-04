package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtByteArray private constructor(name: String? = null) : NbtArray<ByteArray>(BYTE_ARRAY, name) {
	
	override val value get() = _value.copyOf()
	override val size get() = _value.size
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size
	
	constructor(value: ByteArray, name: String? = null) : this(name) {
		_value = value.copyOf()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		val length = byteBuffer.int
		
		_value = ByteArray(length)
		byteBuffer.get(_value, 0, length)
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		byteBuffer.put(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtByteArray(this)
	
	override fun clone(name: String?) = NbtByteArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString()}]"
}