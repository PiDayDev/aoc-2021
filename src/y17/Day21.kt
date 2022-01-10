package y17

private const val day = 21

private fun String.rotoflip2(): List<String> {
    val (a, b) = split("/")
    val q = a.take(1) + b.take(1)
    val r = a.takeLast(1) + b.takeLast(1)
    return listOf(
        "$a/$b", "${a.reversed()}/${b.reversed()}",
        "$b/$a", "${b.reversed()}/${a.reversed()}",
        "$q/$r", "${q.reversed()}/${r.reversed()}",
        "$r/$q", "${r.reversed()}/${q.reversed()}",
    ).distinct()
}

private fun String.rotoflip3(): List<String> {
    val list = split("/")
    val (a, b, c) = list
    val (q, r, s) = (0..2).map { d -> list.joinToString("") { it.drop(d).take(1) } }
    return listOf(
        "$a/$b/$c", "${a.reversed()}/${b.reversed()}/${c.reversed()}",
        "$c/$b/$a", "${c.reversed()}/${b.reversed()}/${a.reversed()}",
        "$q/$r/$s", "${q.reversed()}/${r.reversed()}/${s.reversed()}",
        "$s/$r/$q", "${s.reversed()}/${r.reversed()}/${q.reversed()}",
    ).distinct()
}

private fun String.rotoflip() = when (count { it == '/' }) {
    2 -> rotoflip3()
    else -> rotoflip2()
}

fun main() {

    fun List<String>.enhance(rules: Map<String, List<String>>): List<String> {
        val blockSize = 2 + size % 2
        return chunked(blockSize).map { rows ->
            val windows: List<List<String>> = (1..size / blockSize).map {
                val start = (it - 1) * blockSize
                val window = rows.joinToString("/") { row -> row.substring(start, start + blockSize) }
                rules[window]!!
            }
            (0..blockSize).map { r -> windows.joinToString("") { it[r] } }
        }.flatten()
    }

    fun List<String>.onAfter(iterations: Int): Int {
        val rules = flatMap { rule ->
            val (src, dst) = rule.split(" => ")
            src.rotoflip().map { it to dst.split("/") }
        }.toMap()
        val start = listOf(".#.", "..#", "###")
        val end = (1..iterations).fold(start) { p, _ -> p.enhance(rules) }
        return end.sumOf { it.count { c -> c == '#' } }
    }

    fun part1(input: List<String>) = input.onAfter(5)

    fun part2(input: List<String>) = input.onAfter(18)

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
