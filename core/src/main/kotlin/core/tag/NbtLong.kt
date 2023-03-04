package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtLong private constructor(name: String? = null) : NbtBase<Long>(LONG, name) {
	
	override val sizeInBytes = Long.SIZE_BYTES
	
	constructor(value: Long, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.long
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putLong(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtLong(this)
	
	override fun clone(name: String?) = NbtLong(value, name)
	
	override fun valueToString() = "${_value}L"
}