@file:Suppress("unused")

package br.com.luizrcs.nbt.core.tag

import br.com.luizrcs.nbt.core.api.*
import br.com.luizrcs.nbt.core.tag.NbtType.*
import java.nio.*

typealias NbtAny = NbtBase<out Any>

val NbtAny?.isNbtEnd get() = this != null && type == END
val NbtAny?.isNbtByte get() = this != null && type == BYTE
val NbtAny?.isNbtShort get() = this != null && type == SHORT
val NbtAny?.isNbtInt get() = this != null && type == INT
val NbtAny?.isNbtLong get() = this != null && type == LONG
val NbtAny?.isNbtFloat get() = this != null && type == FLOAT
val NbtAny?.isNbtDouble get() = this != null && type == DOUBLE
val NbtAny?.isNbtByteArray get() = this != null && type == BYTE_ARRAY
val NbtAny?.isNbtString get() = this != null && type == STRING
val NbtAny?.isNbtList get() = this != null && type == LIST
val NbtAny?.isNbtCompound get() = this != null && type == COMPOUND
val NbtAny?.isNbtIntArray get() = this != null && type == INT_ARRAY
val NbtAny?.isNbtLongArray get() = this != null && type == LONG_ARRAY

val NbtAny?.nbtEnd get() = this!!.getAs<NbtEnd>()
val NbtAny?.nbtByte get() = this!!.getAs<NbtByte>()
val NbtAny?.nbtShort get() = this!!.getAs<NbtShort>()
val NbtAny?.nbtInt get() = this!!.getAs<NbtInt>()
val NbtAny?.nbtLong get() = this!!.getAs<NbtLong>()
val NbtAny?.nbtFloat get() = this!!.getAs<NbtFloat>()
val NbtAny?.nbtDouble get() = this!!.getAs<NbtDouble>()
val NbtAny?.nbtByteArray get() = this!!.getAs<NbtByteArray>()
val NbtAny?.nbtString get() = this!!.getAs<NbtString>()
val NbtAny?.nbtList get() = this!!.getAs<NbtList>()
val NbtAny?.nbtCompound get() = this!!.getAs<NbtCompound>()
val NbtAny?.nbtIntArray get() = this!!.getAs<NbtIntArray>()
val NbtAny?.nbtLongArray get() = this!!.getAs<NbtLongArray>()

val NbtAny?.byte get() = nbtByte.value
val NbtAny?.short get() = nbtShort.value
val NbtAny?.int get() = nbtInt.value
val NbtAny?.long get() = nbtLong.value
val NbtAny?.float get() = nbtFloat.value
val NbtAny?.double get() = nbtDouble.value
val NbtAny?.byteArray get() = nbtByteArray.value
val NbtAny?.string get() = nbtString.value
val NbtAny?.list get() = nbtList.value
val NbtAny?.compound get() = nbtCompound.value
val NbtAny?.intArray get() = nbtIntArray.value
val NbtAny?.longArray get() = nbtLongArray.value

/**
 * Creates an [NbtBase] with the specified type [T] and, if appropriate regarding the NBT specs,
 * a name (only really used inside an [NbtCompound]). Should never be called directly, only by subclasses.
 *
 * @param T ensures the typed return value for this tag.
 * @param type the type of this tag.
 * @param name this tag's name, if inside an [NbtCompound].
 */
sealed class NbtBase<T : Any>(val type: NbtType, val name: String?) {
	
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
	 *
	 * @return this tag cast to the specified type [T].
	 *
	 * @throws IllegalStateException thrown if trying to cast to an incorrect type.
	 */
	inline fun <reified T : NbtAny?> getAs(): T =
		this as? T ?: throw IllegalStateException("Tag is not an ${T::class.java.simpleName}")
	
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
	 * Converts this tag to a specified type [U], or `null` if this tag cannot be converted to the specified type [U].
	 *
	 * @param U the type to convert this tag to.
	 * @param converter the converter function instance to use.
	 *
	 * @return the converted tag value, or `null` if this tag cannot be converted to the specified type [U].
	 */
	abstract fun <U : Any> convert(converter: NbtConverter<U>): U?
	
	/**
	 * Deep-clones this tag. Some properties can be changed during the copy.
	 *
	 * @param name the name to apply to the cloned tag.
	 *
	 * @return a clone of this tag; some properties may have been changed with the params.
	 */
	internal abstract fun clone(name: String? = this.name): NbtBase<T>
	
