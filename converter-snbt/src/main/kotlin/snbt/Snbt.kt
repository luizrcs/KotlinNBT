package br.com.luizrcs.nbt.snbt

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*

object Snbt : NbtConverter<String>("snbt") {
	override val convertTagByte: Tag<Byte>.() -> String? = { "${value}b" }
	override val convertTagShort: Tag<Short>.() -> String? = { "${value}s" }
	override val convertTagInt: Tag<Int>.() -> String? = { "$value" }
	override val convertTagLong: Tag<Long>.() -> String? = { "${value}L" }
	override val convertTagFloat: Tag<Float>.() -> String? = { "${value}f" }
	override val convertTagDouble: Tag<Double>.() -> String? = { "${value}d" }
	override val convertTagByteArray: Tag<ByteArray>.() -> String? = { "[B;${value.joinToString(",") { byte -> "${byte}b" }}]" }
	override val convertTagString: Tag<String>.() -> String? = { quoteString(value) }
	override val convertTagList: Tag<TagListList>.() -> String? = { value.mapNotNull { tag -> tag.convert("snbt") }.let { list -> "[${list.joinToString(",")}]" } }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> String? = { "{${value.entries.joinToString(",") { (key, value) -> "${quoteString(key)}:${value.convert<String>("snbt")}" }}}" }
	override val convertTagIntArray: Tag<IntArray>.() -> String? = { "[I;${value.joinToString(",")}}" }
	override val convertTagLongArray: Tag<LongArray>.() -> String? = { "[L;${value.joinToString(",") { long -> "${long}L" }}]" }
	
	override fun convert(tag: TagAny) = tag.convert<String>("snbt")
	
	override fun parse(value: String): TagAny? {
		var buffer = StringBuilder()
		
		fun flushBuffer(): String {
			val result = buffer.toString()
			buffer = StringBuilder()
			return result
		}
		
		value.forEach { char ->
		
		}
		
		return null
	}
	
	private fun quoteString(string: String) = if ('\'' in string) "\"${string.replace("\"", "\\\"")}\"" else "'$string'"
}