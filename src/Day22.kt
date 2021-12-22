private const val day = "22"

private data class Instr(val on: Boolean, val xs: IntRange, val ys: IntRange, val zs: IntRange)

private fun String.parse(): Instr {
    val (on, xs, ys, zs) = split("""[ ,]""".toRegex())
    val (x1, x2) = xs.toRange()
    val (y1, y2) = ys.toRange()
    val (z1, z2) = zs.toRange()
    return Instr(on == "on", x1..x2, y1..y2, z1..z2)
}

private fun String.toRange() = substringAfter("=").split("..").map { it.toInt() }

fun main() {
    fun part1(input: List<String>): Int {
        val reactor =
            (0..100).map {
                (0..100).map {
                    (0..100).map {
                        false
                    }.toMutableList()
                }
            }
        input.map { it.parse() }
            .forEach { (on, xs, ys, zs) ->
                (-50..50).intersect(xs).forEach { x ->
                    (-50..50).intersect(ys).forEach { y ->
                        (-50..50).intersect(zs).forEach { z ->
                            reactor[x + 50][y + 50][z + 50] = on
                        }
                    }
                }
            }

        return reactor.sumOf { floor -> floor.sumOf { line -> line.count { it } } }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
