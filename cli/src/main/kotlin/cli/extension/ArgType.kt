package br.com.luizrcs.nbt.cli.extension

import kotlinx.cli.ArgType
import java.io.*

object CustomArgType {
	object File : ArgType<java.io.File>(true) {
		override val description = "{ File }"
		
		override fun convert(value: kotlin.String, name: kotlin.String) = File(value)
	}
}