package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import me.mrpingu.nbt.extension.*
import java.nio.*

class TagString private constructor(name: String?): Tag<String>(TAG_STRING, name) {
	
	override val sizeInBytes get() = Short.SIZE_BYTES + _value.toByteArray().size
	
	constructor(value: String, name: String?): this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.string
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putString(_value)
	}
	
	override fun clone(name: String?) = TagString(value, name)
	
	override fun valueToString() = "\"$_value\""
}
