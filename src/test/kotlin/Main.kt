import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.api.*
import io.github.mrpng.nbt.io.*
import io.github.mrpng.nbt.io.NbtIO.Compression.*
import java.io.*

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
	
	val fibonacci: IntArray = nbt["testCompound"].asTagCompound["fibonacciWithoutZero"].asIntArray
	val message: String = nbt["testList"].asTagList[0].asTagCompound["firstString"].asString
	val timestamp: Long = nbt["timestamp"].asLong
	
	println(fibonacci.toList())
	println(message)
	println(timestamp)
	
	NbtIO.write(nbt, File("test.nbt"), GZIP)
	println(NbtIO.read(File("test.nbt"), GZIP))
}
