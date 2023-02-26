package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.TagType.*
import kotlinx.collections.immutable.*
import java.nio.*

typealias TagListList = List<TagAny>
typealias MutableTagListList = MutableList<TagAny>

class TagList private constructor(name: String? = null) : Tag<TagListList>(TAG_LIST, name) {
	
	override val sizeInBytes get() = Byte.SIZE_BYTES + Int.SIZE_BYTES + _value.sumOf { it.sizeInBytes }
	
	private lateinit var _elementsType: TagType
	val elementsType get() = _elementsType
	
	operator fun get(index: Int) = _value[index]
	
	constructor(elementsType: TagType, value: TagListList, check: Boolean = true, name: String? = null) : this(name) {
		require(!check || check(elementsType, value)) { "TagList elements must be of the same type" }
		
		_elementsType = elementsType
		_value = value.map { tag -> tag.ensureName(null) }.toImmutableList()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	private fun check(elementsType: TagType, list: TagListList) = list.all { it.type == elementsType }
	
	override fun read(byteBuffer: ByteBuffer) {
		val elementsId = byteBuffer.byte
		val size = byteBuffer.int
		
		_elementsType = TagType[elementsId]
		_value = List(size) { read(_elementsType, byteBuffer) }.toImmutableList()
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.put(_elementsType.id)
		byteBuffer.putInt(_value.size)
		
		_value.forEach { it.write(byteBuffer) }
	}
	
	override fun clone(name: String?) = TagList(elementsType, value.map { it.clone(null) }, false, name)
	
	override fun prefix() = "${if (name.isNullOrEmpty()) "" else "${if (name.matches(nameRegex)) name else "`$name`"}: "}$type<$_elementsType> = "
	
	override fun toString() = buildString {
		append("${prefix()}[")
		
		if (_value.isNotEmpty()) {
			when (_elementsType) {
				TAG_COMPOUND, TAG_LIST -> {
					appendLine().tab()
					appendLine(_value.joinToString(",\n$tab") { it.toString().replace("\n", "\n$tab") })
				}
				else                   -> append(_value.joinToString(", ") { it.valueToString() })
			}
		}
		
		append("]")
	}
}
