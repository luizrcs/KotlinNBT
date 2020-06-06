package io.github.mrpng.nbt

import io.github.mrpng.nbt.TagType.*
import io.github.mrpng.nbt.implementation.*
import java.nio.*

typealias TagAny = Tag<out Any>

val TagAny?.isTagEnd get() = this != null && type == TAG_END
val TagAny?.isTagByte get() = this != null && type == TAG_BYTE
val TagAny?.isTagShort get() = this != null && type == TAG_SHORT
val TagAny?.isTagInt get() = this != null && type == TAG_INT
val TagAny?.isTagLong get() = this != null && type == TAG_LONG
val TagAny?.isTagFloat get() = this != null && type == TAG_FLOAT
val TagAny?.isTagDouble get() = this != null && type == TAG_DOUBLE
val TagAny?.isTagByteArray get() = this != null && type == TAG_BYTE_ARRAY
val TagAny?.isTagString get() = this != null && type == TAG_STRING
val TagAny?.isTagList get() = this != null && type == TAG_LIST
val TagAny?.isTagCompound get() = this != null && type == TAG_COMPOUND
val TagAny?.isTagIntArray get() = this != null && type == TAG_INT_ARRAY
val TagAny?.isTagLongArray get() = this != null && type == TAG_LONG_ARRAY

val TagAny?.asTagEnd get() = this!!.getAs<TagEnd>()
val TagAny?.asTagByte get() = this!!.getAs<TagByte>()
val TagAny?.asTagShort get() = this!!.getAs<TagShort>()
val TagAny?.asTagInt get() = this!!.getAs<TagInt>()
val TagAny?.asTagLong get() = this!!.getAs<TagLong>()
val TagAny?.asTagFloat get() = this!!.getAs<TagFloat>()
val TagAny?.asTagDouble get() = this!!.getAs<TagDouble>()
val TagAny?.asTagByteArray get() = this!!.getAs<TagByteArray>()
val TagAny?.asTagString get() = this!!.getAs<TagString>()
val TagAny?.asTagList get() = this!!.getAs<TagList>()
val TagAny?.asTagCompound get() = this!!.getAs<TagCompound>()
val TagAny?.asTagIntArray get() = this!!.getAs<TagIntArray>()
val TagAny?.asTagLongArray get() = this!!.getAs<TagLongArray>()

val TagAny?.asByte get() = asTagByte._value
val TagAny?.asShort get() = asTagShort._value
val TagAny?.asInt get() = asTagInt._value
val TagAny?.asLong get() = asTagLong._value
val TagAny?.asFloat get() = asTagFloat._value
val TagAny?.asDouble get() = asTagDouble._value
val TagAny?.asByteArray get() = asTagByteArray._value
val TagAny?.asString get() = asTagString._value
val TagAny?.asList get() = asTagList._value
val TagAny?.asCompound get() = asTagCompound._value
val TagAny?.asIntArray get() = asTagIntArray._value
val TagAny?.asLongArray get() = asTagLongArray._value

/**
 * Creates a [Tag] with the specified type [T] and, if appropriate in regards to the NBT specs,
 * a name (only really used inside a [TagCompound]). Should never be called directly, only by subclasses.
 *
 * @param T ensures the typed return value for this tag.
 * @param type the type of this tag.
 * @param name this tag's name, if inside a [TagCompound].
 */
abstract class Tag<T: Any> protected constructor(val type: TagType, val name: String?) {
	
	/**
	 * Backing mutable property for this tag's [value].
	 */
	@PublishedApi internal lateinit var _value: T
	
	/**
	 * Accessible tag value, already correctly typed and "immutable".
	 */
	val value: T get() = _value
	
	/**
	 * Used to calculate this tag (and its children, if applicable) size in bytes, not considering
	 * its type ID nor its name.
	 *
	 * @return this tag's size, not considering its type ID nor its name; calculates its children when necessary.
	 */
	internal abstract val sizeInBytes: Int
	
	/**
	 * Safely casts this tag to specified type, throwing an exception if supplied type
	 * is incorrect.
	 *
	 * @param T type to cast this tag to; should be checked with one the "is[T]" properties
	 * before calling this function.
	 * @throws IllegalStateException thrown if trying to cast to an incorrect type.
	 */
	inline fun <reified T: TagAny?> getAs() = this as? T ?: throw IllegalStateException("Tag is not a ${T::class.java.simpleName}")
	
	/**
	 * Reads this tag from a [ByteBuffer] to [_value] with appropriate type.
	 *
	 * @param byteBuffer the [ByteBuffer] to read from.
	 */
	internal abstract fun read(byteBuffer: ByteBuffer)
	
