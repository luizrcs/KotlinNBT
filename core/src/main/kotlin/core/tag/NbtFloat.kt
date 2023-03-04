package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtFloat private constructor(name: String? = null) : NbtBase<Float>(FLOAT, name) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Float, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.float
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putFloat(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtFloat(this)
	
	override fun clone(name: String?) = NbtFloat(value, name)
	
	override fun valueToString() = "${_value}f"
}