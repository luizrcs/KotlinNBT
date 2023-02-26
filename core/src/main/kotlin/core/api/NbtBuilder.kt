package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*

@DslMarker
annotation class NbtBuilder

inline fun buildTagList(
	name: String? = null,
	entries: TagListList = emptyList(),
	builder: TagListBuilder.() -> Unit,
) = TagListBuilder(name, entries).apply(builder).build()

inline fun buildTagCompound(
	name: String? = null,
	entries: TagCompoundMap = emptyMap(),
	builder: TagCompoundBuilder.() -> Unit,
) = TagCompoundBuilder(name, entries).apply(builder).build()

inline fun buildNbt(
	name: String? = null,
	entries: TagCompoundMap = emptyMap(),
	builder: TagCompoundBuilder.() -> Unit,
) = buildTagCompound(name, entries, builder)

@NbtBuilder
abstract class TagBuilder<T : TagAny, U : Any>(protected val name: String?, @PublishedApi internal val entries: U) {
	abstract fun build(): T
}

class TagCompoundBuilder @PublishedApi internal constructor(name: String?, entries: TagCompoundMap = emptyMap()) :
	TagBuilder<TagCompound, MutableTagCompoundMap>(name, MutableTagCompoundMap().apply { putAll(entries) }) {
	
	override fun build() = TagCompound(entries, name)
	
	fun put(name: String, tag: TagAny) = entries.put(name, tag)
	
	fun put(name: String, byte: Byte) = put(name, TagByte(byte, name))
	fun put(name: String, short: Short) = put(name, TagShort(short, name))
	fun put(name: String, int: Int) = put(name, TagInt(int, name))
	fun put(name: String, long: Long) = put(name, TagLong(long, name))
	fun put(name: String, float: Float) = put(name, TagFloat(float, name))
	fun put(name: String, double: Double) = put(name, TagDouble(double, name))
	fun put(name: String, byteArray: ByteArray) = put(name, TagByteArray(byteArray, name))
	fun put(name: String, string: String) = put(name, TagString(string, name))
	fun put(name: String, intArray: IntArray) = put(name, TagIntArray(intArray, name))
	fun put(name: String, longArray: LongArray) = put(name, TagLongArray(longArray, name))
	
	fun putTagList(name: String, builder: TagListBuilder.() -> Unit) = put(name, TagListBuilder(name).apply(builder).build())
	fun putTagCompound(name: String, builder: TagCompoundBuilder.() -> Unit) = put(name, TagCompoundBuilder(null).apply(builder).build())
	
	@PublishedApi internal fun List<*>.toListOfByte() = map { TagByte(it as Byte) }
	@PublishedApi internal fun List<*>.toListOfShort() = map { TagShort(it as Short) }
	@PublishedApi internal fun List<*>.toListOfInt() = map { TagInt(it as Int) }
	@PublishedApi internal fun List<*>.toListOfLong() = map { TagLong(it as Long) }
	@PublishedApi internal fun List<*>.toListOfFloat() = map { TagFloat(it as Float) }
	@PublishedApi internal fun List<*>.toListOfDouble() = map { TagDouble(it as Double) }
	@PublishedApi internal fun List<*>.toListOfByteArray() = map { TagByteArray(it as ByteArray) }
	@PublishedApi internal fun List<*>.toListOfString() = map { TagString(it as String) }
	@PublishedApi internal fun List<*>.toListOfIntArray() = map { TagIntArray(it as IntArray) }
	@PublishedApi internal fun List<*>.toListOfLongArray() = map { TagLongArray(it as LongArray) }
	
	@PublishedApi internal fun <T : TagAny> List<*>.toListOf() = map { (it as TagAny).clone(null) as T }
	
	inline fun <reified T> put(name: String, value: List<T>) {
		val tagList = when (val clazz = T::class) {
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
			Any::class          -> throw IllegalArgumentException("TagList elements must be of the same type")
			else                -> throw IllegalArgumentException("Unsupported type ${clazz.simpleName} for TagList")
		}
		
		put(name, tagList)
	}
}

class TagListBuilder @PublishedApi internal constructor(name: String?, entries: TagListList = emptyList()) :
	TagBuilder<TagList, MutableTagListList>(name, entries.toMutableList()) {
	
	private var elementsType = entries.firstOrNull()?.type
	
	override fun build() = TagList(elementsType ?: TAG_END, entries, false, name)
	
	fun add(tag: TagAny) {
		if (elementsType == null) elementsType = tag.type
		else if (elementsType != tag.type) throw IllegalArgumentException("TagList elements must be of the same type")
		
		entries.add(tag)
	}
	
	fun add(byte: Byte) = add(TagByte(byte))
	fun add(short: Short) = add(TagShort(short))
	fun add(int: Int) = add(TagInt(int))
	fun add(long: Long) = add(TagLong(long))
	fun add(float: Float) = add(TagFloat(float))
	fun add(double: Double) = add(TagDouble(double))
	fun add(byteArray: ByteArray) = add(TagByteArray(byteArray))
	fun add(string: String) = add(TagString(string))
	fun add(intArray: IntArray) = add(TagIntArray(intArray))
	fun add(longArray: LongArray) = add(TagLongArray(longArray))
	
	fun addTagList(builder: TagListBuilder.() -> Unit) = add(TagListBuilder(null).apply(builder).build())
	fun addTagCompound(builder: TagCompoundBuilder.() -> Unit) = add(TagCompoundBuilder(null).apply(builder).build())
}