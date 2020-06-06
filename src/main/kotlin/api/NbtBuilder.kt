package io.github.mrpng.nbt.api

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.implementation.*

@DslMarker
annotation class NbtAPI

@NbtAPI
inline fun nbt(
	name: String? = null,
	entries: CompoundMap = emptyMap(),
	lenient: Boolean = false,
	builder: TagCompoundBuilder.() -> Unit
) = TagCompoundBuilder(name, entries).apply(builder).build()

@NbtAPI
open class TagCompoundBuilder(private val name: String?, entries: CompoundMap) {
	
	@PublishedApi internal val entries = mutableMapOf<String, TagAny>().apply { putAll(entries) }
	
	@NbtAPI val byte = TagCompoundEntry<Byte> { value, name -> TagByte(value, name) }
	@NbtAPI val short = TagCompoundEntry<Short> { value, name -> TagShort(value, name) }
	@NbtAPI val int = TagCompoundEntry<Int> { value, name -> TagInt(value, name) }
	@NbtAPI val long = TagCompoundEntry<Long> { value, name -> TagLong(value, name) }
	@NbtAPI val float = TagCompoundEntry<Float> { value, name -> TagFloat(value, name) }
	@NbtAPI val double = TagCompoundEntry<Double> { value, name -> TagDouble(value, name) }
	@NbtAPI val byteArray = TagCompoundEntry<ByteArray> { value, name -> TagByteArray(value, name) }
	@NbtAPI val string = TagCompoundEntry<String> { value, name -> TagString(value, name) }
	@NbtAPI val list = TagCompoundListEntry()
	@NbtAPI val intArray = TagCompoundEntry<IntArray> { value, name -> TagIntArray(value, name) }
	@NbtAPI val longArray = TagCompoundEntry<LongArray> { value, name -> TagLongArray(value, name) }
	
	@NbtAPI
	inline fun compound(name: String, entries: CompoundMap = emptyMap(), builder: TagCompoundBuilder.() -> Unit) {
		this.entries[name] = TagCompoundBuilder(name, entries).apply(builder).build()
	}
	
	@NbtAPI
	inline fun compound(entries: CompoundMap = emptyMap(), builder: TagCompoundBuilder.() -> Unit) =
		TagCompoundBuilder(null, entries).apply(builder).build()
	
	open fun build() = TagCompound(entries, name)
	
	inner class TagCompoundEntry<T: Any>(private val initializer: (T, String) -> TagAny) {
		
		operator fun set(name: String, value: T) {
			entries[name] = initializer(value, name)
		}
	}
	
	inner class TagCompoundListEntry {
		
		fun toListOfByte(list: List<*>) = list.map { TagByte(it as Byte, null) }
		fun toListOfShort(list: List<*>) = list.map { TagShort(it as Short, null) }
		fun toListOfInt(list: List<*>) = list.map { TagInt(it as Int, null) }
		fun toListOfLong(list: List<*>) = list.map { TagLong(it as Long, null) }
		fun toListOfFloat(list: List<*>) = list.map { TagFloat(it as Float, null) }
		fun toListOfDouble(list: List<*>) = list.map { TagDouble(it as Double, null) }
		fun toListOfByteArray(list: List<*>) = list.map { TagByteArray(it as ByteArray, null) }
		fun toListOfString(list: List<*>) = list.map { TagString(it as String, null) }
		fun toListOfIntArray(list: List<*>) = list.map { TagIntArray(it as IntArray, null) }
		fun toListOfLongArray(list: List<*>) = list.map { TagLongArray(it as LongArray, null) }
		
		inline operator fun <reified T> set(name: String, value: List<T>) {
			set(name, when (T::class.java) {
				java.lang.Byte::class.java -> TagList(TAG_BYTE, toListOfByte(value), false, null)
				java.lang.Short::class.java -> TagList(TAG_SHORT, toListOfShort(value), false, null)
				java.lang.Integer::class.java -> TagList(TAG_INT, toListOfInt(value), false, null)
				java.lang.Long::class.java -> TagList(TAG_LONG, toListOfLong(value), false, null)
				java.lang.Float::class.java -> TagList(TAG_FLOAT, toListOfFloat(value), false, null)
				java.lang.Double::class.java -> TagList(TAG_DOUBLE, toListOfDouble(value), false, null)
				ByteArray::class.java -> TagList(TAG_BYTE_ARRAY, toListOfByteArray(value), false, null)
				String::class.java -> TagList(TAG_STRING, toListOfString(value), false, null)
				IntArray::class.java -> TagList(TAG_INT_ARRAY, toListOfIntArray(value), false, null)
				LongArray::class.java -> TagList(TAG_LONG_ARRAY, toListOfLongArray(value), false, null)
				TagByte::class.java -> TagList(TAG_BYTE, value as List<TagByte>, false, null)
				TagShort::class.java -> TagList(TAG_SHORT, value as List<TagShort>, false, null)
				TagInt::class.java -> TagList(TAG_INT, value as List<TagInt>, false, null)
				TagLong::class.java -> TagList(TAG_LONG, value as List<TagLong>, false, null)
				TagFloat::class.java -> TagList(TAG_FLOAT, value as List<TagFloat>, false, null)
				TagDouble::class.java -> TagList(TAG_DOUBLE, value as List<TagDouble>, false, null)
				TagByteArray::class.java -> TagList(TAG_BYTE_ARRAY, value as List<TagByteArray>, false, null)
				TagString::class.java -> TagList(TAG_STRING, value as List<TagString>, false, null)
				TagList::class.java -> TagList(TAG_LIST, value as List<TagList>, false, null)
				TagCompound::class.java -> TagList(TAG_COMPOUND, value as List<TagCompound>, false, null)
				TagIntArray::class.java -> TagList(TAG_INT_ARRAY, value as List<TagIntArray>, false, null)
				TagLongArray::class.java -> TagList(TAG_LONG_ARRAY, value as List<TagLongArray>, false, null)
				else -> throw IllegalArgumentException("Unsupported type ${T::class.java.simpleName} for TagList")
			})
		}
		
		fun set(name: String, value: TagList) {
			entries[name] = value
		}
	}
}
