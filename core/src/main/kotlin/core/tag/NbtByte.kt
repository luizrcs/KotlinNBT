package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtByte private constructor(name: String? = null) : NbtBase<Byte>(BYTE, name) {
	
	override val sizeInBytes = Byte.SIZE_BYTES
	
	constructor(value: Byte, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.byte
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.put(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtByte(this)
	
	override fun clone(name: String?) = NbtByte(value, name)
}