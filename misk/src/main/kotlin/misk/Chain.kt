package misk

import kotlin.reflect.KFunction

interface Chain {
    val action: Any
    val args: List<Any?>
    val function: KFunction<*>
    fun proceed(args: List<Any?>): Any?
}
