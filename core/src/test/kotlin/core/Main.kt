package br.com.luizrcs.nbt.core

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.io.*
import br.com.luizrcs.nbt.core.io.NbtIO.Compression.*
import br.com.luizrcs.nbt.core.tag.*
import java.io.*
import java.util.zip.*
import kotlin.system.*

fun main() {
	var nbt: NbtCompound
	
	val nbtBuilderTime = measureTimeMillis {
		nbt = buildNbt("root") {
			putNbtCompound("testCompound") {
				put("fibonacci", intArrayOf(1, 1, 2, 3, 5, 8, 13, 21))
			}
			putNbtList("testList") {
				addNbtCompound {
					put("firstString", "I'm the first String :)")
					put("secondString", "I'm the second String, but order is not guaranteed :/")
					put("justAnInteger", 1)
				}
			}
			put("timestamp", System.currentTimeMillis())
		}
	}
	
	val repeatedNbtBuilderTime = measureNanoTime {
		nbt = buildNbt("root") {
			putNbtCompound("testCompound") {
				put("fibonacci", intArrayOf(1, 1, 2, 3, 5, 8, 13, 21))
			}
			putNbtList("testList") {
				addNbtCompound {
					put("firstString", "I'm the first String :)")
					put("secondString", "I'm the second String, but order is not guaranteed :/")
					put("justAnInteger", 1)
				}
			}
			put("timestamp", System.currentTimeMillis())
		}
	}
	
	val fibonacci: IntArray = nbt.compound["testCompound"].compound["fibonacci"].intArray
	val message: String = nbt.compound["testList"].list[0].compound["firstString"].string
	val timestamp: Long = nbt.compound["timestamp"].long
	
	println(fibonacci.toList())
	println(message)
	println(timestamp)
	println()
	
	println("NBT builder: ${nbtBuilderTime}ms")
	println("Repeated NBT builder: ${repeatedNbtBuilderTime}ns")
	println()
	
	val file = File("test.nbt")
	NbtIO.write(nbt, file, GZIP)
	
	val nanoTime: Long
	
	val bytes = GZIPInputStream(FileInputStream(file)).use(InputStream::readBytes)
	val readNbt: NbtAny
	nanoTime = measureNanoTime { readNbt = NbtIO.read(ByteArrayInputStream(bytes).buffered()) }
	println(readNbt)
	println()
	
	println(nanoTime.toString() + "ns")
	
	file.delete()
}