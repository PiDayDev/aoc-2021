private const val day = "08"

fun main() {
    fun part1(input: List<String>): Int {
        val uniq = listOf(2, 3, 4, 7)
        return input.map { it.substringAfter(" | ") }
            .flatMap { it.split(" ") }
            .count { it.length in uniq }
    }

    fun List<List<Char>>.intersection() =
        reduce { a: Collection<Char>, b: Collection<Char> -> a.intersect(b.toSet()) }.toList()

    fun deduce(row: String): Int {
        val digits = row.substringBefore(" | ").split(" ")
        val d1 = digits.first { it.length == 2 }.toList()
        val d7 = digits.first { it.length == 3 }.toList()
        val d4 = digits.first { it.length == 4 }.toList()
        val d235 = digits.filter { it.length == 5 }.map { it.toList() }
        val d069 = digits.filter { it.length == 6 }.map { it.toList() }
        val d8 = digits.first { it.length == 7 }.toList()

        val tcTlBrBc = d069.intersection()
        val tcMcBc = d235.intersection()
        val tc = d7.toSet() - d1.toSet()
        val bc = tcMcBc.toSet() - d4.toSet() - d7.toSet()
        val mc = tcMcBc.toSet() - tc.toSet() - bc.toSet()
        val tl = tcTlBrBc.toSet() - d7.toSet() - bc.toSet()
        val tr = d7.toSet() - tcTlBrBc.toSet()
        val br = d1.toSet() - tr.toSet()
        val bl = d8.toSet() - tc.toSet() - mc.toSet() - bc.toSet() - tl.toSet() - tr.toSet() - br.toSet()

        fun classify(s: String) = when (s.toList().toSet()) {
            tr + br -> 1
            tr + br + tc -> 7
            tr + br + tl + mc -> 4
            tc + mc + bc + tr + bl -> 2
            tc + mc + bc + tl + br -> 5
            tc + mc + bc + tr + br -> 3
            tc + mc + bc + tl + bl + br -> 6
            tc + mc + bc + tl + tr + br -> 9
            tc + bc + tl + tr + bl + br -> 0
            else -> 8
        }

        val (k, h, da, u) = row.substringAfter(" | ").split(" ").map { classify(it) }
        return k * 1000 + h * 100 + da * 10 + u
    }

    fun part2(input: List<String>) = input.sumOf { deduce(it) }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
