package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import kotlinx.collections.immutable.*
import java.nio.*

typealias TagCompoundMap = Map<String, TagAny>
typealias TagCompoundMapEntry = Map.Entry<String, TagAny>
typealias MutableTagCompoundMap = LinkedHashMap<String, TagAny>

open class TagCompound protected constructor(name: String? = null) : Tag<TagCompoundMap>(TAG_COMPOUND, name) {
	
	override val sizeInBytes
		get() = _value.entries.sumOf { (name, tag) ->
			Byte.SIZE_BYTES + (Short.SIZE_BYTES + name.toByteArray().size) + tag.sizeInBytes
		} + Byte.SIZE_BYTES
	
	constructor(value: TagCompoundMap, name: String? = null) : this(name) {
		_value = value.map { (name, tag) -> name to tag.ensureName(name) }.toMap().toImmutableMap()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	operator fun get(name: String) = _value[name]
	
	final override fun read(byteBuffer: ByteBuffer) {
		val value = MutableTagCompoundMap()
		
		var nextId: Byte
		do {
			nextId = byteBuffer.byte
			
			if (nextId == TAG_END.id) break
			
			val nextName = byteBuffer.string
			val nextTag = read(nextId, byteBuffer, nextName)
			
			value[nextName] = nextTag
		} while (true)
		
		_value = value.toImmutableMap()
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		_value.entries.forEach { (name, tag) ->
			byteBuffer.put(tag.type.id)
			byteBuffer.putString(name)
			
			tag.write(byteBuffer)
		}
		
		byteBuffer.put(TAG_END.id)
	}
	
	internal fun writeRoot(byteBuffer: ByteBuffer) {
		byteBuffer.put(TAG_COMPOUND.id)
		byteBuffer.putString(name ?: "")
		
		write(byteBuffer)
	}
	
	override fun clone(name: String?): Tag<TagCompoundMap> = clone(name, true)
	
	override fun clone(name: String?, deep: Boolean): Tag<TagCompoundMap> =
		TagCompound(_value.entries.associate { (name, tag) -> name to if (deep) tag.clone(name, deep) else tag }, name)
	
	override fun toString() = buildString {
		append("${prefix()}{")
		
		if (_value.isNotEmpty()) {
			appendLine().tab()
			appendLine(
				_value.entries.sortedWith(nbtComparator).joinToString(",\n\t") { (_, nextTag) ->
					when (nextTag.type) {
						TAG_COMPOUND, TAG_LIST -> nextTag.toString().replace("\n", "\n\t")
						else                   -> nextTag.toString()
					}
				}
			)
		}
		
		append("}")
	}
	
	companion object {
		/** Custom [Comparator] for NBT entries in a [TagCompound], inspired by NBTExplorer. */
		val nbtComparator = Comparator<TagCompoundMapEntry> { (name1, tag1), (name2, tag2) ->
			val type1 = tag1.type
			val type2 = tag2.type
			
			fun compareNames() = name1.lowercase().compareTo(name2.lowercase())
			
			when {
				type1 == TAG_COMPOUND -> if (type2 == TAG_COMPOUND) compareNames() else -1
				type2 == TAG_COMPOUND -> 1
				type1 == TAG_LIST     -> if (type2 == TAG_LIST) compareNames() else -1
				type2 == TAG_LIST     -> 1
				else                  -> compareNames()
			}
		}
	}
}
