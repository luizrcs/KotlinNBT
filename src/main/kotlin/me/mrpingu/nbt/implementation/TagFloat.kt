package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import java.nio.*

class TagFloat private constructor(name: String?): Tag<Float>(TAG_FLOAT, name) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Float, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.float
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putFloat(_value)
	}
	
	override fun clone(name: String?) = TagFloat(value, name)
	
	override fun valueToString() = "${_value}f"
}
