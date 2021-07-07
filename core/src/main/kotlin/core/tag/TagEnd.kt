package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
import java.nio.*

object TagEnd : Tag<Nothing>(TAG_END, null) {
	
	override val sizeInBytes = 0
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
}