package y16

import kotlin.math.max

private const val input = "njfxhljp"

private data class Room(val r: Int, val c: Int, val path: String) {
    fun next(): List<Room> {
        val (uc, dc, lc, rc) = (input + path).md5h().toList().map { it in "bcdef" }
        val ud = uc && r > 0
        val dd = dc && r < 3
        val ld = lc && c > 0
        val rd = rc && c < 3
        val list = mutableListOf<Room>()
        if (ud) list += Room(r - 1, c, path + "U")
        if (dd) list += Room(r + 1, c, path + "D")
        if (ld) list += Room(r, c - 1, path + "L")
        if (rd) list += Room(r, c + 1, path + "R")
        return list
    }

    fun isVault() = r == 3 && c == 3
}

fun main() {
    fun part1(): String {
        var curr = listOf(Room(0, 0, ""))
        while (curr.none { it.isVault() }) {
            curr = curr.flatMap { it.next() }
        }
        return curr.first { it.isVault() }.path
    }

    fun part2(): Int {
        var curr = listOf(Room(0, 0, ""))
        var len = 0
        while (curr.isNotEmpty()) {
            val (v, nv) = curr.flatMap { it.next() }.partition { it.isVault() }
            len = max(len, v.maxOfOrNull { it.path.length } ?: 0)
            curr = nv
        }
        return len
    }

    println(part1())
    println(part2())
}
