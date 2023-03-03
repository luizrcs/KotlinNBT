package br.com.luizrcs.nbt.snbt

import br.com.luizrcs.nbt.core.api.*

@OptIn(ExperimentalSnbtNbtConverter::class)
fun main() {
	SnbtNbtConverter.registerConverter()
	
	val nbt = buildNbt {
		putTagCompound("testCompound") {
			put("fibonacci", intArrayOf(1, 1, 2, 3, 5, 8, 13, 21))
		}
		putTagList("testList") {
			addTagCompound {
				put("firstString", "I'm the first String :)")
				put("secondString", "I'm the second String, but order is not guaranteed :/")
				put("justAnInteger", 1)
			}
		}
		put("timestamp", System.currentTimeMillis())
	}
	
	val snbt = SnbtNbtConverter.convertFromTag(nbt)
	
	println("SNBT:")
	println(snbt)
	println()
	
	if (snbt != null) {
		println("NBT:")
		println(SnbtNbtConverter.convertToTag(snbt))
	}
}