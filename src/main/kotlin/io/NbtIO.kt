package io.github.mrpng.nbt.io

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.extension.*
import io.github.mrpng.nbt.implementation.*
import io.github.mrpng.nbt.io.NbtIO.Compression.*
import java.io.*
import java.nio.*
import java.util.zip.*

object NbtIO {
	
	fun read(inputStream: BufferedInputStream, compression: Compression = NONE): TagAny {
		val byteArray = when (compression) {
			NONE -> inputStream
			GZIP -> GZIPInputStream(inputStream)
			ZLIB -> InflaterInputStream(inputStream)
		}.readBytes()
		
		return read(ByteBuffer.wrap(byteArray))
	}
	
	fun read(file: File, compression: Compression = NONE) =
		read(file.inputStream().buffered(), compression).asTagCompound
	
	fun write(outputStream: BufferedOutputStream, tagCompound: TagCompound, compression: Compression = NONE) {
		val tagCompoundSize = (tagCompound.name?.toByteArray()?.size ?: 0) + tagCompound.sizeInBytes
		val byteBuffer = ByteBuffer.allocate(Byte.SIZE_BYTES + Short.SIZE_BYTES + tagCompoundSize)
		val byteArray = byteBuffer.also { tagCompound.writeRoot(it) }.array()
		
		val _outputStream = when (compression) {
			NONE -> outputStream
			GZIP -> GZIPOutputStream(outputStream)
			ZLIB -> DeflaterOutputStream(outputStream)
		}.exhaustive
		_outputStream.use { it.write(byteArray) }
	}
	
	fun write(file: File, tagCompound: TagCompound, compression: Compression = NONE) = write(
		file.outputStream().buffered(),
		tagCompound,
		compression)
	
	private fun read(byteBuffer: ByteBuffer): TagAny {
		val id = byteBuffer.byte.toInt()
		val name = byteBuffer.string
		
		return if (id == TAG_COMPOUND.id) TagCompound(byteBuffer, name) else Tag.read(id, byteBuffer)
	}
	
	enum class Compression(val id: Int) {
		
		NONE(0), GZIP(1), ZLIB(2);
		
		companion object {
			
			private val compressionByIndex = values().map { it.id to it }.toMap()
			
			operator fun get(id: Int) =
				compressionByIndex[id] ?: throw IllegalArgumentException("No compression method with id $id")
		}
	}
}