	/**
	 * Clones this tag. Some properties can be changed during the copy.
	 *
	 * @param name the name to apply to the cloned tag.
	 * @param deep perform deep clone.
	 *
	 * @return a clone of this tag; some properties may have been changed with the params.
	 */
	internal open fun clone(name: String? = this.name, deep: Boolean): NbtBase<T> = clone(name)
	
	/**
	 * Forces the specified name on this tag using a clone if needed.
	 * Used when building an [NbtCompound] or an [NbtList].
	 *
	 * @param name the name to be ensured in this tag.
	 *
	 * @return this tag, if the name is correct, or a clone with the new name.
	 */
	internal fun ensureName(name: String?): NbtBase<T> = if (this.name == name) this else clone(name)
	
	/**
	 * Prefix of this tag's value when calling [toString].
	 * Should always contain the tag type.
	 *
	 * @return the prefix for use in [toString]
	 */
	open fun prefix(): String = "${if (name.isNullOrEmpty()) "" else "${if (name.matches(nameRegex)) name else "`$name`"}: "}$type = "
	
	/**
	 * Only the portion of [toString] containing this tag's value, formatted according to its type.
	 *
	 * @return this tag's value, after formatting.
	 */
	open fun valueToString(): String = "$_value"
	
	/**
	 * Formatted version of this tag's contents ready for pretty-printing.
	 *
	 * @return stringified version of this tag's contents
	 */
	override fun toString(): String = prefix() + valueToString()
	
	companion object {
		/** Regex to check if a tag name is a valid Java identifier. */
		internal val nameRegex = """[a-zA-Z_$][a-zA-Z0-9_$]*""".toRegex()
		
		/**
		 * Reads a tag given its type and a [ByteBuffer] to read from. A name can also be
		 * provided, though it's not obligatory and defaults to null, but should be used when reading
		 * [NbtCompound] children for string formatting purposes.
		 *
		 * @param tagType type of the tag to be read.
		 * @param byteBuffer the [ByteBuffer] to read from.
		 * @param name the name of the tag to be read; defaults to null.
		 *
		 * @return the read tag.
		 */
		fun read(tagType: NbtType, byteBuffer: ByteBuffer, name: String? = null): NbtAny = when (tagType) {
			END       -> NbtEnd
			BYTE    -> NbtByte(byteBuffer, name)
			SHORT    -> NbtShort(byteBuffer, name)
			INT       -> NbtInt(byteBuffer, name)
			LONG       -> NbtLong(byteBuffer, name)
			FLOAT          -> NbtFloat(byteBuffer, name)
			DOUBLE     -> NbtDouble(byteBuffer, name)
			BYTE_ARRAY -> NbtByteArray(byteBuffer, name)
			STRING       -> NbtString(byteBuffer, name)
			LIST          -> NbtList(byteBuffer, name)
			COMPOUND       -> NbtCompound(byteBuffer, name)
			INT_ARRAY  -> NbtIntArray(byteBuffer, name)
			LONG_ARRAY -> NbtLongArray(byteBuffer, name)
		}
		
		/**
		 * Reads a tag given its type ID and a [ByteBuffer] to read from. A name can also be
		 * provided, though it's not obligatory and defaults to null, but should be used when reading
		 * [NbtCompound] children for string formatting purposes.
		 *
		 * @param id type id of the tag to be read.
		 * @param byteBuffer the [ByteBuffer] to read from.
		 * @param name the name of the tag to be read; defaults to null.
		 *
		 * @return the read tag.
		 */
		fun read(id: Byte, byteBuffer: ByteBuffer, name: String? = null): NbtAny = read(NbtType[id], byteBuffer, name)
	}
}

enum class NbtType(val id: Byte, private val string: String) {
	END(0, "End"),
	BYTE(1, "Byte"),
	SHORT(2, "Short"),
	INT(3, "Int"),
	LONG(4, "Long"),
	FLOAT(5, "Float"),
	DOUBLE(6, "Double"),
	BYTE_ARRAY(7, "ByteArray"),
	STRING(8, "String"),
	LIST(9, "List"),
	COMPOUND(10, "Compound"),
	INT_ARRAY(11, "IntArray"),
	LONG_ARRAY(12, "LongArray");
	
	override fun toString(): String = string
	
	companion object : LinkedHashMap<Byte, NbtType>() {
		init {
			putAll(values().associateBy { it.id })
		}
		
		override fun get(key: Byte): NbtType = super.get(key) ?: throw IllegalArgumentException("Invalid NbtType id $key")
	}
}