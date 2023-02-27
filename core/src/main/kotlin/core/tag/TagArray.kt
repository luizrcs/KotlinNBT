package br.com.luizrcs.nbt.core.tag

abstract class TagArray<T : Any, U : Number>
internal constructor(name: String? = null, type: TagType, converters: Map<String, Tag<T>.() -> Any>) :
	Tag<T>(name, type, converters) {
	
	abstract val size: Int
	
	abstract operator fun get(index: Int): U
}
