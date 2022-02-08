package y19

import java.io.File
import java.io.PrintStream
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, ext: String = "txt") = File("src/y19", "$name.$ext").readLines()
fun printStream(name: String, ext: String = "txt") = PrintStream(File("src/y19", "$name.$ext"))

fun String.md5b(): ByteArray =
    MessageDigest.getInstance("MD5").digest(toByteArray())

fun String.md5h(): String =
    md5b().joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

fun <T> permutations(list: List<T>): List<List<T>> {
    if (list.isEmpty()) return emptyList()
    if (list.size == 1) return listOf(list)
    return list.flatMap { elem -> permutations(list - elem).map { it + elem } }
}

fun List<String>.codes() = flatMap { r -> r.split(",").map { it.toLong() } }

fun <T> minDistances(start: T, finish: T? = null, getNeighbors: (T) -> List<T>): Map<T, Int> {
    val distances: MutableMap<T, Int> = mutableMapOf(start to 0)
    var last = distances.toMap()
    var d = 0
    while (last.isNotEmpty() && finish !in distances) {
        d++
        val neighbors = last.keys
            .flatMap { getNeighbors(it) }
            .distinct()
            .filter { it !in distances.keys }
        last = neighbors.associateWith { d }
        distances.putAll(last)
    }
    return distances
}
