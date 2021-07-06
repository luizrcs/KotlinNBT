import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.io.*
import br.com.luizrcs.nbt.core.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.core.tag.*
import java.io.*
import java.util.zip.*
import kotlin.system.*

fun main() {
	val nbt = nbt("root") {
		compound("testCompound") {
			intArray["fibonacciWithoutZero"] = intArrayOf(1, 1, 2, 3, 5, 8, 13, 21)
		}
		list["testList"] = listOf(
			compound {
				string["firstString"] = "I'm the first String :)"
				string["secondString"] = "I'm the second String, but order is not guaranteed :/"
				int["justAnInteger"] = 1
			}
		)
		long["timestamp"] = System.currentTimeMillis()
	}
	
	val fibonacci: IntArray = nbt["testCompound"].tagCompound["fibonacciWithoutZero"].intArray
	val message: String = nbt["testList"].tagList[0].tagCompound["firstString"].string
	val timestamp: Long = nbt["timestamp"].long
	
	println(fibonacci.toList())
	println(message)
	println(timestamp)
	println()
	
	val file = File("test.nbt")
	NbtIO.write(nbt, file, GZIP)
	
	val nanoTime: Long
	
	val bytes = GZIPInputStream(FileInputStream(file)).use(InputStream::readBytes)
	val readNbt: TagAny
	nanoTime = measureNanoTime { readNbt = NbtIO.read(ByteArrayInputStream(bytes).buffered()) }
	println(readNbt)
	println()
	
	println(nanoTime.toString() + "ns")
	
	file.delete()
}
