private const val day = "25"

private fun List<String>.east() = map {
    it.mapIndexed { index, c ->
        when {
            c == '>' && it[(index + 1) % it.length] == '.' -> '.'
            c == '.' && it[(it.length + index - 1) % it.length] == '>' -> '>'
            else -> c
        }
    }.joinToString("")
}

private fun List<String>.south() = mapIndexed { r, row ->
    row.mapIndexed { c, char ->
        when {
            char == 'v' && this[(r + 1) % size][c] == '.' -> '.'
            char == '.' && this[(size + r - 1) % size][c] == 'v' -> 'v'
            else -> char
        }
    }.joinToString("")
}

fun main() {

    fun part1(input: List<String>) =
        generateSequence(input) {
            val next = it.east().south()
            if (next == it) null else next
        }.count()

    val input = readInput("Day${day}")
    println(part1(input))
}
