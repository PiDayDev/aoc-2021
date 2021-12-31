package y16

private const val input = "qzyelonm"

fun main() {
    fun md5(s: String): String {
        val bytes = s.md5b()
        return bytes.joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
    }

    fun solve(hash: (String) -> String): Int {
        val v = generateSequence(0) { it + 1 }
            .map { it to input + it }
            .map { (idx, it) -> idx to hash(it) }
            .windowed(1001)
            .filter { list ->
                val curr = list[0]
                val rest = list.drop(1)
                val result = """(.)\1\1""".toRegex().find(curr.second)
                if (result == null) {
                    false
                } else {
                    val k5 = result.groupValues[1].repeat(5)
                    rest.any { s ->
                        val b = k5 in s.second
                        if (b)
                            println("${k5[0]} in $curr ==> $k5 in ${s.second}")
                        b
                    }
                }
            }
            .map { it[0].first }
            .take(64)
            .last()
        check(v > 12473)
        return v
    }

    println(solve { md5(it) })
    println(solve { (0..2016).fold(it) { acc, _ -> md5(acc) } })
}
