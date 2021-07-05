package br.com.luizrcs.nbt.tag

import br.com.luizrcs.nbt.tag.TagType.*
import java.nio.*

object TagEnd: Tag<Nothing>(TAG_END, null) {
	
	override val sizeInBytes = 0
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
}
