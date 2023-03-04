package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*

abstract class NbtConverter<T : Any> protected constructor() {
	
	abstract val convertNbtEnd: NbtBase<Nothing>.() -> T?
	abstract val convertNbtByte: NbtBase<Byte>.() -> T?
	abstract val convertNbtShort: NbtBase<Short>.() -> T?
	abstract val convertNbtInt: NbtBase<Int>.() -> T?
	abstract val convertNbtLong: NbtBase<Long>.() -> T?
	abstract val convertNbtFloat: NbtBase<Float>.() -> T?
	abstract val convertNbtDouble: NbtBase<Double>.() -> T?
	abstract val convertNbtByteArray: NbtBase<ByteArray>.() -> T?
	abstract val convertNbtString: NbtBase<String>.() -> T?
	abstract val convertNbtList: NbtBase<NbtListList>.() -> T?
	abstract val convertNbtCompound: NbtBase<NbtCompoundMap>.() -> T?
	abstract val convertNbtIntArray: NbtBase<IntArray>.() -> T?
	abstract val convertNbtLongArray: NbtBase<LongArray>.() -> T?
	
	/**
	 * Converts the given tag to this format, or null if the tag cannot be converted.
	 *
	 * @param tag the tag to convert.
	 *
	 * @return the converted value, or null if the tag cannot be converted to this format.
	 */
	abstract fun convertFromNbt(tag: NbtAny): T?
	
	/**
	 * Parses the given value to an [NbtAny], or null if the value is not a valid tag.
	 *
	 * @param value the value to parse.
	 *
	 * @return the parsed tag, or null if the value is not a valid tag.
	 */
	abstract fun convertToNbt(value: T): NbtAny?
}