package me.mrpingu.nbt.extension

inline val <T> T.exhaustive: T get() = this

inline fun <R> runTry(printStackTrace: Boolean = false, block: () -> R) =
	try {
		block()
	} catch (ignored: Exception) {
		if (printStackTrace) ignored.printStackTrace()
		null
	}
