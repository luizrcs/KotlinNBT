package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagShort private constructor(name: String? = null) : Tag<Short>(name, TAG_SHORT, converters) {
	
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
	
	override fun clone(name: String?) = TagShort(value, name)
	
	companion object : TagCompanion<Short>()
}
