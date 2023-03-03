package br.com.luizrcs.nbt.core.io

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import java.io.*
import java.nio.*
import java.util.zip.*

object NbtIO {
	fun fromByteArray(byteArray: ByteArray): TagAny = read(ByteBuffer.wrap(byteArray))
	
	fun toByteArray(tag: TagAny): ByteArray = if (tag is TagCompound) {
		val tagCompoundSize = (tag.name?.toByteArray()?.size ?: 0) + tag.sizeInBytes
		val byteBuffer = ByteBuffer.allocate(Byte.SIZE_BYTES + Short.SIZE_BYTES + tagCompoundSize)
		byteBuffer.also { tag.writeRoot(it) }.array() as ByteArray
	} else byteArrayOf()
	
	private fun read(byteBuffer: ByteBuffer): TagAny {
		val id = byteBuffer.byte
		val name = byteBuffer.string
		
		return if (id == TAG_COMPOUND.id) TagCompound(byteBuffer, name) else Tag.read(id, byteBuffer)
	}
	
	fun read(inputStream: InputStream, compression: Compression = NONE): TagAny = fromByteArray(
		when (compression) {
			NONE -> inputStream
			GZIP -> GZIPInputStream(inputStream)
			ZLIB -> InflaterInputStream(inputStream)
		}.buffered().use(InputStream::readBytes)
	)
	
	fun read(file: File, compression: Compression = NONE): TagAny = read(file.inputStream(), compression).tagCompound
	
	fun write(tagCompound: TagCompound, outputStream: OutputStream, compression: Compression = NONE) {
		when (compression) {
			NONE -> outputStream
			GZIP -> GZIPOutputStream(outputStream)
			ZLIB -> DeflaterOutputStream(outputStream)
		}.exhaustive.buffered().use { it.write(toByteArray(tagCompound)) }
	}
	
	fun write(tagCompound: TagCompound, file: File, compression: Compression = NONE): Unit =
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