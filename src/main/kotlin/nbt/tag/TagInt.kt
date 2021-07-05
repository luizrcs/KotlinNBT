package br.com.luizrcs.nbt.tag

import br.com.luizrcs.nbt.tag.TagType.*
import java.nio.*

class TagInt private constructor(name: String? = null): Tag<Int>(TAG_INT, name) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Int, name: String? = null): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.int
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value)
	}
	
	override fun clone(name: String?) = TagInt(value, name)
}
