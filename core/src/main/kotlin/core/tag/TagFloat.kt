package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagFloat private constructor(name: String? = null) : Tag<Float>(name, TAG_FLOAT, converters) {
	
	override val sizeInBytes = Int.SIZE_BYTES
	
	constructor(value: Float, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
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
	
	companion object : TagCompanion<Float>()
}