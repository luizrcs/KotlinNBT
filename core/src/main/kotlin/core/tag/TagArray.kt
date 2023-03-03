package br.com.luizrcs.nbt.core.tag

abstract class TagArray<T : Any, U : Number>
protected constructor(name: String? = null, type: TagType, converters: Map<String, Tag<T>.() -> Any?>) :
	Tag<T>(name, type, converters) {
	
	/** Returns the number of elements in the array. */
	abstract val size: Int
	
	/**
	 * Returns the element at the given [index].
	 *
	 * If the index is out of bounds of this array, throws an [IndexOutOfBoundsException].
	 *
	 * @param index the index of the element to return.
	 *
	 * @return the element at the given [index].
	 *
	 * @throws IndexOutOfBoundsException if [index] is out of bounds of this array.
	 */
	abstract operator fun get(index: Int): U
}