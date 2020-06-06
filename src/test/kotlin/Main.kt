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
	
	NbtIO.write(File("test.nbt"), nbt, GZIP)
	println(NbtIO.read(File("test.nbt"), GZIP))
}
