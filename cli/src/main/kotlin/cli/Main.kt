package br.com.luizrcs.nbt.cli

import br.com.luizrcs.nbt.cli.extension.*
import br.com.luizrcs.nbt.core.io.*
import kotlinx.cli.*

private val compressionAlgorithms = listOf("none", "gzip", "zlib")

fun main(args: Array<String>) {
	val parser = ArgParser("nbt")
	val input by parser.argument(CustomArgType.File, description = "Input file")
	val compression by parser.option(
		ArgType.Choice(compressionAlgorithms, { it }),
		shortName = "c",
		description = "Compression method"
	).default("none")
	parser.parse(args)
	
	val nbt = NbtIO.read(input, NbtIO.Compression[compressionAlgorithms.indexOf(compression.lowercase())])
	println(nbt)
}