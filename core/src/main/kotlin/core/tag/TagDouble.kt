package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagDouble private constructor(name: String? = null) : Tag<Double>(name, TAG_DOUBLE, converters) {
	
	override val sizeInBytes = Long.SIZE_BYTES
	
	constructor(value: Double, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = byteBuffer.double
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putDouble(_value)
	}
	
	override fun clone(name: String?) = TagDouble(value, name)
	
	companion object : TagCompanion<Double>()
}