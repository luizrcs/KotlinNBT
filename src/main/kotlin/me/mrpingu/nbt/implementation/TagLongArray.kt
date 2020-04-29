package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import java.nio.*

class TagLongArray private constructor(name: String?): Tag<LongArray>(TAG_LONG_ARRAY, name) {
	
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Long.SIZE_BYTES
	
	constructor(value: LongArray, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = LongArray(byteBuffer.int) { byteBuffer.long }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putLong(it) }
	}
	
	override fun clone(name: String?) = TagLongArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString("L, ")}L]"
}
