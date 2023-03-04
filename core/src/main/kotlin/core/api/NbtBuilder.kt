@file:Suppress(
	"unused",
	"MemberVisibilityCanBePrivate",
)

package br.com.luizrcs.nbt.core.api

import br.com.luizrcs.nbt.core.tag.*
import br.com.luizrcs.nbt.core.tag.NbtType.*

@DslMarker
annotation class NbtBuilderDsl

inline fun buildNbtList(
	name: String? = null,
	builder: NbtListBuilder.() -> Unit,
): NbtList = NbtListBuilder(name).apply(builder).build()

inline fun buildNbtCompound(
	name: String? = null,
	builder: NbtCompoundBuilder.() -> Unit,
): NbtCompound = NbtCompoundBuilder(name).apply(builder).build()

inline fun buildNbt(
	name: String? = null,
	builder: NbtCompoundBuilder.() -> Unit,
): NbtCompound = buildNbtCompound(name, builder)

@NbtBuilderDsl
abstract class NbtBuilder<T : NbtAny, U : Any> internal constructor(protected val name: String?) {
	protected abstract val entries: U
	
	abstract fun build(): T
	
	@PublishedApi internal fun List<*>.toListOfByte() = map { NbtByte(it as Byte) }
	@PublishedApi internal fun List<*>.toListOfShort() = map { NbtShort(it as Short) }
	@PublishedApi internal fun List<*>.toListOfInt() = map { NbtInt(it as Int) }
	@PublishedApi internal fun List<*>.toListOfLong() = map { NbtLong(it as Long) }
	@PublishedApi internal fun List<*>.toListOfFloat() = map { NbtFloat(it as Float) }
	@PublishedApi internal fun List<*>.toListOfDouble() = map { NbtDouble(it as Double) }
	@PublishedApi internal fun List<*>.toListOfByteArray() = map { NbtByteArray(it as ByteArray) }
	@PublishedApi internal fun List<*>.toListOfString() = map { NbtString(it as String) }
	@PublishedApi internal fun List<*>.toListOfIntArray() = map { NbtIntArray(it as IntArray) }
	@PublishedApi internal fun List<*>.toListOfLongArray() = map { NbtLongArray(it as LongArray) }
	
	@Suppress("UNCHECKED_CAST")
	@PublishedApi internal fun <T : NbtAny> List<*>.toListOf() = map { (it as NbtAny).clone(null) as T }
	
	@PublishedApi internal inline fun <reified T : Any> parseList(value: List<T>) = when (val clazz = T::class) {
		Byte::class         -> NbtList(BYTE, value.toListOfByte(), false)
		Short::class        -> NbtList(SHORT, value.toListOfShort(), false)
		Integer::class      -> NbtList(INT, value.toListOfInt(), false)
		Long::class         -> NbtList(LONG, value.toListOfLong(), false)
		Float::class        -> NbtList(FLOAT, value.toListOfFloat(), false)
		Double::class       -> NbtList(DOUBLE, value.toListOfDouble(), false)
		ByteArray::class    -> NbtList(BYTE_ARRAY, value.toListOfByteArray(), false)
		String::class       -> NbtList(STRING, value.toListOfString(), false)
		IntArray::class     -> NbtList(INT_ARRAY, value.toListOfIntArray(), false)
		LongArray::class    -> NbtList(LONG_ARRAY, value.toListOfLongArray(), false)
		NbtByte::class      -> NbtList(BYTE, value.toListOf<NbtByte>(), false)
		NbtShort::class     -> NbtList(SHORT, value.toListOf<NbtShort>(), false)
		NbtInt::class       -> NbtList(INT, value.toListOf<NbtInt>(), false)
		NbtLong::class      -> NbtList(LONG, value.toListOf<NbtLong>(), false)
		NbtFloat::class     -> NbtList(FLOAT, value.toListOf<NbtFloat>(), false)
		NbtDouble::class    -> NbtList(DOUBLE, value.toListOf<NbtDouble>(), false)
		NbtByteArray::class -> NbtList(BYTE_ARRAY, value.toListOf<NbtByteArray>(), false)
		NbtString::class    -> NbtList(STRING, value.toListOf<NbtString>(), false)
		NbtList::class      -> NbtList(LIST, value.toListOf<NbtList>(), false)
		NbtCompound::class  -> NbtList(COMPOUND, value.toListOf<NbtCompound>(), false)
		NbtIntArray::class  -> NbtList(INT_ARRAY, value.toListOf<NbtIntArray>(), false)
		NbtLongArray::class -> NbtList(LONG_ARRAY, value.toListOf<NbtLongArray>(), false)
		Any::class          -> throw IllegalArgumentException("NbtList elements must be of the same type")
		else                -> throw IllegalArgumentException("Unsupported type ${clazz.simpleName} for NbtList")
	}
}

