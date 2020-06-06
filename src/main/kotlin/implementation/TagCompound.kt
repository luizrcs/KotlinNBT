package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.extension.*
import java.nio.*

typealias CompoundMap = Map<String, TagAny>

open class TagCompound protected constructor(name: String?): Tag<CompoundMap>(TAG_COMPOUND, name) {
	
	override val sizeInBytes
		get() = _value.entries.sumBy { (name, tag) ->
			Byte.SIZE_BYTES + (Short.SIZE_BYTES + name.toByteArray().size) + tag.sizeInBytes
		} + Byte.SIZE_BYTES
	
	constructor(value: CompoundMap, name: String?): this(name) {
		_value = value.map { (name, tag) -> name to tag.ensureName(name) }.toMap()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String?): this(name) {
		read(byteBuffer)
	}
	
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
			appendln().tab()
			appendln(
				value.entries.sortedWith(NBTComparator).joinToString(",\n\t") { (_, nextTag) ->
					when (nextTag.type) {
						TAG_COMPOUND, TAG_LIST -> nextTag.toString().replace("\n", "\n\t")
						else -> nextTag.toString()
					}
				}
			)
		}
		
		append("}")
	}
	
	companion object {
		
		/** Custom [Comparator] for NBT entries in a [TagCompound], inspired by NBTExplorer. */
		val NBTComparator = Comparator<Map.Entry<String, TagAny>> { (name1, tag1), (name2, tag2) ->
			val type1 = tag1.type
			val type2 = tag2.type
			
			fun compareNames() = name1.toLowerCase().compareTo(name2.toLowerCase())
			
			when {
				type1 == TAG_COMPOUND -> if (type2 == TAG_COMPOUND) compareNames() else -1
				type2 == TAG_COMPOUND -> 1
				type1 == TAG_LIST -> if (type2 == TAG_LIST) compareNames() else -1
				type2 == TAG_LIST -> 1
				else -> compareNames()
			}
		}
	}
}
