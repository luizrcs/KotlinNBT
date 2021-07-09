package br.com.luizrcs.nbt.core.tag

abstract class TagArray<T : Any, U : Number> internal constructor(type: TagType, name: String? = null) :
	Tag<T>(type, name) {
	
	abstract val size: Int
	
	abstract operator fun get(index: Int): U
}
