@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
)

package br.com.luizrcs.nbt.snbt

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.snbt.SnbtNbtConverter.*

@ExperimentalSnbtNbtConverter
inline fun SnbtNbtConverter(builder: SnbtNbtConverterBuilder.() -> Unit): SnbtNbtConverter =
	SnbtNbtConverterBuilder().apply(builder).build()

@ExperimentalSnbtNbtConverter
open class SnbtNbtConverter private constructor(
	private val conf: SnbtNbtConverterBuilder = SnbtNbtConverterBuilder(),
) : NbtConverter<String>("snbt") {
	override val convertTagByte: Tag<Byte>.() -> String? = { "${value}b" }
	override val convertTagShort: Tag<Short>.() -> String? = { "${value}s" }
	override val convertTagInt: Tag<Int>.() -> String? = { "$value" }
	override val convertTagLong: Tag<Long>.() -> String? = { "${value}L" }
	override val convertTagFloat: Tag<Float>.() -> String? = { "${value}f" }
	override val convertTagDouble: Tag<Double>.() -> String? = { "${value}d" }
	override val convertTagByteArray: Tag<ByteArray>.() -> String? = { "[B;${value.joinToString(",") { byte -> "${byte}b" }}]" }
	override val convertTagString: Tag<String>.() -> String? = { quoteString(value) }
	override val convertTagList: Tag<TagListList>.() -> String? = { "[${value.mapNotNull { tag -> tag.convert("snbt") }.joinToString(",")}]" }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> String? = { "{${value.entries.mapNotNull { (key, value) -> value.convert<String>("snbt")?.let { "$key:$it" } }.joinToString(",")}}" }
	override val convertTagIntArray: Tag<IntArray>.() -> String? = { "[I;${value.joinToString(",")}}" }
	override val convertTagLongArray: Tag<LongArray>.() -> String? = { "[L;${value.joinToString(",") { long -> "${long}L" }}]" }
	
	override fun convertFromTag(tag: TagAny): String? = tag.convert<String>("snbt")
	override fun convertToTag(value: String): TagAny? {
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
	
	class SnbtNbtConverterBuilder {
		fun build() = SnbtNbtConverter(this)
	}
	
	@ExperimentalSnbtNbtConverter
	companion object : SnbtNbtConverter()
}

/**
 * This annotation marks the conversion between NBT and SNBT as experimental.
 *
 * Beware using the annotated API especially if you're developing a library, since your library might become binary
 * incompatible with future versions of the `nbt-converter-snbt` module.
 *
 * Any usage of a declaration annotated with `@ExperimentalSnbtNbtConverter` must be accepted either by annotating that
 * usage with the [OptIn] annotation, e.g. `@OptIn(ExperimentalSnbtNbtConverter::class)`, or by using the compiler
 * argument `-opt-in=br.com.luizrcs.nbt.snbt.ExperimentalSnbtNbtConverter`.
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
annotation class ExperimentalSnbtNbtConverter