package br.com.luizrcs.nbt.core.io

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.core.tag.*
import java.io.*
import java.nio.*
import java.util.zip.*

object NbtIO {
	fun fromByteArray(byteArray: ByteArray): NbtAny = read(ByteBuffer.wrap(byteArray))
	
	fun toByteArray(tag: NbtAny): ByteArray = if (tag is NbtCompound) {
		val tagCompoundSize = (tag.name?.toByteArray()?.size ?: 0) + tag.sizeInBytes
		val byteBuffer = ByteBuffer.allocate(Byte.SIZE_BYTES + Short.SIZE_BYTES + tagCompoundSize)
		byteBuffer.also { tag.writeRoot(it) }.array() as ByteArray
	} else byteArrayOf()
	
	private fun read(byteBuffer: ByteBuffer): NbtAny {
		val id = byteBuffer.byte
		val name = byteBuffer.string
		return NbtBase.read(id, byteBuffer, name)
	}
	
	fun read(inputStream: InputStream, compression: Compression = NONE): NbtAny = fromByteArray(
		when (compression) {
			NONE -> inputStream
			GZIP -> GZIPInputStream(inputStream)
			ZLIB -> InflaterInputStream(inputStream)
		}.buffered().use(InputStream::readBytes)
	)
	
	fun read(file: File, compression: Compression = NONE): NbtAny = read(file.inputStream(), compression)
	
	fun write(tagCompound: NbtCompound, outputStream: OutputStream, compression: Compression = NONE) {
		when (compression) {
			NONE -> outputStream
			GZIP -> GZIPOutputStream(outputStream)
			ZLIB -> DeflaterOutputStream(outputStream)
		}.exhaustive.buffered().use { it.write(toByteArray(tagCompound)) }
	}
	
	fun write(tagCompound: NbtCompound, file: File, compression: Compression = NONE): Unit =
		write(tagCompound, file.outputStream(), compression)
	
	enum class Compression(private val id: Int) {
		NONE(0), GZIP(1), ZLIB(2);
		
		companion object : LinkedHashMap<Int, Compression>() {
			init {
				putAll(values().associateBy { it.id })
			}
			
			override fun get(key: Int): Compression = super.get(key)
				?: throw IllegalArgumentException("No compression method with id '$key'")
		}
	}
}