	/**
	 * Writes this tag to a [ByteBuffer] from [_value] with appropriate type.
	 *
	 * @param byteBuffer the [ByteBuffer] to write to.
	 */
	internal abstract fun write(byteBuffer: ByteBuffer)
	
	/**
	 * Clones this tag. Some properties can be changed during the copy.
	 *
	 * @param name the name to apply to the cloned tag.
	 * @return a clone of this tag; some properties may have been changed with the params.
	 */
	internal abstract fun clone(name: String? = this.name): Tag<T>
	
	/**
	 * Forces the specified name on this tag using a clone if needed.
	 * Used when building a [TagCompound] or a [TagList].
	 *
	 * @param name the name to be ensured in this tag.
	 * @return this tag, if the name is correct, or a clone with the new name.
	 */
	internal fun ensureName(name: String?) = if (this.name == name) this else clone(name)
	
	/**
	 * Prefix of this tag's value when calling [toString].
	 * Should always contain the tag type.
	 *
	 * @return the prefix for use in [toString]
	 */
	open fun prefix() = "${if (name.isNullOrEmpty()) "" else "$name: "}$type = "
	
	/**
	 * Only the portion of [toString] containing this tag's value, formatted according to its type.
	 *
	 * @return this tag's value, after formatting.
	 */
	open fun valueToString() = "$_value"
	
	/**
	 * Formatted version of this tag's contents ready for pretty-printing.
	 *
	 * @return stringified version of this tag's contents
	 */
	override fun toString() = prefix() + valueToString()
	
	companion object {
		
		/**
		 * Reads a tag given its type and a [ByteBuffer] to read from. A name can also be
		 * provided, though it's not obligatory and defaults to null, but should be used when reading
		 * [TagCompound] children for string formatting purposes.
		 *
		 * @param tagType type of the tag to be read.
		 * @param byteBuffer the [ByteBuffer] to read from.
		 * @param name the name of the tag to be read; defaults to null.
		 */
		fun read(tagType: TagType, byteBuffer: ByteBuffer, name: String? = null) = when (tagType) {
			TAG_END -> TagEnd
			TAG_BYTE -> TagByte(byteBuffer, name)
			TAG_SHORT -> TagShort(byteBuffer, name)
			TAG_INT -> TagInt(byteBuffer, name)
			TAG_LONG -> TagLong(byteBuffer, name)
			TAG_FLOAT -> TagFloat(byteBuffer, name)
			TAG_DOUBLE -> TagDouble(byteBuffer, name)
			TAG_BYTE_ARRAY -> TagByteArray(byteBuffer, name)
			TAG_STRING -> TagString(byteBuffer, name)
			TAG_LIST -> TagList(byteBuffer, name)
			TAG_COMPOUND -> TagCompound(byteBuffer, name)
			TAG_INT_ARRAY -> TagIntArray(byteBuffer, name)
			TAG_LONG_ARRAY -> TagLongArray(byteBuffer, name)
		}
		
		/**
		 * Reads a tag given its type ID and a [ByteBuffer] to read from. A name can also be
		 * provided, though it's not obligatory and defaults to null, but should be used when reading
		 * [TagCompound] children for string formatting purposes.
		 *
		 * @param id type id of the tag to be read.
		 * @param byteBuffer the [ByteBuffer] to read from.
		 * @param name the name of the tag to be read; defaults to null.
		 */
		fun read(id: Int, byteBuffer: ByteBuffer, name: String? = null) = read(TagType[id], byteBuffer, name)
	}
}

enum class TagType(val id: Int, private val string: String) {
	
	TAG_END(0, "End"),
	TAG_BYTE(1, "Byte"),
	TAG_SHORT(2, "Short"),
	TAG_INT(3, "Int"),
	TAG_LONG(4, "Long"),
	TAG_FLOAT(5, "Float"),
	TAG_DOUBLE(6, "Double"),
	TAG_BYTE_ARRAY(7, "ByteArray"),
	TAG_STRING(8, "String"),
	TAG_LIST(9, "List"),
	TAG_COMPOUND(10, "Compound"),
	TAG_INT_ARRAY(11, "IntArray"),
	TAG_LONG_ARRAY(12, "LongArray");
	
	override fun toString() = string
	
	companion object {
		
		private val tagByIndex = values().map { it.id to it }.toMap()
		
		operator fun get(id: Int) = tagByIndex[id] ?: throw IllegalArgumentException("Invalid tag id $id")
	}
}
