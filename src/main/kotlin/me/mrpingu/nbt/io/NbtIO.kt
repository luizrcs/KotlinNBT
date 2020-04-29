package me.mrpingu.nbt.io

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import me.mrpingu.nbt.extension.*
import me.mrpingu.nbt.implementation.*
import me.mrpingu.nbt.io.NbtIO.CompressionMethod.*
import java.io.*
import java.nio.*
import java.util.zip.*

object NbtIO {
	
	fun to(outputStream: BufferedOutputStream, tagCompound: TagCompound, compressionMethod: CompressionMethod = NONE) {
		val byteBuffer = ByteBuffer.allocate(Byte.SIZE_BYTES + Short.SIZE_BYTES + (tagCompound.name?.toByteArray()?.size ?: 0) + tagCompound.sizeInBytes)
		val byteArray = byteBuffer.also { tagCompound.writeRoot(it) }.array()
		
		when (compressionMethod) {
			NONE -> outputStream
			GZIP -> GZIPOutputStream(outputStream)
			ZLIB -> DeflaterOutputStream(outputStream)
		}.exhaustive.use { it.write(byteArray) }
	}
	
	fun to(file: File, tagCompound: TagCompound, compressionMethod: CompressionMethod = NONE) = to(
		file.outputStream().buffered(),
		tagCompound,
		compressionMethod)
	
	fun from(inputStream: BufferedInputStream, compressionMethod: CompressionMethod = NONE): TagAny {
		val byteArray = when (compressionMethod) {
			NONE -> inputStream
			GZIP -> GZIPInputStream(inputStream)
			ZLIB -> InflaterInputStream(inputStream)
		}.readBytes()
		
		return from(ByteBuffer.wrap(byteArray))
	}
	
	fun from(file: File, compressionMethod: CompressionMethod = NONE) = from(file.inputStream().buffered(),
		compressionMethod).asTagCompound
	
	private fun from(byteBuffer: ByteBuffer): TagAny {
		val id = byteBuffer.byte.toInt()
		val name = byteBuffer.string
		
		return if (id == TAG_COMPOUND.id) TagCompound(byteBuffer, name) else Tag.read(id, byteBuffer)
	}
	
	enum class CompressionMethod(val id: Int) {
		
		NONE(0), GZIP(1), ZLIB(2);
		
		companion object {
			
			private val compressionMethodByIndex = values().map { it.id to it }.toMap()
			
			operator fun get(id: Int) = compressionMethodByIndex[id] ?: throw IllegalArgumentException("No compression method with id $id")
		}
	}
}
