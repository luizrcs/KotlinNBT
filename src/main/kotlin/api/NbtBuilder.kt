package io.github.mrpng.nbt.api

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.implementation.*

@DslMarker
annotation class NbtBuilder

@NbtBuilder
inline fun nbt(
	name: String? = null,
	entries: CompoundMap = emptyMap(),
	lenient: Boolean = false,
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
	
	inner class TagCompoundEntry<T: Any>(private val initializer: (T, String) -> TagAny) {
		
		operator fun set(name: String, value: T) {
			entries[name] = initializer(value, name)
		}
	}
	
	inner class TagCompoundListEntry {
		
		fun toListOfByte(list: List<*>) = list.map { TagByte(it as Byte) }
		fun toListOfShort(list: List<*>) = list.map { TagShort(it as Short) }
		fun toListOfInt(list: List<*>) = list.map { TagInt(it as Int) }
		fun toListOfLong(list: List<*>) = list.map { TagLong(it as Long) }
		fun toListOfFloat(list: List<*>) = list.map { TagFloat(it as Float) }
		fun toListOfDouble(list: List<*>) = list.map { TagDouble(it as Double) }
		fun toListOfByteArray(list: List<*>) = list.map { TagByteArray(it as ByteArray) }
		fun toListOfString(list: List<*>) = list.map { TagString(it as String) }
		fun toListOfIntArray(list: List<*>) = list.map { TagIntArray(it as IntArray) }
		fun toListOfLongArray(list: List<*>) = list.map { TagLongArray(it as LongArray) }
		
		inline operator fun <reified T> set(name: String, value: List<T>) {
			set(name, when (T::class.java) {
				java.lang.Byte::class.java -> TagList(TAG_BYTE, toListOfByte(value), false)
				java.lang.Short::class.java -> TagList(TAG_SHORT, toListOfShort(value), false)
				java.lang.Integer::class.java -> TagList(TAG_INT, toListOfInt(value), false)
				java.lang.Long::class.java -> TagList(TAG_LONG, toListOfLong(value), false)
				java.lang.Float::class.java -> TagList(TAG_FLOAT, toListOfFloat(value), false)
				java.lang.Double::class.java -> TagList(TAG_DOUBLE, toListOfDouble(value), false)
				ByteArray::class.java -> TagList(TAG_BYTE_ARRAY, toListOfByteArray(value), false)
				String::class.java -> TagList(TAG_STRING, toListOfString(value), false)
				IntArray::class.java -> TagList(TAG_INT_ARRAY, toListOfIntArray(value), false)
				LongArray::class.java -> TagList(TAG_LONG_ARRAY, toListOfLongArray(value), false)
				TagByte::class.java -> TagList(TAG_BYTE, value as List<TagByte>, false)
				TagShort::class.java -> TagList(TAG_SHORT, value as List<TagShort>, false)
				TagInt::class.java -> TagList(TAG_INT, value as List<TagInt>, false)
				TagLong::class.java -> TagList(TAG_LONG, value as List<TagLong>, false)
				TagFloat::class.java -> TagList(TAG_FLOAT, value as List<TagFloat>, false)
				TagDouble::class.java -> TagList(TAG_DOUBLE, value as List<TagDouble>, false)
				TagByteArray::class.java -> TagList(TAG_BYTE_ARRAY, value as List<TagByteArray>, false)
				TagString::class.java -> TagList(TAG_STRING, value as List<TagString>, false)
				TagList::class.java -> TagList(TAG_LIST, value as List<TagList>, false)
				TagCompound::class.java -> TagList(TAG_COMPOUND, value as List<TagCompound>, false)
				TagIntArray::class.java -> TagList(TAG_INT_ARRAY, value as List<TagIntArray>, false)
				TagLongArray::class.java -> TagList(TAG_LONG_ARRAY, value as List<TagLongArray>, false)
				else -> throw IllegalArgumentException("Unsupported type ${T::class.java.simpleName} for TagList")
			})
		}
		
		fun set(name: String, value: TagList) {
			entries[name] = value
		}
	}
}
