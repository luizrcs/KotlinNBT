package br.com.luizrcs.nbt.core.extension

inline val <T> T.exhaustive: T get() = this

inline val tab get() = "    "