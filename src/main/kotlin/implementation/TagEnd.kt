package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import java.nio.*

object TagEnd: Tag<Nothing>(TAG_END, null) {
	
	override val sizeInBytes = 0
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
}
