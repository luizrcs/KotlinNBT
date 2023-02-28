package snbt

import br.com.luizrcs.nbt.core.tag.*
import core.api.*

object Snbt : NbtConverter<String>("snbt") {
	override val convertTagByte: Tag<Byte>.() -> String = { "${value}b" }
	override val convertTagShort: Tag<Short>.() -> String = { "${value}s" }
	override val convertTagInt: Tag<Int>.() -> String = { "$value" }
	override val convertTagLong: Tag<Long>.() -> String = { "${value}L" }
	override val convertTagFloat: Tag<Float>.() -> String = { "${value}f" }
	override val convertTagDouble: Tag<Double>.() -> String = { "${value}d" }
	override val convertTagByteArray: Tag<ByteArray>.() -> String = { "[B;${value.joinToString(",") { "${it}b" }}]" }
	override val convertTagString: Tag<String>.() -> String = { quoteString(value) }
	override val convertTagList: Tag<TagListList>.() -> String = { "[${value.joinToString(",") { it.convert("snbt") }}]" }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> String = { "{${value.entries.joinToString(",") { "${quoteString(it.key)}:${it.value.convert<String>("snbt")}" }}}" }
	override val convertTagIntArray: Tag<IntArray>.() -> String = { "[I;${value.joinToString(",")}}" }
	override val convertTagLongArray: Tag<LongArray>.() -> String = { "[L;${value.joinToString(",") { "${it}L" }}]" }
	
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