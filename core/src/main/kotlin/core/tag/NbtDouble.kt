package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtDouble private constructor(name: String? = null) : NbtBase<Double>(DOUBLE, name) {
	
	override val sizeInBytes = Long.SIZE_BYTES
	
	constructor(value: Double, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.double
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putDouble(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtDouble(this)
	
	override fun clone(name: String?) = NbtDouble(value, name)
}