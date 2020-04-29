package me.mrpingu.nbt

import me.mrpingu.nbt.api.*
import me.mrpingu.nbt.io.*
import java.io.*

fun main() {
	val nbt = nbt("root") {
		compound("testCompound") {
			list["testList"] = listOf(
				compound {
					string["a"] = "a"
				},
				compound {
					string["b"] = "b"
				}
			)
		}
		int["testInt"] = 0
	}
	
	NbtIO.to(File("test.nbt"), nbt)
	println(NbtIO.from(File("test.nbt")))
}
