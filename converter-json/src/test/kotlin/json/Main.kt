package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*

fun main() {
	JsonNbtConverter.registerConverter()
	
	val nbt = buildNbt("root") {
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
	
	val json = JsonNbtConverter.convertFromTag(nbt)
	
	println("JSON:")
	println(json)
	println()
	
	if (json != null) {
		println("NBT:")
		println(JsonNbtConverter.convertToTag(json))
	}
}