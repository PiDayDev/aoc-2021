package y16

import java.io.File
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, ext: String = "txt") = File("src/y16", "$name.$ext").readLines()

fun String.md5b(): ByteArray =
    MessageDigest.getInstance("MD5").digest(toByteArray())

fun String.md5h(): String =
    md5b().joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

fun <T> permutations(list: List<T>): List<List<T>> {
    if (list.isEmpty()) return emptyList()
    if (list.size == 1) return listOf(list)
    return list.flatMap { elem -> permutations(list - elem).map { it + elem } }
}
