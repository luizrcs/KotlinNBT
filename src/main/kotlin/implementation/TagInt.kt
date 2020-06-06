package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import java.nio.*

class TagInt private constructor(name: String?): Tag<Int>(TAG_INT, name) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Int, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
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
