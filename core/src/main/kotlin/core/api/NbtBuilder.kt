package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.TagType.*

@DslMarker
annotation class NbtBuilder

@NbtBuilder
inline fun nbt(
	name: String? = null,
	entries: CompoundMap = emptyMap(),
	builder: TagCompoundBuilder.() -> Unit
) = TagCompoundBuilder(name, entries).apply(builder).build()

@NbtBuilder
open class TagCompoundBuilder(private val name: String?, entries: CompoundMap) {
	
	@PublishedApi internal val entries = mutableMapOf<String, TagAny>().apply { putAll(entries) }
	
	@NbtBuilder val byte = TagCompoundEntry<Byte> { value, name -> TagByte(value, name) }
	@NbtBuilder val short = TagCompoundEntry<Short> { value, name -> TagShort(value, name) }
	@NbtBuilder val int = TagCompoundEntry<Int> { value, name -> TagInt(value, name) }
	@NbtBuilder val long = TagCompoundEntry<Long> { value, name -> TagLong(value, name) }
	@NbtBuilder val float = TagCompoundEntry<Float> { value, name -> TagFloat(value, name) }
	@NbtBuilder val double = TagCompoundEntry<Double> { value, name -> TagDouble(value, name) }
	@NbtBuilder val byteArray = TagCompoundEntry<ByteArray> { value, name -> TagByteArray(value, name) }
	@NbtBuilder val string = TagCompoundEntry<String> { value, name -> TagString(value, name) }
	@NbtBuilder val list = TagCompoundListEntry()
	@NbtBuilder val intArray = TagCompoundEntry<IntArray> { value, name -> TagIntArray(value, name) }
	@NbtBuilder val longArray = TagCompoundEntry<LongArray> { value, name -> TagLongArray(value, name) }
	
	@NbtBuilder
	inline fun compound(name: String, entries: CompoundMap = emptyMap(), builder: TagCompoundBuilder.() -> Unit) {
		this.entries[name] = TagCompoundBuilder(name, entries).apply(builder).build()
	}
	
	@NbtBuilder
	inline fun compound(entries: CompoundMap = emptyMap(), builder: TagCompoundBuilder.() -> Unit) =
		TagCompoundBuilder(null, entries).apply(builder).build()
	
	open fun build() = TagCompound(entries, name)
	
	inner class TagCompoundEntry<T : Any> internal constructor(private val factory: (T, String) -> TagAny) {
		
		operator fun set(name: String, value: T) {
			entries[name] = factory(value, name)
		}
	}
	
	inner class TagCompoundListEntry {
		
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
		
		fun set(name: String, value: TagList) {
			entries[name] = value
		}
		
		inline operator fun <reified T> set(name: String, value: List<T>) {
			set(
				name, when (val kClass = T::class) {
					Byte::class         -> TagList(TAG_BYTE, value.toListOfByte(), false)
					Short::class        -> TagList(TAG_SHORT, value.toListOfShort(), false)
					Int::class          -> TagList(TAG_INT, value.toListOfInt(), false)
					Long::class         -> TagList(TAG_LONG, value.toListOfLong(), false)
					Float::class        -> TagList(TAG_FLOAT, value.toListOfFloat(), false)
					Double::class       -> TagList(TAG_DOUBLE, value.toListOfDouble(), false)
					ByteArray::class    -> TagList(TAG_BYTE_ARRAY, value.toListOfByteArray(), false)
					String::class       -> TagList(TAG_STRING, value.toListOfString(), false)
					IntArray::class     -> TagList(TAG_INT_ARRAY, value.toListOfIntArray(), false)
					LongArray::class    -> TagList(TAG_LONG_ARRAY, value.toListOfLongArray(), false)
					TagByte::class      -> TagList(TAG_BYTE, value as List<TagByte>, false)
					TagShort::class     -> TagList(TAG_SHORT, value as List<TagShort>, false)
					TagInt::class       -> TagList(TAG_INT, value as List<TagInt>, false)
					TagLong::class      -> TagList(TAG_LONG, value as List<TagLong>, false)
					TagFloat::class     -> TagList(TAG_FLOAT, value as List<TagFloat>, false)
					TagDouble::class    -> TagList(TAG_DOUBLE, value as List<TagDouble>, false)
					TagByteArray::class -> TagList(TAG_BYTE_ARRAY, value as List<TagByteArray>, false)
					TagString::class    -> TagList(TAG_STRING, value as List<TagString>, false)
					TagList::class      -> TagList(TAG_LIST, value as List<TagList>, false)
					TagCompound::class  -> TagList(TAG_COMPOUND, value as List<TagCompound>, false)
					TagIntArray::class  -> TagList(TAG_INT_ARRAY, value as List<TagIntArray>, false)
					TagLongArray::class -> TagList(TAG_LONG_ARRAY, value as List<TagLongArray>, false)
					Any::class          -> throw IllegalArgumentException("Mixed types are not supported for TagList")
					else                -> throw IllegalArgumentException("Unsupported type ${kClass.simpleName} for TagList")
				}
			)
		}
	}
}
