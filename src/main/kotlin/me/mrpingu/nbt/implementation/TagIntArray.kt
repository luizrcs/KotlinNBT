package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import java.nio.*

class TagIntArray private constructor(name: String?): Tag<IntArray>(TAG_INT_ARRAY, name) {
	
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Int.SIZE_BYTES
	
	constructor(value: IntArray, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = IntArray(byteBuffer.int) { byteBuffer.int }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putInt(it) }
	}
	
	override fun clone(name: String?) = TagIntArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString()}]"
}
