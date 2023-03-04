package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

class NbtString private constructor(name: String? = null) : NbtBase<String>(STRING, name) {
	
	override val sizeInBytes get() = Short.SIZE_BYTES + _value.toByteArray().size
	
	constructor(value: String, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.string
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putString(_value)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtString(this)
	
	override fun clone(name: String?) = NbtString(value, name)
	
	override fun valueToString() = "\"$_value\""
}