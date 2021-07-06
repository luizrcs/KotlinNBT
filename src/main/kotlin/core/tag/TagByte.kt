package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagByte private constructor(name: String? = null): Tag<Byte>(TAG_BYTE, name) {
	
	override val sizeInBytes = Byte.SIZE_BYTES
	
	constructor(value: Byte, name: String? = null): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.byte
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.put(_value)
	}
	
	override fun clone(name: String?) = TagByte(value, name)
}
