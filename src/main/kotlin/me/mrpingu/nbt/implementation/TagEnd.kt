package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import java.nio.*

object TagEnd: Tag<Nothing>(TAG_END, null) {
	
	override val sizeInBytes = 0
	
	override fun read(byteBuffer: ByteBuffer) {}
	
	override fun write(byteBuffer: ByteBuffer) {}
	
	override fun clone(name: String?) = this
	
	override fun toString() = ""
}
