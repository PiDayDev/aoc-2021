package y20

private const val day = "17"

interface ConwayCube {
    fun neighbors(): List<ConwayCube>
}

private data class Conway3D(val x: Int, val y: Int, val z: Int) : ConwayCube {
    override fun neighbors(): List<Conway3D> = neighbors
    private val neighbors: List<Conway3D> by lazy {
        (-1..1).flatMap { dx ->
            (-1..1).flatMap { dy ->
                (-1..1).map { dz ->
                    copy(x = x + dx, y = y + dy, z = z + dz)
                }
            }
        }.minus(this)
    }
}

private data class Conway4D(val x: Int, val y: Int, val z: Int, val w: Int) : ConwayCube {
    override fun neighbors(): List<Conway4D> = neighbors
    private val neighbors: List<Conway4D> by lazy {
        (-1..1).flatMap { dx ->
            (-1..1).flatMap { dy ->
                (-1..1).flatMap { dz ->
                    (-1..1).map { dw ->
                        copy(x = x + dx, y = y + dy, z = z + dz, w = w + dw)
                    }
                }
            }
        }.minus(this)
    }
}

private fun <CUBE : ConwayCube> Set<CUBE>.game(): Set<CUBE> {
    val curr = toSet()
    val activeNeighbors: Map<CUBE, Int> = flatMap { it.neighbors() }
        .groupBy {
            @Suppress("UNCHECKED_CAST")
            it as CUBE
        }.mapValues { (_, list) -> list.size }
    return activeNeighbors.filter { (cube, count) ->
        count == 3 || count == 2 && cube in curr
    }.keys
}


fun main() {
    fun <CUBE : ConwayCube> List<String>.boot(
        toCube: (x: Int, y: Int) -> CUBE
    ): Int {
        val initial =
            flatMapIndexed { y, row ->
                row.mapIndexedNotNull { x, c ->
                    if (c == '#') toCube(x, y) else null
                }
            }.toSet()
        return generateSequence(initial) { it.game() }
            .take(7)
            .last()
            .size
    }

    fun part1(input: List<String>): Int = input.boot { x, y -> Conway3D(x, y, 0) }
    fun part2(input: List<String>): Int = input.boot { x, y -> Conway4D(x, y, 0, 0) }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 267)
    val p2 = part2(input)
    println(p2)
    check(p2 == 1812)
}
