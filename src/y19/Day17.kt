package y19

private const val day = 17

fun main() {
    fun part1(codes: List<Long>): Int {
        val sb = StringBuilder()
        IntCodeProcessor(codes).process(iterator { }) { sb.append(Char(it.toInt())) }
        val rows = sb.split("\n").filter { it.isNotBlank() }
        println("     " + rows[0].indices.joinToString("") { if(it<10) " " else "${it / 10}" })
        println("     " + rows[0].indices.joinToString("") { "${it % 10}" })
        rows.forEachIndexed { y, s ->
            println("${y.toString().padStart(4)} $s")
        }
        val scaffolds = rows
            .flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, c ->
                    if (c == '#') x to y
                    else null
                }
            }
            .toSet()

        val crossings = scaffolds.filter { (x, y) ->
            listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1).count { it in scaffolds } >= 4
        }
        return crossings.sumOf { (x, y) -> x * y }
    }

    fun part2(codes: List<Long>): Int {
        return codes.size
    }

    try {
        val testInput = readInput("Day${day}_test").codes()
        check(part1(testInput) == 1)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val codes = readInput("Day${day}").codes()
    val p1 = part1(codes)
    println(p1)
    check(p1 in 621..20299)
    println(part2(codes))
}
