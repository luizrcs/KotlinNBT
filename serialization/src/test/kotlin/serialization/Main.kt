package br.com.luizrcs.nbt.serialization

import kotlinx.serialization.*
import java.util.*

@Serializable
data class TestA(val testCompound: TestB, val testList: List<TestC>, val timestamp: Long)

@Serializable
data class TestB(val fibonacciWithoutZero: IntArray) {
	
	override fun equals(other: Any?) = this === other
			|| javaClass == other?.javaClass
			&& fibonacciWithoutZero.contentEquals((other as TestB).fibonacciWithoutZero)
	
	override fun hashCode() = fibonacciWithoutZero.contentHashCode()
}

@Serializable
data class TestC(val firstString: String, val secondString: String, val justAnInteger: Int)

fun main() {
	println("Encode:")
	
	val testA = TestA(
		TestB(
			intArrayOf(1, 1, 2, 3, 5, 8, 13, 21)
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
	println(testA)
	
	val nbt = Nbt.encodeToNbt(testA)
	println(nbt)
	
	println()
	println("Decode:")
	
	val decodedTestA = Nbt.decodeFromNbt<TestA>(nbt)
	println(decodedTestA)
	
	println()
	println("Comparison: " + (decodedTestA == testA))
}