package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.tag.TagType.*
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

val TagAny?.tagEnd get() = this!!.getAs<TagEnd>()
val TagAny?.tagByte get() = this!!.getAs<TagByte>()
val TagAny?.tagShort get() = this!!.getAs<TagShort>()
val TagAny?.tagInt get() = this!!.getAs<TagInt>()
val TagAny?.tagLong get() = this!!.getAs<TagLong>()
val TagAny?.tagFloat get() = this!!.getAs<TagFloat>()
val TagAny?.tagDouble get() = this!!.getAs<TagDouble>()
val TagAny?.tagByteArray get() = this!!.getAs<TagByteArray>()
val TagAny?.tagString get() = this!!.getAs<TagString>()
val TagAny?.tagList get() = this!!.getAs<TagList>()
val TagAny?.tagCompound get() = this!!.getAs<TagCompound>()
val TagAny?.tagIntArray get() = this!!.getAs<TagIntArray>()
val TagAny?.tagLongArray get() = this!!.getAs<TagLongArray>()

val TagAny?.byte get() = tagByte.value
val TagAny?.short get() = tagShort.value
val TagAny?.int get() = tagInt.value
val TagAny?.long get() = tagLong.value
val TagAny?.float get() = tagFloat.value
val TagAny?.double get() = tagDouble.value
val TagAny?.byteArray get() = tagByteArray.value
val TagAny?.string get() = tagString.value
val TagAny?.list get() = tagList.value
val TagAny?.compound get() = tagCompound.value
val TagAny?.intArray get() = tagIntArray.value
val TagAny?.longArray get() = tagLongArray.value

/**
 * Creates a [Tag] with the specified type [T] and, if appropriate in regards to the NBT specs,
 * a name (only really used inside a [TagCompound]). Should never be called directly, only by subclasses.
 *
 * @param T ensures the typed return value for this tag.
 * @param type the type of this tag.
 * @param name this tag's name, if inside a [TagCompound].
 */
sealed class Tag<T : Any>(val type: TagType, val name: String?) {
	
	/**
	 * Backing mutable property for this tag's [value]. Should never be used externally.
	 */
	internal lateinit var _value: T
	
	/**
	 * Accessible tag value, already correctly typed and safe from mutability.
	 */
	open val value: T get() = _value
	
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
	inline fun <reified T : TagAny?> getAs() =
		this as? T ?: throw IllegalStateException("Tag is not a ${T::class.java.simpleName}")
	
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
	 * Deep-clones this tag. Some properties can be changed during the copy.
	 *
	 * @param name the name to apply to the cloned tag.
	 * @return a clone of this tag; some properties may have been changed with the params.
	 */
	internal abstract fun clone(name: String? = this.name): Tag<T>
	
	/**
	 * Clones this tag. Some properties can be changed during the copy.
	 *
	 * @param name the name to apply to the cloned tag.
	 * @param deep perform deep clone.
	 * @return a clone of this tag; some properties may have been changed with the params.
	 */
	internal open fun clone(name: String? = this.name, deep: Boolean): Tag<T> = clone(name)
	
	/**
	 * Forces the specified name on this tag using a clone if needed.
	 * Used when building a [TagCompound] or a [TagList].
	 *
	 * @param name the name to be ensured in this tag.
	 * @return this tag, if the name is correct, or a clone with the new name.
	 */
	internal fun ensureName(name: String?) = if (this.name == name) this else clone(name)
	
	internal val nameRegex = """[a-zA-Z][a-zA-Z0-9_]*""".toRegex()
	
	/**
	 * Prefix of this tag's value when calling [toString].
	 * Should always contain the tag type.
	 *
	 * @return the prefix for use in [toString]
	 */
	open fun prefix() = "${if (name.isNullOrEmpty()) "" else "${if (name.matches(nameRegex)) name else "`$name`"}: "}$type = "
	
	/**
	 * Only the portion of [toString] containing this tag's value, formatted according to its type.
	 *
	 * @return this tag's value, after formatting.
	 */
	open fun valueToString() = "$_value"
	
	operator fun component1() = type
	operator fun component2() = name
	
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
			TAG_END        -> TagEnd
			TAG_BYTE       -> TagByte(byteBuffer, name)
			TAG_SHORT      -> TagShort(byteBuffer, name)
			TAG_INT        -> TagInt(byteBuffer, name)
			TAG_LONG       -> TagLong(byteBuffer, name)
			TAG_FLOAT      -> TagFloat(byteBuffer, name)
			TAG_DOUBLE     -> TagDouble(byteBuffer, name)
			TAG_BYTE_ARRAY -> TagByteArray(byteBuffer, name)
			TAG_STRING     -> TagString(byteBuffer, name)
			TAG_LIST       -> TagList(byteBuffer, name)
			TAG_COMPOUND   -> TagCompound(byteBuffer, name)
			TAG_INT_ARRAY  -> TagIntArray(byteBuffer, name)
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
		fun read(id: Byte, byteBuffer: ByteBuffer, name: String? = null) = read(TagType[id], byteBuffer, name)
	}
}

enum class TagType(val id: Byte, private val string: String) {
	
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
	
	operator fun component1() = id
	operator fun component2() = string
	
	override fun toString() = string
	
	companion object : LinkedHashMap<Byte, TagType>() {
		
		init {
			putAll(values().associateBy { (id) -> id })
		}
		
		override fun get(key: Byte) = super.get(key) ?: throw IllegalArgumentException("Invalid tag id $key")
	}
}
