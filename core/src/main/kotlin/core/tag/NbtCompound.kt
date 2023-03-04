@file:Suppress("unused")

package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import kotlinx.collections.immutable.*
import java.nio.*

typealias NbtCompoundMap = Map<String, NbtAny>
typealias NbtCompoundMapEntry = Map.Entry<String, NbtAny>
typealias MutableNbtCompoundMap = LinkedHashMap<String, NbtAny>

open class NbtCompound protected constructor(name: String? = null) : NbtBase<NbtCompoundMap>(COMPOUND, name) {
	
	override val sizeInBytes
		get() = _value.entries.sumOf { (name, tag) ->
			Byte.SIZE_BYTES + (Short.SIZE_BYTES + name.toByteArray().size) + tag.sizeInBytes
		} + Byte.SIZE_BYTES
	
	constructor(value: NbtCompoundMap, name: String? = null, check: Boolean = true) : this(name) {
		require(!check || value.values.all { tag -> tag !is NbtEnd }) { "NbtCompound cannot contain NbtEnd" }
		
		_value = value.map { (name, tag) -> name to tag.ensureName(name) }.toMap().toImmutableMap()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	final override fun read(byteBuffer: ByteBuffer) {
		val value = MutableNbtCompoundMap()
		
		var nextId: Byte
		do {
			nextId = byteBuffer.byte
			
			if (nextId == END.id) break
			
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
		
		byteBuffer.put(END.id)
	}
	
	internal fun writeRoot(byteBuffer: ByteBuffer) {
		byteBuffer.put(COMPOUND.id)
		byteBuffer.putString(name ?: "")
		
		write(byteBuffer)
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtCompound(this)
	
	override fun clone(name: String?): NbtBase<NbtCompoundMap> = clone(name, true)
	
	@Suppress("KotlinConstantConditions")
	override fun clone(name: String?, deep: Boolean): NbtBase<NbtCompoundMap> =
		NbtCompound(_value.entries.associate { (name, tag) -> name to if (deep) tag.clone(name, deep) else tag }, name)
	
	override fun toString() = buildString {
		append("${prefix()}{")
		
		if (_value.isNotEmpty()) {
			appendLine().tab()
			appendLine(
				_value.entries.sortedWith(nbtComparator).joinToString(",\n$tab") { (_, nextTag) ->
					when (nextTag.type) {
						COMPOUND, LIST -> nextTag.toString().replace("\n", "\n$tab")
						else           -> nextTag.toString()
					}
				}
			)
		}
		
		append("}")
	}
	
	companion object {
		/** Custom [Comparator] for NBT entries in an [NbtCompound], inspired by NBTExplorer. */
		private val nbtComparator = Comparator<NbtCompoundMapEntry> { (name1, tag1), (name2, tag2) ->
			val type1 = tag1.type
			val type2 = tag2.type
			
			fun compareNames() = name1.lowercase().compareTo(name2.lowercase())
			
			when {
				type1 == COMPOUND -> if (type2 == COMPOUND) compareNames() else -1
				type2 == COMPOUND -> 1
				type1 == LIST     -> if (type2 == LIST) compareNames() else -1
				type2 == LIST         -> 1
				else                  -> compareNames()
			}
		}
	}
}