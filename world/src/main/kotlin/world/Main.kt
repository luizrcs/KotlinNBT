package br.com.luizrcs.nbt.world

import br.com.luizrcs.nbt.core.io.*
import br.com.luizrcs.nbt.core.io.NbtIO.Compression.*
import java.io.*
import kotlin.io.path.*

fun main() {
	val regionX = 0
	val regionZ = 0
	val regionFile = Path("r.$regionX.$regionZ.mca")
	if (regionFile.exists()) {
		val bytes = regionFile.inputStream().use(InputStream::readBytes)
		var dataInputStream = DataInputStream(ByteArrayInputStream(bytes))
		
		val firstChunkInfo = dataInputStream.readInt()
		val firstChunkOffset = (firstChunkInfo ushr 8) * 4096
		val firstChunkLength = (firstChunkInfo and 0xff) * 4096
		
		println("$firstChunkOffset; $firstChunkLength")
		
		dataInputStream = DataInputStream(ByteArrayInputStream(bytes.copyOfRange(firstChunkOffset, firstChunkOffset + firstChunkLength)))
		println(dataInputStream.readInt())
		println(dataInputStream.readByte())
		val nbt = NbtIO.read(dataInputStream, ZLIB)
		println(nbt)
	} else println("File ${regionFile.absolutePathString()} doesn't exist")
}