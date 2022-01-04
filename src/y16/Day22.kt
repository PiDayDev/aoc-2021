package y16

private const val day = "22"

private data class Node(val x: Int, val y: Int, val size: Int, val used: Int, val avail: Int) {
    infix fun fitsIn(other: Node) = this != other && used in (1..other.avail)
}

private val spaces = """\s+""".toRegex()
private fun String.toNode(): Node {
    val (name, size, used, avail) = split(spaces)
    val (_, x, y) = name.split("-").map { it.drop(1) }
    return Node(
        x = x.toInt(),
        y = y.toInt(),
        size = size.dropLast(1).toInt(),
        used = used.dropLast(1).toInt(),
        avail = avail.dropLast(1).toInt()
    )
}

fun main() {


    fun nodes(input: List<String>) = input.drop(2).map { it.toNode() }

    fun part1(input: List<String>): Int {
        val nodes = nodes(input)
        return nodes.sumOf { n -> nodes.count { it fitsIn n } }
    }

    fun part2(input: List<String>): Int {
        val nodes = nodes(input)
        val grid = nodes.groupBy { it.x }.mapValues { (_, v) -> v.sortedBy { it.y } }
        val maxX = grid.keys.maxOf { it }
        val maxY = grid.values.first().maxOf { it.y }

        for (y in 0..maxY) {
            for (x in 0..maxX) {
                if (x == maxX && y == 0) print("G")
                else print(
                    when (grid[x]!![y].used) {
                        0 -> "_"
                        in 1..99 -> "."
                        else -> "#"
                    }
                )
            }
            println()
        }
        /*
         This one I solved by hand on the grid...
         - it takes 94 steps to move empty node "_" to the top row just to the left of "G"
         - then it takes 5 steps to move the pair "_G" one cell left:
            ._G  =>  .G_  =>  .G.  =>  .G.  =>  .G.  =>  _G.
            ...      ...      .._      ._.      _..      ...
           and it must be done 34 times ==> 5 * 34 = 170 steps
         - after this, the top left corner is "_G" and it takes one more step to move G to corner
         */
        return 94 + 5 * 34 + 1
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
