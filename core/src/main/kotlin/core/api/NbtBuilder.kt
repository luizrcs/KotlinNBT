package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*

@DslMarker
annotation class NbtBuilder

@NbtBuilder
inline fun nbt(
	name: String? = null,
	entries: CompoundMap = emptyMap(),
	builder: TagCompoundBuilder.() -> Unit,
) = TagCompoundBuilder(name, entries).apply(builder).build()

@NbtBuilder
open class TagCompoundBuilder @PublishedApi internal constructor(private val name: String?, entries: CompoundMap = emptyMap()) {
	
	@PublishedApi internal val entries = MutableCompoundMap().apply { putAll(entries) }
	
	@NbtBuilder val byte = TagCompoundEntryType(tagByteFactory)
	@NbtBuilder val short = TagCompoundEntryType(tagShortFactory)
	@NbtBuilder val int = TagCompoundEntryType(tagIntFactory)
	@NbtBuilder val long = TagCompoundEntryType(tagLongFactory)
	@NbtBuilder val float = TagCompoundEntryType(tagFloatFactory)
	@NbtBuilder val double = TagCompoundEntryType(tagDoubleFactory)
	@NbtBuilder val byteArray = TagCompoundEntryType(tagByteArrayFactory)
	@NbtBuilder val string = TagCompoundEntryType(tagStringFactory)
	@NbtBuilder val list = TagCompoundListEntryType()
	@NbtBuilder val compound = TagCompoundEntryType(tagCompoundFactory)
	@NbtBuilder val intArray = TagCompoundEntryType(tagIntArrayFactory)
	@NbtBuilder val longArray = TagCompoundEntryType(tagLongArrayFactory)
	
	@NbtBuilder
	fun listOf(vararg tagCompounds: TagCompoundBuilder.() -> Unit) = tagCompounds.map { TagCompoundBuilder(null).apply(it).build() }
	
	@NbtBuilder
	inline fun compound(
		entries: CompoundMap = emptyMap(),
		builder: TagCompoundBuilder.() -> Unit,
	) = TagCompoundBuilder(null, entries).apply(builder).build()
	
	open fun build() = TagCompound(entries, name)
	
	inner class TagCompoundEntryType<T : Any> internal constructor(private val factory: (T, String) -> TagAny) {
		operator fun set(name: String, value: T) {
			entries[name] = factory(value, name)
		}
	}
	
	inner class TagCompoundListEntryType {
		fun List<*>.toListOfByte() = map { TagByte(it as Byte) }
		fun List<*>.toListOfShort() = map { TagShort(it as Short) }
		fun List<*>.toListOfInt() = map { TagInt(it as Int) }
		fun List<*>.toListOfLong() = map { TagLong(it as Long) }
		fun List<*>.toListOfFloat() = map { TagFloat(it as Float) }
		fun List<*>.toListOfDouble() = map { TagDouble(it as Double) }
		fun List<*>.toListOfByteArray() = map { TagByteArray(it as ByteArray) }
		fun List<*>.toListOfString() = map { TagString(it as String) }
		fun List<*>.toListOfIntArray() = map { TagIntArray(it as IntArray) }
		fun List<*>.toListOfLongArray() = map { TagLongArray(it as LongArray) }
		
		@PublishedApi
		internal fun <T : TagAny> List<*>.toListOf() = map { (it as TagAny).clone(null) } as List<T>
		
		fun set(name: String, value: TagList) {
			entries[name] = value
		}
		
		inline operator fun <reified T> set(name: String, value: List<T>) {
			entries[name] = when (val clazz = T::class) {
				Byte::class         -> TagList(TAG_BYTE, value.toListOfByte(), false)
				Short::class        -> TagList(TAG_SHORT, value.toListOfShort(), false)
				Integer::class      -> TagList(TAG_INT, value.toListOfInt(), false)
				Long::class         -> TagList(TAG_LONG, value.toListOfLong(), false)
				Float::class        -> TagList(TAG_FLOAT, value.toListOfFloat(), false)
				Double::class       -> TagList(TAG_DOUBLE, value.toListOfDouble(), false)
				ByteArray::class    -> TagList(TAG_BYTE_ARRAY, value.toListOfByteArray(), false)
				String::class       -> TagList(TAG_STRING, value.toListOfString(), false)
				IntArray::class     -> TagList(TAG_INT_ARRAY, value.toListOfIntArray(), false)
				LongArray::class    -> TagList(TAG_LONG_ARRAY, value.toListOfLongArray(), false)
				TagByte::class      -> TagList(TAG_BYTE, value.toListOf<TagByte>(), false)
				TagShort::class     -> TagList(TAG_SHORT, value.toListOf<TagShort>(), false)
				TagInt::class       -> TagList(TAG_INT, value.toListOf<TagInt>(), false)
				TagLong::class      -> TagList(TAG_LONG, value.toListOf<TagLong>(), false)
				TagFloat::class     -> TagList(TAG_FLOAT, value.toListOf<TagFloat>(), false)
				TagDouble::class    -> TagList(TAG_DOUBLE, value.toListOf<TagDouble>(), false)
				TagByteArray::class -> TagList(TAG_BYTE_ARRAY, value.toListOf<TagByteArray>(), false)
				TagString::class    -> TagList(TAG_STRING, value.toListOf<TagString>(), false)
				TagList::class      -> TagList(TAG_LIST, value.toListOf<TagList>(), false)
				TagCompound::class  -> TagList(TAG_COMPOUND, value.toListOf<TagCompound>(), false)
				TagIntArray::class  -> TagList(TAG_INT_ARRAY, value.toListOf<TagIntArray>(), false)
				TagLongArray::class -> TagList(TAG_LONG_ARRAY, value.toListOf<TagLongArray>(), false)
				Any::class          -> throw IllegalArgumentException("Mixed types are not supported for TagList")
				else                -> throw IllegalArgumentException("Unsupported type ${clazz.simpleName} for TagList")
			}
		}
	}
	
	companion object {
		val tagByteFactory = { value: Byte, name: String -> TagByte(value, name) }
		val tagShortFactory = { value: Short, name: String -> TagShort(value, name) }
		val tagIntFactory = { value: Int, name: String -> TagInt(value, name) }
		val tagLongFactory = { value: Long, name: String -> TagLong(value, name) }
		val tagFloatFactory = { value: Float, name: String -> TagFloat(value, name) }
		val tagDoubleFactory = { value: Double, name: String -> TagDouble(value, name) }
		val tagByteArrayFactory = { value: ByteArray, name: String -> TagByteArray(value, name) }
		val tagStringFactory = { value: String, name: String -> TagString(value, name) }
		val tagCompoundFactory = { value: TagCompoundBuilder.() -> Unit, name: String -> TagCompoundBuilder(name).apply(value).build() }
		val tagIntArrayFactory = { value: IntArray, name: String -> TagIntArray(value, name) }
		val tagLongArrayFactory = { value: LongArray, name: String -> TagLongArray(value, name) }
	}
}
