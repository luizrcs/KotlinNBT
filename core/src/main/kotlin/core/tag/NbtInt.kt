package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtInt private constructor(name: String? = null) : NbtBase<Int>(INT, name) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Int, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.int
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtInt(this)
	
	override fun clone(name: String?) = NbtInt(value, name)
}