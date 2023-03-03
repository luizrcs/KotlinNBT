package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

object TagEnd : Tag<Nothing>(null, TAG_END, mutableMapOf()) {
	
	override val value get() = throw UnsupportedOperationException("TagEnd has no value")
	
	override val sizeInBytes = 0
	
	fun addConverter(converter: String, function: Tag<Nothing>.() -> Any?) {
		converters[converter] = function
	}
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
	
	override fun valueToString() = ""
}