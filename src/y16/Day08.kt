package y16

private const val day = "08"

private typealias Screen = List<List<Boolean>>

fun Screen.display() =
    joinToString("\n", postfix = "\n") { row -> row.joinToString("") { if (it) "@@" else ".." } }

fun main() {
    fun screen(): Screen = List(6) { List(50) { false } }

    fun Screen.rect(w: Int, h: Int) = mapIndexed { r, row ->
        row.mapIndexed { c, value ->
            r < h && c < w || value
        }
    }

    fun Screen.rotateRow(y: Int, by: Int) = mapIndexed { r, row ->
        if (r == y)
            List(row.size) { c ->
                row[(row.size + c - by % row.size) % row.size]
            }
        else row
    }

    fun Screen.rotateColumn(x: Int, by: Int) = mapIndexed { r, row ->
        row.mapIndexed { c, value ->
            when (c) {
                x -> this[(size + r - by % size) % size][c]
                else -> value
            }
        }
    }

    fun Screen.exec(operation: String) =
        if ("rect" in operation) {
            val (w, h) = operation.substringAfter(" ").split("x").map { it.toInt() }
            rect(w, h)
        } else if ("rotate row" in operation) {
            val (y, by) = operation.substringAfter("y=").split(" by ").map { it.toInt() }
            rotateRow(y, by)
        } else if ("rotate col" in operation) {
            val (x, by) = operation.substringAfter("x=").split(" by ").map { it.toInt() }
            rotateColumn(x, by)
        } else {
            this
        }

    fun solve(input: List<String>): Int {
        val s = input.fold(screen()) { screen, operation ->
            println(screen.display())
            screen.exec(operation)
        }
        println(s.display())
        return s.sumOf { row -> row.count { it } }
    }

    val input = readInput("Day${day}")
    println(solve(input))
}
