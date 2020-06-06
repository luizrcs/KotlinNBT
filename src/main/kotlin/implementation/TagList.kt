package io.github.mrpng.nbt.implementation

import io.github.mrpng.nbt.*
import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.extension.*
import java.nio.*

class TagList private constructor(name: String? = null): Tag<List<TagAny>>(TAG_LIST, name) {
	
	override val sizeInBytes get() = Byte.SIZE_BYTES + Int.SIZE_BYTES + _value.sumBy { it.sizeInBytes }
	
	private lateinit var _elementsType: TagType
	val elementsType get() = _elementsType
	
	operator fun get(index: Int) = _value[index]
	
	constructor(elementsType: TagType, value: List<TagAny>, check: Boolean = true, name: String? = null): this(name) {
		require(!check || check(elementsType, value)) { "TagList elements must be of a single type" }
		
		_elementsType = elementsType
		_value = value.map { tag -> tag.ensureName(null) }.toList()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null): this(name) {
		read(byteBuffer)
	}
	
	private fun check(elementsType: TagType, list: List<TagAny>) = list.all { it.type == elementsType }
	
	override fun read(byteBuffer: ByteBuffer) {
		val elementsId = byteBuffer.byte.toInt()
		val size = byteBuffer.int
		
		_elementsType = TagType[elementsId]
		_value = List(size) { read(_elementsType, byteBuffer) }
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.put(_elementsType.id.toByte())
		byteBuffer.putInt(_value.size)
		
		_value.forEach { it.write(byteBuffer) }
	}
	
	override fun clone(name: String?) = TagList(elementsType, value.map(TagAny::clone), false, name)
	
	override fun prefix() = "${if (name.isNullOrEmpty()) "" else "$name: "}$type<$_elementsType> = "
	
	override fun toString() = buildString {
		append("${prefix()}[")
		
		if (_value.isNotEmpty()) {
			when (_elementsType) {
				TAG_COMPOUND, TAG_LIST -> {
					appendln().tab()
					appendln(_value.joinToString(",\n\t") { it.toString().replace("\n", "\n\t") })
				}
				else -> append(_value.joinToString(", ") { it.valueToString() })
			}
		}
		
		append("]")
	}
}
