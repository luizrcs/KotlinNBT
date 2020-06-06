package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import java.nio.*

class TagDouble private constructor(name: String?): Tag<Double>(TAG_DOUBLE, name) {
	
	override val sizeInBytes = Long.SIZE_BYTES
	
	constructor(value: Double, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.double
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putDouble(_value)
	}
	
	override fun clone(name: String?) = TagDouble(value, name)
}
