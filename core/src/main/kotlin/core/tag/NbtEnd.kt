package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

object NbtEnd : NbtBase<Nothing>(END, null) {
	
	override val value get() = throw UnsupportedOperationException("NbtEnd has no value")
	
	override val sizeInBytes = 0
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtEnd(this)
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
	
	override fun valueToString() = ""
}