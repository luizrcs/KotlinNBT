package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtLongArray private constructor(name: String? = null) : NbtArray<LongArray>(LONG_ARRAY, name) {
	
	override val value get() = _value.copyOf()
	override val size get() = _value.size
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Long.SIZE_BYTES
	
	constructor(value: LongArray, name: String? = null) : this(name) {
		_value = value.copyOf()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = LongArray(byteBuffer.int) { byteBuffer.long }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putLong(it) }
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtLongArray(this)
	
	override fun clone(name: String?) = NbtLongArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString { "${it}L" }}]"
}