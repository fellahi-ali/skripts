import okio.BufferedSink
import okio.appendingSink
import okio.buffer
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> List<T>.joinLines() = joinToString(separator = "\n")
fun <T> List<T>.joinLines(operation: (T) -> String) = joinToString(transform = operation, separator = "\n")
fun <T> environmentVariable(notfoundMesssage: String): ReadOnlyProperty<T, String> =
    LazyEnvironmentVariable(notfoundMesssage, null)

fun <T> optionalEnvironmentVariable(default: String): ReadOnlyProperty<T, String> = LazyEnvironmentVariable("", default)
class LazyEnvironmentVariable<in T>(val notfoundMesssage: String, val default: String?) : ReadOnlyProperty<T, String> {
    override fun getValue(thisRef: T, property: KProperty<*>): String {
        val name: String = property.name
        val value = System.getenv(name) ?: default
        if (value != null) return value
        else {
            val missingKey = "ERROR: Missing environment variable\n $ export $name='xxxxx'\n\n$notfoundMesssage"
            System.err.println(missingKey)
            System.exit(1); error("missingKey")
        }
    }
}

fun <T> T.debug(name: String): T {
    println("DEBUG: $name = ${toString()}")
    return this
}

fun <T> List<T>.debugList(name: String): List<T> {
    println("<List $name with $size elements>")
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
    println("</list>")
    return this
}

fun BufferedSink.newLine() = writeUtf8("\n")
fun File.okAppendingSink(): BufferedSink = this.appendingSink().buffer()
