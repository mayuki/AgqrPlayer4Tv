package org.misuzilla.agqrplayer4tv

/**
 * Created by Tomoyo on 2017/01/21.
 */
class Greeter(val message: String) {
    private val prefix = "Hello!"

    fun hello() {
        println("$prefix $message")
    }
}

class Program {
    fun main() {
        Greeter("Konnichiwa!").hello() // => Konnichiwa!

val value = NantokaType.ARIENAI
val message = when (value) {
    NantokaType.SUGOI -> "Sugoi"
    NantokaType.YABAI -> "Yabai"
    else -> "Majikayo"
}
println(message) // => "Majikayo"
    }
}

enum class NantokaType
{
    SUGOI,
    YABAI,
    ARIENAI
}