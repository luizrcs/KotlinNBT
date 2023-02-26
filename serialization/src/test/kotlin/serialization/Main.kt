package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.system.*

private const val repetitions = 10000

val testA = TestA(
	testCompound = TestB(
		fibonacci = floatArrayOf(1f, 1f, 2f, 3f, 5f, 8f, 13f, 21f)
	),
	testList = listOf(
		TestC(
			firstString = "I'm the first String :)",
			secondString = "I'm the second String, but order is not guaranteed :/",
			justAnInteger = 1
		)
	),
	timestamp = System.currentTimeMillis()
)

fun main() {
	println("Encoding:")
	
	val nbt: TagAny
	val firstNbtEncodingTime = measureTimeMillis { nbt = Nbt.encodeToNbt(testA) }
	println(nbt)
	
	val repeatedNbtEncodingTime = (0 .. repetitions).sumOf { measureNanoTime { Nbt.encodeToNbt(testA) } }
	
	println()
	println("Decoding:")
	
	val decodedTestA: TestA
	val firstNbtDecodingTime = measureTimeMillis { decodedTestA = Nbt.decodeFromNbt(nbt) }
	println(decodedTestA)
	
	val repeatedNbtDecodingTime = (0 .. repetitions).sumOf { measureNanoTime { Nbt.decodeFromNbt<TestA>(nbt) } }
	
	println()
	println("Comparison: " + (decodedTestA == testA))
	
	println()
	println("First NBT encoding: ${firstNbtEncodingTime}ms")
	println("Repeated NBT encoding: ${repeatedNbtEncodingTime}ns")
	println("First NBT decoding: ${firstNbtDecodingTime}ms")
	println("Repeated NBT decoding: ${repeatedNbtDecodingTime}ns")
	
	val jsonElement: JsonElement
	val firstJsonEncodingTime = measureTimeMillis { jsonElement = Json.encodeToJsonElement(testA) }
	val repeatedJsonEncodingTime = (0 .. repetitions).sumOf { measureNanoTime { Json.encodeToJsonElement(testA) } }
	
	val firstJsonDecodingTime = measureTimeMillis { Json.decodeFromJsonElement<TestA>(jsonElement) }
	val repeatedJsonDecodingTime = (0 .. repetitions).sumOf { measureNanoTime { Json.decodeFromJsonElement<TestA>(jsonElement) } }
	
	println()
	println("First JSON encoding: ${firstJsonEncodingTime}ms")
	println("Repeated JSON encoding: ${repeatedJsonEncodingTime}ns")
	println("First JSON decoding: ${firstJsonDecodingTime}ms")
	println("Repeated JSON decoding: ${repeatedJsonDecodingTime}ns")
}

@Serializable
data class TestA(val testCompound: TestB, val testList: List<TestC>, val timestamp: Long)

@Serializable
data class TestB(val fibonacci: FloatArray) {
	override fun equals(other: Any?) = this === other
			|| javaClass == other?.javaClass
			&& fibonacci.contentEquals((other as TestB).fibonacci)
	
	override fun hashCode() = fibonacci.contentHashCode()
}

@Serializable
data class TestC(val firstString: String, val secondString: String, val justAnInteger: Int)