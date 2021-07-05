import br.com.luizrcs.nbt.api.*
import br.com.luizrcs.nbt.io.*
import br.com.luizrcs.nbt.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.tag.*
import java.io.*
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
	
	println(measureNanoTime { NbtIO.write(nbt, File("test.nbt"), GZIP) })
	
	val readNbt: TagCompound
	println(measureNanoTime { readNbt = NbtIO.read(File("test.nbt"), GZIP) })
	println(readNbt)
}
