package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import java.nio.*

class TagLong private constructor(name: String?): Tag<Long>(TAG_LONG, name) {
	
	override val sizeInBytes = Long.SIZE_BYTES
	
	constructor(value: Long, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
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
