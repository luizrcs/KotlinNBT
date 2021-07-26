@file:OptIn(ExperimentalSerializationApi::class)

package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.io.*
import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.modules.*

object Nbt : BinaryFormat {
	override val serializersModule = EmptySerializersModule
	
	fun <T> encodeToNbt(serializer: SerializationStrategy<T>, value: T): TagAny {
		lateinit var tag: TagAny
		val encoder = NbtEncoder { tag = it }
		encoder.encodeSerializableValue(serializer, value)
		return tag
	}
	
	inline fun <reified T> encodeToNbt(value: T) = encodeToNbt(serializer(), value)
	
	override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T) =
		NbtIO.toByteArray(encodeToNbt(serializer, value))
	
	inline fun <reified T> encodeToByteArray(value: T) = encodeToByteArray(serializer(), value)
	
	fun <T> decodeFromNbt(deserializer: DeserializationStrategy<T>, tag: TagAny): T {
		val decoder = NbtDecoder(tag)
		return decoder.decodeSerializableValue(deserializer)
	}
	
	inline fun <reified T> decodeFromNbt(tag: TagAny) = decodeFromNbt(serializer<T>(), tag)
	
	override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray) =
		decodeFromNbt(deserializer, NbtIO.fromByteArray(bytes))
	
	inline fun <reified T> decodeFromByteArray(bytes: ByteArray) =
		decodeFromNbt(serializer<T>(), NbtIO.fromByteArray(bytes))
}