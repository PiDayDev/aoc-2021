package y16

import y15.md5b


fun main() {
    fun ByteArray.hasGoodMd5() =
        this[0].toInt() == 0 && this[1].toInt() == 0 && this[2].toUByte().toString(16).length < 2

    fun part1(key: String) =
        generateSequence(1) { it + 1 }
            .map { key + it }
            .map { it.md5b() }
            .filter { it.hasGoodMd5() }
            .take(8)
            .map { it[2].toString(16) }
            .joinToString("")

    fun part2(key: String) =
        generateSequence(1) { it + 1 }
            .map { key + it }
            .map { it.md5b() }
            .filter { it.hasGoodMd5() }
            .map {
                val pos = it[2].toInt()
                val digit = it[3].toUByte().toString(16).take(1)
                pos to digit
            }
            .filter { (pos, _) -> pos in 0..7 }
            .scan("________") { password, (pos, digit) ->
                val next = when (password[pos]) {
                    '_' -> password.slice(0 until pos) + digit + password.slice(pos + 1 until password.length)
                    else -> password
                }
                println(next)
                next
            }
            .dropWhile { "_" in it }
            .first()


    val input = "cxdnnyjw"
    println(part1(input))
    println(part2(input))
}
