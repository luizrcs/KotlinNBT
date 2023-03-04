package br.com.luizrcs.nbt.json

import br.com.luizrcs.nbt.core.api.*

@OptIn(ExperimentalJsonNbtConverter::class)
fun main() {
	val nbt = buildNbt("root") {
		put("byte", 1.toByte())
		put("short", 1000.toShort())
		put("int", 300000)
		put("long", 400000000000L)
		put("float", 5f)
		put("double", 6.0)
		put("byteArray", byteArrayOf(1, 2, 3))
		put("string", "Hello, world!")
		putNbtList("intList") {
			add(100000)
			add(200000)
			add(300000)
		}
		putNbtList("listList") {
			addNbtList {
				add(1)
				add(2)
				add(3)
			}
			addNbtList {
				add(4)
				add(5)
				add(6)
			}
		}
		putNbtList("compoundList") {
			addNbtCompound { put("int", 100000) }
			addNbtCompound { put("int", 200000) }
			addNbtCompound { put("int", 300000) }
		}
		put("intArray", intArrayOf(100000, 200000, 300000))
		put("longArray", longArrayOf(100000000000L, 200000000000L, 300000000000L))
	}
	
	println("Original NBT:")
	println(nbt)
	println()
	
	val json = JsonNbtConverter.convertFromNbt(nbt)
	
	println("Converted JSON:")
	println(json)
	println()
	
	if (json != null) {
		println("Converted NBT:")
		println(JsonNbtConverter.convertToNbt(json))
	}
}