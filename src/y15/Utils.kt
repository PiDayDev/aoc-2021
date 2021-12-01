package y15

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, ext: String = "txt") = File("src/y15", "$name.$ext").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String {
    val digest: ByteArray = MessageDigest.getInstance("MD5").digest(toByteArray())
    return digest.toList().chunked(2).joinToString("") { BigInteger(1, it.toByteArray()).toString(16) }
}

fun String.md5b() = MessageDigest.getInstance("MD5").digest(toByteArray())

fun <T> permutations(list: List<T>): List<List<T>> {
    if (list.isEmpty()) return emptyList()
    if (list.size == 1) return listOf(list)
    return list.flatMap { elem -> permutations(list - elem).map { it + elem } }
}
