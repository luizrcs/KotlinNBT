package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagString private constructor(name: String? = null): Tag<String>(TAG_STRING, name) {
	
	override val sizeInBytes get() = Short.SIZE_BYTES + _value.toByteArray().size
	
	constructor(value: String, name: String? = null): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.string
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putString(_value)
	}
	
	override fun clone(name: String?) = TagString(value, name)
	
	override fun valueToString() = "\"$_value\""
}