class NbtCompoundBuilder @PublishedApi internal constructor(name: String?) :
	NbtBuilder<NbtCompound, MutableNbtCompoundMap>(name) {
	
	override val entries = MutableNbtCompoundMap()
	
	override fun build() = NbtCompound(entries, name, false)
	
	fun put(name: String, tag: NbtAny) {
		if (tag is NbtEnd) throw IllegalArgumentException("Cannot add an NbtEnd to an NbtCompound")
		else entries[name] = tag
	}
	
	fun put(name: String, byte: Byte): Unit = put(name, NbtByte(byte, name))
	fun put(name: String, short: Short): Unit = put(name, NbtShort(short, name))
	fun put(name: String, int: Int): Unit = put(name, NbtInt(int, name))
	fun put(name: String, long: Long): Unit = put(name, NbtLong(long, name))
	fun put(name: String, float: Float): Unit = put(name, NbtFloat(float, name))
	fun put(name: String, double: Double): Unit = put(name, NbtDouble(double, name))
	fun put(name: String, byteArray: ByteArray): Unit = put(name, NbtByteArray(byteArray, name))
	fun put(name: String, string: String): Unit = put(name, NbtString(string, name))
	fun put(name: String, intArray: IntArray): Unit = put(name, NbtIntArray(intArray, name))
	fun put(name: String, longArray: LongArray): Unit = put(name, NbtLongArray(longArray, name))
	
	fun putNbtList(name: String, builder: NbtListBuilder.() -> Unit): Unit = put(name, NbtListBuilder(name).apply(builder).build())
	fun putNbtCompound(name: String, builder: NbtCompoundBuilder.() -> Unit): Unit = put(name, NbtCompoundBuilder(null).apply(builder).build())
	
	inline fun <reified T : Any> put(name: String, value: List<T>) = put(name, parseList(value))
}

class NbtListBuilder @PublishedApi internal constructor(name: String?) :
	NbtBuilder<NbtList, MutableNbtListList>(name) {
	
	override val entries = MutableNbtListList()
	
	private var elementsType = entries.firstOrNull()?.type
	
	override fun build() = NbtList(elementsType ?: END, entries, false, name)
	
	fun add(tag: NbtAny) {
		if (elementsType == null) elementsType = tag.type
		else if (elementsType != tag.type) throw IllegalArgumentException("NbtList elements must be of the same type")
		
		entries.add(tag)
	}
	
	fun add(byte: Byte): Unit = add(NbtByte(byte))
	fun add(short: Short): Unit = add(NbtShort(short))
	fun add(int: Int): Unit = add(NbtInt(int))
	fun add(long: Long): Unit = add(NbtLong(long))
	fun add(float: Float): Unit = add(NbtFloat(float))
	fun add(double: Double): Unit = add(NbtDouble(double))
	fun add(byteArray: ByteArray): Unit = add(NbtByteArray(byteArray))
	fun add(string: String): Unit = add(NbtString(string))
	fun add(intArray: IntArray): Unit = add(NbtIntArray(intArray))
	fun add(longArray: LongArray): Unit = add(NbtLongArray(longArray))
	
	fun addNbtList(builder: NbtListBuilder.() -> Unit): Unit = add(NbtListBuilder(null).apply(builder).build())
	fun addNbtCompound(builder: NbtCompoundBuilder.() -> Unit): Unit = add(NbtCompoundBuilder(null).apply(builder).build())
	
	inline fun <reified T : Any> add(value: List<T>) = add(parseList(value))
}