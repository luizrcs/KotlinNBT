@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
	"NAME_SHADOWING",
)

package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.extension.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import kotlinx.collections.immutable.*
import java.nio.*

typealias NbtListList = List<NbtAny>
typealias MutableNbtListList = ArrayList<NbtAny>

class NbtList private constructor(name: String? = null) : NbtBase<NbtListList>(LIST, name) {
	
	override val sizeInBytes get() = Byte.SIZE_BYTES + Int.SIZE_BYTES + _value.sumOf { it.sizeInBytes }
	
	private lateinit var _elementsType: NbtType
	val elementsType get() = _elementsType
	
	constructor(elementsType: NbtType, value: NbtListList, check: Boolean = true, name: String? = null) : this(name) {
		var value = value
		
		if (elementsType == END) value = emptyList()
		else require(!check || check(elementsType, value)) { "NbtList elements must be of the same type" }
		
		_elementsType = elementsType
		_value = value.map { tag -> tag.ensureName(null) }.toImmutableList()
	}
	
	constructor(byteBuffer: ByteBuffer, name: String? = null) : this(name) {
		read(byteBuffer)
	}
	
	override fun read(byteBuffer: ByteBuffer) {
		val elementsId = byteBuffer.byte
		val size = byteBuffer.int
		
		_elementsType = NbtType[elementsId]
		_value = List(size) { read(_elementsType, byteBuffer) }.toImmutableList()
	}
	
	override fun write(byteBuffer: ByteBuffer) {
		byteBuffer.put(_elementsType.id)
		byteBuffer.putInt(_value.size)
		
		_value.forEach { it.write(byteBuffer) }
	}
	
	override fun <U : Any> convert(converter: NbtConverter<U>): U? = converter.convertNbtList(this)
	
	override fun clone(name: String?) = NbtList(elementsType, value.map { it.clone(null) }, false, name)
	
	override fun prefix() = "${if (name.isNullOrEmpty()) "" else "${if (name.matches(nameRegex)) name else "`$name`"}: "}$type<$_elementsType> = "
	
	override fun toString() = buildString {
		append("${prefix()}[")
		
		if (_value.isNotEmpty()) {
			when (_elementsType) {
				COMPOUND, LIST -> {
					appendLine().tab()
					appendLine(_value.joinToString(",\n$tab") { it.toString().replace("\n", "\n$tab") })
				}
				else           -> append(_value.joinToString(", ") { it.valueToString() })
			}
		}
		
		append("]")
	}
	
	private fun check(elementsType: NbtType, list: NbtListList) = list.all { it.type == elementsType }
}