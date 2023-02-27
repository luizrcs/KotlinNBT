package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

class TagLongArray private constructor(name: String? = null) : TagArray<LongArray, Long>(name, TAG_LONG_ARRAY, converters) {
	
	override val value get() = _value.copyOf()
	
	override val sizeInBytes get() = Int.SIZE_BYTES + _value.size * Long.SIZE_BYTES
	
	override val size get() = _value.size
	
	constructor(value: LongArray, name: String? = null) : this(name) {
		_value = value
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun get(index: Int) = _value[index]
	
	override fun read(byteBuffer: ByteBuffer) {
		_value = LongArray(byteBuffer.int) { byteBuffer.long }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.putInt(_value.size)
		_value.forEach { byteBuffer.putLong(it) }
	}
	
	override fun clone(name: String?) = TagLongArray(value, name)
	
	override fun valueToString() = "[${_value.joinToString { "${it}L" }}]"
	
	companion object : TagCompanion<LongArray>()
}
