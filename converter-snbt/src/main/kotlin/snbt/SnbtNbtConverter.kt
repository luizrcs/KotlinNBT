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
	
	override val convertTagEnd: Tag<Nothing>.() -> String? = { "" }
	override val convertTagByte: Tag<Byte>.() -> String? = { "${value}b" }
	override val convertTagShort: Tag<Short>.() -> String? = { "${value}s" }
	override val convertTagInt: Tag<Int>.() -> String? = { "$value" }
	override val convertTagLong: Tag<Long>.() -> String? = { "${value}L" }
	override val convertTagFloat: Tag<Float>.() -> String? = { "${value}f" }
	override val convertTagDouble: Tag<Double>.() -> String? = { "${value}d" }
	override val convertTagByteArray: Tag<ByteArray>.() -> String? = { "[B;${value.joinToString(",") { byte -> "${byte}b" }}]" }
	override val convertTagString: Tag<String>.() -> String? = { value.quote() }
	override val convertTagList: Tag<TagListList>.() -> String? = { "[${value.mapNotNull { tag -> convertFromTag(tag) }.joinToString(",")}]" }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> String? =
		{ "{${value.entries.mapNotNull { (key, value) -> convertFromTag(value)?.let { "${key.quote(false)}:$it" } }.joinToString(",")}}" }
	override val convertTagIntArray: Tag<IntArray>.() -> String? = { "[I;${value.joinToString(",")}]" }
	override val convertTagLongArray: Tag<LongArray>.() -> String? = { "[L;${value.joinToString(",") { long -> "${long}L" }}]" }
	
	override fun convertFromTag(tag: TagAny): String? = tag.convert<String>("snbt")
	
	@Suppress("JoinDeclarationAndAssignment")
	override fun convertToTag(value: String): TagAny? {
		var index = 0
		
		val skipWhitespaces = { while (value[index].toString().matches(whitespaceRegex)) index++ }
		
		val skipColon = {
			skipWhitespaces()
			if (value[index] == ':') index++
			else throw IllegalArgumentException("Expected ':' at index $index")
			skipWhitespaces()
		}
		
		val skipSeparator = {
			skipWhitespaces()
			if (value[index].toString().matches(separatorRegex)) index++
			skipWhitespaces()
		}
		
		lateinit var readIdentifier: () -> String
		lateinit var readTag: () -> TagAny
		
		lateinit var readValue: () -> TagAny
		lateinit var readListOrArray: () -> TagAny
		lateinit var readCompound: () -> TagCompound
		
		readIdentifier = {
			val buffer = StringBuilder()
			fun flushBuffer() = buffer.toString().also { buffer.clear() }
			
			val condition = when (value[index]) {
				'"'  -> {
					index++ // Skip first '"'
					{ value[index] != '"' || value[index - 1] == '\\' }
				}
				'\'' -> {
					index++ // Skip first '\''
					{ value[index] != '\'' || value[index - 1] == '\\' }
				}
				else -> {
					{ value[index].toString().matches(identifierRegex) }
				}
			}
			
			while (condition()) buffer.append(value[index++])
			
			if (value[index].toString().matches(quoteRegex)) {
				index++ // Skip last '"' or '\''
				flushBuffer()
			} else flushBuffer().trim()
		}
		
		readTag = {
			skipWhitespaces()
			
			when (value[index]) {
				'{'  -> readCompound()
				'['  -> readListOrArray()
				else -> readValue()
			}
		}
		
		readValue = {
			val identifier = readIdentifier()
			when (value[index]) {
				'"', '\'' -> TagString(identifier)
				else      -> when (identifier.lowercase()) {
					"true"  -> TagByte(1)
					"false" -> TagByte(0)
					else    -> {
						val cutIdentifier = identifier.dropLast(1)
						when (identifier.last()) {
							'b'  -> cutIdentifier.toByteOrNull()?.let { TagByte(it) }
							's'  -> cutIdentifier.toShortOrNull()?.let { TagShort(it) }
							'l'  -> cutIdentifier.toLongOrNull()?.let { TagLong(it) }
							'f'  -> cutIdentifier.toFloatOrNull()?.let { TagFloat(it) }
							'd'  -> cutIdentifier.toDoubleOrNull()?.let { TagDouble(it) }
							else -> identifier.toIntOrNull()?.let { TagInt(it) } ?: identifier.toDoubleOrNull()?.let { TagDouble(it) }
						} ?: TagString(identifier)
					}
				}
			}
		}
		
		readListOrArray = {
			index++ // Skip '['
			skipWhitespaces()
			
			@Suppress("UNCHECKED_CAST")
			fun <T : Any> readArray(): List<T> {
				index += 2 // Skip 'B;', 'I;' or 'L;'
				skipWhitespaces()
				
				val list = mutableListOf<Tag<T>>()
				while (value[index] != ']') {
					list.add(readValue() as Tag<T>)
					skipSeparator()
				}
				index++ // Skip ']'
				
				return list.map { it.value }
			}
			
			when (value.substring(index, index + 2)) {
				"B;" -> TagByteArray(readArray<Byte>().toByteArray())
				"I;" -> TagIntArray(readArray<Int>().toIntArray())
				"L;" -> TagLongArray(readArray<Long>().toLongArray())
				else -> {
					buildTagList {
						while (value[index] != ']') {
							add(readTag())
							skipSeparator()
						}
						index++ // Skip ']'
					}
				}
			}
		}
		
		readCompound = {
			index++ // Skip '{'
			skipWhitespaces()
			
			buildTagCompound {
				while (value[index] != '}') {
					val key = readIdentifier()
					skipColon()
					put(key, readTag())
					skipSeparator()
				}
				index++ // Skip '}'
			}
		}
		
		return readTag()
	}
	
	private fun String.startsOrEndsWithSpace() = startsWith(' ') || endsWith(' ')
	
	private fun String.quote(forceQuotes: Boolean = true) = when {
		!forceQuotes && matches(identifierRegex) && !startsOrEndsWithSpace() -> this
		contains('\'')                                                       -> "\"${replace("\"", "\\\"")}\""
		else                                                                 -> "'$this'"
	}
	
	class SnbtNbtConverterBuilder {
		fun build() = SnbtNbtConverter(this)
	}
	
	@ExperimentalSnbtNbtConverter
	companion object : SnbtNbtConverter() {
		private val identifierRegex = "[0-9A-Za-z_\\-+. ]+".toRegex()
		private val quoteRegex = "[\"']".toRegex()
		private val separatorRegex = "[,;]".toRegex()
		private val whitespaceRegex = "[ \t\n\r]+".toRegex()
	}
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