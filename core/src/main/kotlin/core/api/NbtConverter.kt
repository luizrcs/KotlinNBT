package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*

abstract class NbtConverter<T : Any> protected constructor(val name: String) {
	protected abstract val convertTagEnd: Tag<Nothing>.() -> T?
	protected abstract val convertTagByte: Tag<Byte>.() -> T?
	protected abstract val convertTagShort: Tag<Short>.() -> T?
	protected abstract val convertTagInt: Tag<Int>.() -> T?
	protected abstract val convertTagLong: Tag<Long>.() -> T?
	protected abstract val convertTagFloat: Tag<Float>.() -> T?
	protected abstract val convertTagDouble: Tag<Double>.() -> T?
	protected abstract val convertTagByteArray: Tag<ByteArray>.() -> T?
	protected abstract val convertTagString: Tag<String>.() -> T?
	protected abstract val convertTagList: Tag<TagListList>.() -> T?
	protected abstract val convertTagCompound: Tag<TagCompoundMap>.() -> T?
	protected abstract val convertTagIntArray: Tag<IntArray>.() -> T?
	protected abstract val convertTagLongArray: Tag<LongArray>.() -> T?
	
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
	
	fun register() {
		TagEnd.addConverter(name, convertTagEnd)
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