package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import java.nio.*

class TagShort private constructor(name: String? = null): Tag<Short>(TAG_SHORT, name) {
	
	override val sizeInBytes = Short.SIZE_BYTES
	
	constructor(value: Short, name: String? = null): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.short
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putShort(_value)
	}
	
	override fun clone(name: String?) = TagShort(value, name)
}
