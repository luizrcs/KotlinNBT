package br.com.luizrcs.nbt.serialization

import br.com.luizrcs.nbt.core.tag.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.system.*

private const val repetitions = 10000

val testA = TestA(
	TestB(
		floatArrayOf(1f, 1f, 2f, 3f, 5f, 8f, 13f, 21f)
	),
	listOf(
		TestC(
			"I'm the first String :)",
			"I'm the second String, but order is not guaranteed :/",
			1
		)
	),
	System.currentTimeMillis()
)

fun main() {
	println(testA)
	
	println()
	println("Encoding:")
	
	val nbt: TagAny
	val firstNbtEncodingTime = measureTimeMillis { nbt = Nbt.encodeToNbt(testA) }
	println(nbt)
	
	val repeatedNbtEncodingTime = (0 .. repetitions).sumOf { measureTimeMillis { Nbt.encodeToNbt(testA) } }
	
	println()
	println("Decoding:")
	
	val decodedTestA: TestA
	val firstNbtDecodingTime = measureTimeMillis { decodedTestA = Nbt.decodeFromNbt(nbt) }
	println(decodedTestA)
	
	val repeatedNbtDecodingTime = (0 .. repetitions).sumOf { measureTimeMillis { Nbt.decodeFromNbt<TestA>(nbt) } }
	
	println()
	println("Comparison: " + (decodedTestA == testA))
	
	println()
	println("First NBT encoding: ${firstNbtEncodingTime}ms")
	println("Repeated NBT encoding: ${repeatedNbtEncodingTime}ms")
	println("First NBT decoding: ${firstNbtDecodingTime}ms")
	println("Repeated NBT decoding: ${repeatedNbtDecodingTime}ms")
	
	val jsonElement: JsonElement
	val firstJsonEncodingTime = measureTimeMillis { jsonElement = Json.encodeToJsonElement(testA) }
	val repeatedJsonEncodingTime = (0 .. repetitions).sumOf { measureTimeMillis { Json.encodeToJsonElement(testA) } }
	
	val firstJsonDecodingTime = measureTimeMillis { Json.decodeFromJsonElement<TestA>(jsonElement) }
	val repeatedJsonDecodingTime = (0 .. repetitions).sumOf { measureTimeMillis { Json.decodeFromJsonElement<TestA>(jsonElement) } }
	
	println()
	println("First JSON encoding: ${firstJsonEncodingTime}ms")
	println("Repeated JSON encoding: ${repeatedJsonEncodingTime}ms")
	println("First JSON decoding: ${firstJsonDecodingTime}ms")
	println("Repeated JSON decoding: ${repeatedJsonDecodingTime}ms")
}

@Serializable
data class TestA(val testCompound: TestB, val testList: List<TestC>, val timestamp: Long)

@Serializable
data class TestB(val fibonacciWithoutZero: FloatArray) {
	
	override fun equals(other: Any?) = this === other
			|| javaClass == other?.javaClass
			&& fibonacciWithoutZero.contentEquals((other as TestB).fibonacciWithoutZero)
	
	override fun hashCode() = fibonacciWithoutZero.contentHashCode()
}

@Serializable
data class TestC(val firstString: String, val secondString: String, val justAnInteger: Int)