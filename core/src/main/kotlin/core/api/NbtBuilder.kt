@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
)

package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*

@DslMarker
annotation class NbtBuilder

inline fun buildTagList(
	name: String? = null,
	builder: TagListBuilder.() -> Unit,
): TagList = TagListBuilder(name).apply(builder).build()

inline fun buildTagCompound(
	name: String? = null,
	builder: TagCompoundBuilder.() -> Unit,
): TagCompound = TagCompoundBuilder(name).apply(builder).build()

inline fun buildNbt(
	name: String? = null,
	builder: TagCompoundBuilder.() -> Unit,
): TagCompound = buildTagCompound(name, builder)

@NbtBuilder
abstract class TagBuilder<T : TagAny, U : Any> internal constructor(protected val name: String?) {
	protected abstract val entries: U
	
	abstract fun build(): T
}

class TagCompoundBuilder @PublishedApi internal constructor(name: String?) :
	TagBuilder<TagCompound, MutableTagCompoundMap>(name) {
	
	override val entries = MutableTagCompoundMap()
	
	override fun build() = TagCompound(entries, name, false)
	
	fun put(name: String, tag: TagAny) {
		if (tag is TagEnd) throw IllegalArgumentException("Cannot add a TagEnd to a TagCompound")
		else entries[name] = tag
	}
	
	fun put(name: String, byte: Byte): Unit = put(name, TagByte(byte, name))
	fun put(name: String, short: Short): Unit = put(name, TagShort(short, name))
	fun put(name: String, int: Int): Unit = put(name, TagInt(int, name))
	fun put(name: String, long: Long): Unit = put(name, TagLong(long, name))
	fun put(name: String, float: Float): Unit = put(name, TagFloat(float, name))
	fun put(name: String, double: Double): Unit = put(name, TagDouble(double, name))
	fun put(name: String, byteArray: ByteArray): Unit = put(name, TagByteArray(byteArray, name))
	fun put(name: String, string: String): Unit = put(name, TagString(string, name))
	fun put(name: String, intArray: IntArray): Unit = put(name, TagIntArray(intArray, name))
	fun put(name: String, longArray: LongArray): Unit = put(name, TagLongArray(longArray, name))
	
	fun putTagList(name: String, builder: TagListBuilder.() -> Unit): Unit = put(name, TagListBuilder(name).apply(builder).build())
	fun putTagCompound(name: String, builder: TagCompoundBuilder.() -> Unit): Unit = put(name, TagCompoundBuilder(null).apply(builder).build())
	
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
	
	@Suppress("UNCHECKED_CAST")
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

class TagListBuilder @PublishedApi internal constructor(name: String?) :
	TagBuilder<TagList, MutableTagListList>(name) {
	
	override val entries = MutableTagListList()
	
	private var elementsType = entries.firstOrNull()?.type
	
	override fun build() = TagList(elementsType ?: TAG_END, entries, false, name)
	
	fun add(tag: TagAny) {
		if (elementsType == null) elementsType = tag.type
		else if (elementsType != tag.type) throw IllegalArgumentException("TagList elements must be of the same type")
		
		entries.add(tag)
	}
	
	fun add(byte: Byte): Unit = add(TagByte(byte))
	fun add(short: Short): Unit = add(TagShort(short))
	fun add(int: Int): Unit = add(TagInt(int))
	fun add(long: Long): Unit = add(TagLong(long))
	fun add(float: Float): Unit = add(TagFloat(float))
	fun add(double: Double): Unit = add(TagDouble(double))
	fun add(byteArray: ByteArray): Unit = add(TagByteArray(byteArray))
	fun add(string: String): Unit = add(TagString(string))
	fun add(intArray: IntArray): Unit = add(TagIntArray(intArray))
	fun add(longArray: LongArray): Unit = add(TagLongArray(longArray))
	
	fun addTagList(builder: TagListBuilder.() -> Unit): Unit = add(TagListBuilder(null).apply(builder).build())
	fun addTagCompound(builder: TagCompoundBuilder.() -> Unit): Unit = add(TagCompoundBuilder(null).apply(builder).build())
}