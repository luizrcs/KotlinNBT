package br.com.luizrcs.nbt.core.extension

import java.nio.*

val ByteBuffer.byte get() = get()

val ByteBuffer.string: String
	get() {
		val length = short.toInt()
		val byteArray = ByteArray(length).also { get(it, 0, length) }
		return String(byteArray)
	}

fun ByteBuffer.putString(value: String) = apply {
	putShort(value.length.toShort())
	put(value.toByteArray())
}