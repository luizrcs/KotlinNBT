package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*

fun main() {
	Json.registerConverter()
	
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
	
	val json = Json.convert(nbt)
	
	println("JSON:")
	println(json)
	
	if (json != null) {
		println("NBT:")
		println(Json.parse(json))
	}
}