package br.com.luizrcs.nbt.core.tag

typealias NbtArrayAny = NbtArray<Any>

abstract class NbtArray<T : Any> protected constructor(type: NbtType, name: String? = null) : NbtBase<T>(type, name) {
	
	/** Returns the number of elements in the array. */
	abstract val size: Int
}