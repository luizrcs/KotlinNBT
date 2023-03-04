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
) : NbtConverter<String>() {
	
	override val convertNbtEnd: NbtBase<Nothing>.() -> String? = { "" }
	override val convertNbtByte: NbtBase<Byte>.() -> String? = { "${value}b" }
	override val convertNbtShort: NbtBase<Short>.() -> String? = { "${value}s" }
	override val convertNbtInt: NbtBase<Int>.() -> String? = { "$value" }
	override val convertNbtLong: NbtBase<Long>.() -> String? = { "${value}L" }
	override val convertNbtFloat: NbtBase<Float>.() -> String? = { "${value}f" }
	override val convertNbtDouble: NbtBase<Double>.() -> String? = { "${value}d" }
	override val convertNbtByteArray: NbtBase<ByteArray>.() -> String? = { "[B;${value.joinToString(",") { byte -> "${byte}b" }}]" }
	override val convertNbtString: NbtBase<String>.() -> String? = { value.quote() }
	override val convertNbtList: NbtBase<NbtListList>.() -> String? = { "[${value.mapNotNull { tag -> convertFromNbt(tag) }.joinToString(",")}]" }
	override val convertNbtCompound: NbtBase<NbtCompoundMap>.() -> String? =
		{ "{${value.entries.mapNotNull { (key, value) -> convertFromNbt(value)?.let { "${key.quote(false)}:$it" } }.joinToString(",")}}" }
	override val convertNbtIntArray: NbtBase<IntArray>.() -> String? = { "[I;${value.joinToString(",")}]" }
	override val convertNbtLongArray: NbtBase<LongArray>.() -> String? = { "[L;${value.joinToString(",") { long -> "${long}L" }}]" }
	
	override fun convertFromNbt(tag: NbtAny): String? = tag.convert(this)
	
	@Suppress("JoinDeclarationAndAssignment")
	override fun convertToNbt(value: String): NbtAny? {
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
		lateinit var readTag: () -> NbtAny
		
		lateinit var readValue: () -> NbtAny
		lateinit var readListOrArray: () -> NbtAny
		lateinit var readCompound: () -> NbtCompound
		
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
				'"', '\'' -> NbtString(identifier)
				else      -> when (identifier.lowercase()) {
					"true"  -> NbtByte(1)
					"false" -> NbtByte(0)
					else    -> {
						val cutIdentifier = identifier.dropLast(1)
						when (identifier.last()) {
							'b'  -> cutIdentifier.toByteOrNull()?.let { NbtByte(it) }
							's'  -> cutIdentifier.toShortOrNull()?.let { NbtShort(it) }
							'l'  -> cutIdentifier.toLongOrNull()?.let { NbtLong(it) }
							'f'  -> cutIdentifier.toFloatOrNull()?.let { NbtFloat(it) }
							'd'  -> cutIdentifier.toDoubleOrNull()?.let { NbtDouble(it) }
							else -> identifier.toIntOrNull()?.let { NbtInt(it) } ?: identifier.toDoubleOrNull()?.let { NbtDouble(it) }
						} ?: NbtString(identifier)
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
				
				val list = mutableListOf<NbtBase<T>>()
				while (value[index] != ']') {
					list.add(readValue() as NbtBase<T>)
					skipSeparator()
				}
				index++ // Skip ']'
				
				return list.map { it.value }
			}
			
			when (value.substring(index, index + 2)) {
				"B;" -> NbtByteArray(readArray<Byte>().toByteArray())
				"I;" -> NbtIntArray(readArray<Int>().toIntArray())
				"L;" -> NbtLongArray(readArray<Long>().toLongArray())
				else -> {
					buildNbtList {
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
			
			buildNbtCompound {
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