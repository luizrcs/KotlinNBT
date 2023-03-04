package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtShort private constructor(name: String? = null) : NbtBase<Short>(SHORT, name) {
	
	override val sizeInBytes = Short.SIZE_BYTES
	
	constructor(value: Short, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.short
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putShort(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtShort(this)
	
	override fun clone(name: String?) = NbtShort(value, name)
}