package br.com.luizrcs.nbt.serialization

import kotlinx.serialization.*

@Serializable
data class TestA(val testCompound: TestB, val testList: List<TestC>, val timestamp: Long)

@Serializable
data class TestB(val fibonacciWithoutZero: IntArray)

@Serializable
data class TestC(val firstString: String, val secondString: String, val justAnInteger: Int)

fun main() {
	println(
		encodeToNbt(
			TestA(
				TestB(intArrayOf(1, 1, 2, 3, 5, 8, 13, 21)),
				listOf(TestC("I'm the first String :)", "I'm the second String, but order is not guaranteed :/", 1)),
				System.currentTimeMillis()
			)
		)
	)
}