package me.mrpingu.nbt.implementation

import me.mrpingu.nbt.*
import me.mrpingu.nbt.TagType.*
import me.mrpingu.nbt.extension.*
import java.nio.*

typealias CompoundMap = Map<String, TagAny>

open class TagCompound protected constructor(name: String?): Tag<CompoundMap>(TAG_COMPOUND, name) {
	
	override val sizeInBytes get() = _value.entries.sumBy { (name, tag) -> Byte.SIZE_BYTES + (Short.SIZE_BYTES + name.toByteArray().size) + tag.sizeInBytes } + Byte.SIZE_BYTES
	
	constructor(value: CompoundMap, name: String?): this(name) {
		_value = value.map { (name, tag) -> name to tag.ensureName(name) }.toMap()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
	inline fun <reified T: TagAny?> getAs(name: String) =
		value[name]?.let { it as? T ?: throw IllegalArgumentException("Tag $name is not a ${T::class.java.simpleName}") }
	
	internal fun writeRoot(byteBuffer: ByteBuffer) {
		byteBuffer.put(TAG_COMPOUND.id.toByte())
		byteBuffer.putString(name ?: "")
		
		write(byteBuffer)
	}
	
	operator fun get(name: String) = _value[name]
	
	final override fun read(byteBuffer: ByteBuffer) {
		val value = mutableMapOf<String, TagAny>()
		
		var nextId: Int
		
		do {
			nextId = byteBuffer.byte.toInt()
			
			if (nextId == TAG_END.id) break
			
			val nextName = byteBuffer.string
			val nextTag = read(nextId, byteBuffer, nextName)
			
			value[nextName] = nextTag
		} while (true)
		
		_value = value
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		_value.entries.forEach { (name, tag) ->
			byteBuffer.put(tag.type.id.toByte())
			byteBuffer.putString(name)
			
			tag.write(byteBuffer)
		}
		
		byteBuffer.put(TAG_END.id.toByte())
	}
	
	override fun clone(name: String?) = TagCompound(value, name)
	
	override fun toString() = buildString {
		append("${prefix()}{")
		
		if (value.isNotEmpty()) {
			newLine().tab()
			append(
				value.entries.sortedWith(NBTComparator).joinToString(",\n\t") { (_, nextTag) ->
					when (nextTag.type) {
						TAG_COMPOUND, TAG_LIST -> nextTag.toString().replace("\n", "\n\t")
						else                   -> nextTag.toString()
					}
				}
			)
			newLine()
		}
		
		append("}")
	}
}

/**
 * Custom [Comparator] for NBT entries in a [TagCompound], heavily inspired by NBTExplorer.
 */
object NBTComparator: Comparator<Map.Entry<String, TagAny>> {
	
	override fun compare(entry1: Map.Entry<String, TagAny>, entry2: Map.Entry<String, TagAny>): Int {
		val type1 = entry1.value.type
		val type2 = entry2.value.type
		
		return if (type1 != type2 && (type1 == TAG_COMPOUND || type1 == TAG_LIST || type2 == TAG_COMPOUND || type2 == TAG_LIST)) -type1.compareTo(type2)
		else entry1.key.toLowerCase().compareTo(entry2.key.toLowerCase())
	}
}
