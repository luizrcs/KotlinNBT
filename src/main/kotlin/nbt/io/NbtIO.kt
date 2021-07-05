package br.com.luizrcs.nbt.io

import br.com.luizrcs.nbt.extension.*
import br.com.luizrcs.nbt.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.tag.*
import br.com.luizrcs.nbt.tag.TagType.*
import java.io.*
import java.nio.*
import java.util.zip.*

object NbtIO {
	
	private fun read(byteBuffer: ByteBuffer): TagAny {
		val id = byteBuffer.byte
		val name = byteBuffer.string
		
		return if (id == TAG_COMPOUND.id) TagCompound(byteBuffer, name) else Tag.read(id, byteBuffer)
	}
	
	fun read(inputStream: BufferedInputStream, compression: Compression = NONE): TagAny {
		val byteArray = when (compression) {
			NONE -> inputStream
			GZIP -> GZIPInputStream(inputStream)
			ZLIB -> InflaterInputStream(inputStream)
		}.use(InputStream::readBytes)
		
		return read(ByteBuffer.wrap(byteArray))
	}
	
	fun read(file: File, compression: Compression = NONE) =
		read(file.inputStream().buffered(), compression).tagCompound
	
	fun write(tagCompound: TagCompound, outputStream: BufferedOutputStream, compression: Compression = NONE) {
		val tagCompoundSize = (tagCompound.name?.toByteArray()?.size ?: 0) + tagCompound.sizeInBytes
		val byteBuffer = ByteBuffer.allocate(Byte.SIZE_BYTES + Short.SIZE_BYTES + tagCompoundSize)
		val byteArray = byteBuffer.also { tagCompound.writeRoot(it) }.array()
		
		when (compression) {
			NONE -> outputStream
			GZIP -> GZIPOutputStream(outputStream)
			ZLIB -> DeflaterOutputStream(outputStream)
		}.exhaustive.use { it.write(byteArray) }
	}
	
	fun write(tagCompound: TagCompound, file: File, compression: Compression = NONE) =
		write(tagCompound, file.outputStream().buffered(), compression)
	
	enum class Compression(private val id: Int) {
		
		NONE(0), GZIP(1), ZLIB(2);
		
		operator fun component1() = id
		
		companion object: LinkedHashMap<Int, Compression>() {
			
			init {
				putAll(values().associateBy { (id) -> id })
			}
			
			override fun get(id: Int) = super.get(id) ?: throw IllegalArgumentException("No compression method with id $id")
		}
	}
}
