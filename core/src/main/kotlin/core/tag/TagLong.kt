package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagLong private constructor(name: String? = null) : Tag<Long>(TAG_LONG, name) {
	
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
	
	override fun clone(name: String?) = TagLong(value, name)
	
	override fun valueToString() = "${_value}L"
}
