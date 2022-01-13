package y18

private const val day = "10"

private data class Light10(val x: Int, val y: Int, val vx: Int, val vy: Int) {
    fun move(n: Int = 1) = copy(x = x + n * vx, y = y + n * vy)
}

private fun String.toLight() =
    split("""[<, >]+""".toRegex()).let {
        Light10(it[1].toInt(), it[2].toInt(), it[4].toInt(), it[5].toInt())
    }


fun main() {
    val input = readInput("Day${day}")

    val lights = input.map { it.toLight() }
    (1..11111).forEach { n ->
        val grid = lights.map { it.move(n) }
        val xs = grid.sortedBy { it.x }.let { it.first().x..it.last().x }
        val ys = grid.sortedBy { it.y }.let { it.first().y..it.last().y }
        if (xs.last - xs.first <= 64) {
            ys.forEach { y ->
                xs.forEach { x ->
                    print(if (grid.any { it.x == x && it.y == y }) "@" else ".")
                }
                println()
            }
            println("\n\n------ $n seconds --------\n")
        }
    }

}
