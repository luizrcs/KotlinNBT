package br.com.luizrcs.nbt.serialization

import kotlinx.serialization.*

@Serializable
data class Test(val id: Int, val name: String, val data: ByteArray)

fun main() {
	println(encodeToNbt(Test(0, "nbt", byteArrayOf(0, 1, 2))))
}