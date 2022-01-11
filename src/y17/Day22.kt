package y17

private const val day = 22

fun Pair<Int, Int>.left() = -second to +first
fun Pair<Int, Int>.right() = +second to -first
fun Pair<Int, Int>.reverse() = -first to -second


fun main() {
    fun Char.flip() = if (this == '#') '.' else '#'

    fun part1(input: List<String>): Int {
        var r = input.size / 2
        var c = input.first().length / 2
        var dir = -1 to 0
        val grid = mutableMapOf<Pair<Int, Int>, Char>()
        input.forEachIndexed { ri, row ->
            row.forEachIndexed { ci, char ->
                grid[ri to ci] = char
            }
        }
        var infections = 0
        repeat(10000) {
            val state = grid[r to c] ?: '.'
            val infected = state == '#'
            if (!infected) infections++
            dir = if (infected) dir.right() else dir.left()
            grid[r to c] = state.flip()
            r += dir.first
            c += dir.second
        }
        return infections
    }

    fun Char.evolve() = when (this) {
        '.' -> 'W'
        'W' -> '#'
        '#' -> 'F'
        else -> '.'
    }

    fun part2(input: List<String>): Int {
        var r = input.size / 2
        var c = input.first().length / 2
        var dir = -1 to 0
        val grid = mutableMapOf<Pair<Int, Int>, Char>()
        input.forEachIndexed { ri, row ->
            row.forEachIndexed { ci, char ->
                grid[ri to ci] = char
            }
        }
        var infections = 0
        repeat(10_000_000) {
            val state = grid[r to c] ?: '.'
            dir = when (state) {
                '.' -> dir.left()
                '#' -> dir.right()
                'F' -> dir.reverse()
                else -> dir
            }
            val evolved = state.evolve()
            if (evolved == '#') infections++
            grid[r to c] = evolved
            r += dir.first
            c += dir.second
        }
        return infections
    }


    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
