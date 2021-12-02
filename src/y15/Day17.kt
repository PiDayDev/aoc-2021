package y15

private const val day = "17"

fun main() {
    fun List<Int>.combosTo(rest: Int): List<List<Int>> = when (rest) {
        0 -> listOf(listOf())
        else -> flatMapIndexed { i, c ->
            val residual = rest - c
            if (residual < 0) listOf()
            else drop(i + 1).combosTo(residual).map { it + c }
        }
    }

    val input = readInput("Day${day}").map { it.toInt() }

    val combos = input.combosTo(150)
    println(combos.size)

    val minContainers = combos.minOf { it.size }
    println(combos.count { it.size == minContainers })
}
