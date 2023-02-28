package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*

abstract class NbtConverter<T : Any>(val name: String) {
	abstract val convertTagByte: Tag<Byte>.() -> T?
	abstract val convertTagShort: Tag<Short>.() -> T?
	abstract val convertTagInt: Tag<Int>.() -> T?
	abstract val convertTagLong: Tag<Long>.() -> T?
	abstract val convertTagFloat: Tag<Float>.() -> T?
	abstract val convertTagDouble: Tag<Double>.() -> T?
	abstract val convertTagByteArray: Tag<ByteArray>.() -> T?
	abstract val convertTagString: Tag<String>.() -> T?
	abstract val convertTagList: Tag<TagListList>.() -> T?
	abstract val convertTagCompound: Tag<TagCompoundMap>.() -> T?
	abstract val convertTagIntArray: Tag<IntArray>.() -> T?
	abstract val convertTagLongArray: Tag<LongArray>.() -> T?
	
	/**
	 * Converts the given tag to this format, or null if the tag cannot be converted.
	 *
	 * @param tag the tag to convert.
	 *
	 * @return the converted value, or null if the tag cannot be converted to this format.
	 */
	abstract fun convertFromTag(tag: TagAny): T?
	
	/**
	 * Parses the given value to a [TagAny], or null if the value is not a valid tag.
	 *
	 * @param value the value to parse.
	 *
	 * @return the parsed tag, or null if the value is not a valid tag.
	 */
	abstract fun convertToTag(value: T): TagAny?
	
	fun registerConverter() {
		TagByte.addConverter(name, convertTagByte)
		TagShort.addConverter(name, convertTagShort)
		TagInt.addConverter(name, convertTagInt)
		TagLong.addConverter(name, convertTagLong)
		TagFloat.addConverter(name, convertTagFloat)
		TagDouble.addConverter(name, convertTagDouble)
		TagByteArray.addConverter(name, convertTagByteArray)
		TagString.addConverter(name, convertTagString)
		TagList.addConverter(name, convertTagList)
		TagCompound.addConverter(name, convertTagCompound)
		TagIntArray.addConverter(name, convertTagIntArray)
		TagLongArray.addConverter(name, convertTagLongArray)
	}
}