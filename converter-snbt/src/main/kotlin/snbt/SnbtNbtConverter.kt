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
	override val convertTagString: Tag<String>.() -> String? = { quoteString(value) }
	override val convertTagList: Tag<TagListList>.() -> String? = { "[${value.mapNotNull { tag -> tag.convert("snbt") }.joinToString(",")}]" }
	override val convertTagCompound: Tag<TagCompoundMap>.() -> String? = { "{${value.entries.mapNotNull { (key, value) -> value.convert<String>("snbt")?.let { "$key:$it" } }.joinToString(",")}}" }
	override val convertTagIntArray: Tag<IntArray>.() -> String? = { "[I;${value.joinToString(",")}]" }
	override val convertTagLongArray: Tag<LongArray>.() -> String? = { "[L;${value.joinToString(",") { long -> "${long}L" }}]" }
	
	override fun convertFromTag(tag: TagAny): String? = tag.convert<String>("snbt")
	
	@Suppress("JoinDeclarationAndAssignment")
	override fun convertToTag(value: String): TagAny? {
		val buffer = StringBuilder()
		fun flushBuffer() = buffer.toString().also { buffer.clear() }
		
		var index = 0
		
		lateinit var readIdentifier: () -> String
		lateinit var readTag: () -> TagAny
		
		lateinit var readValue: () -> TagAny
		lateinit var readCompound: () -> TagCompound
		lateinit var readListOrArray: () -> TagAny
		
		readIdentifier = {
			val condition = when (value[index]) {
				'"'  -> {
					index++
					{ value[index] != '"' || value[index - 1] == '\\' }
				}
				'\'' -> {
					index++
					{ value[index] != '\'' || value[index - 1] == '\\' }
				}
				else -> {
					{ value[index].toString().matches(identifierRegex) }
				}
			}
			
			while (condition()) buffer.append(value[index++])
			if (value[index] == '"' || value[index] == '\'') index++
			
			flushBuffer()
		}
		
		readTag = {
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
		
		readCompound = {
			index++
			buildTagCompound {
				while (value[index] != '}') {
					val key = readIdentifier()
					if (value[index++] != ':') throw IllegalArgumentException("Expected ':' at index $index")
					put(key, readTag())
					if (value[index] == ',' || value[index] == ';') index++
				}
				index++
			}
		}
		
		readListOrArray = {
			index++
			when (value.substring(index, index + 2)) {
				"B;" -> {
					index += 2
					val list = mutableListOf<TagByte>()
					while (value[index] != ']') {
						list.add(readValue() as TagByte)
						if (value[index] == ',' || value[index] == ';') index++
					}
					index++
					TagByteArray(list.map { it.value }.toByteArray())
				}
				"I;" -> {
					index += 2
					val list = mutableListOf<TagInt>()
					while (value[index] != ']') {
						list.add(readValue() as TagInt)
						if (value[index] == ',' || value[index] == ';') index++
					}
					index++
					TagIntArray(list.map { it.value }.toIntArray())
				}
				"L;" -> {
					index += 2
					val list = mutableListOf<TagLong>()
					while (value[index] != ']') {
						list.add(readValue() as TagLong)
						if (value[index] == ',' || value[index] == ';') index++
					}
					index++
					TagLongArray(list.map { it.value }.toLongArray())
				}
				else -> {
					buildTagList {
						while (value[index] != ']') {
							add(readTag())
							if (value[index] == ',' || value[index] == ';') index++
						}
						index++
					}
				}
			}
		}
		
		return readTag()
	}
	
	private fun quoteString(string: String) = if ('\'' in string) "\"${string.replace("\"", "\\\"")}\"" else "'$string'"
	
	class SnbtNbtConverterBuilder {
		fun build() = SnbtNbtConverter(this)
	}
	
	@ExperimentalSnbtNbtConverter
	companion object : SnbtNbtConverter() {
		private val identifierRegex = "[0-9A-Za-z_\\-+.]+".toRegex()
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