import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

/*
ADVANCED DATA STRUCTURES
 */
interface PriorityQ<T : Comparable<T>?> {
    fun add(element: T): Boolean
    fun updatePriority(oldElement: T, newElement: T): Boolean
    fun remove(element: T): Boolean
    fun top(): T?
    fun peek(): T?
    operator fun contains(element: T): Boolean
    val size: Int
    fun isEmpty(): Boolean
    fun isNotEmpty() = !isEmpty()
    fun clear()
}

class MyQ<T : Comparable<T>?>(private val queue: PriorityQueue<T> = PriorityQueue<T>()) :
    PriorityQ<T>, Queue<T> by queue {

    override fun updatePriority(oldElement: T, newElement: T): Boolean {
        val wasPresent = queue.removeIf { it == oldElement }
        if (wasPresent) queue.add(newElement)
        return wasPresent
    }

    override fun top(): T? = poll()

}


