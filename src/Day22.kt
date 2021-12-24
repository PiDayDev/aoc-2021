private const val day = "22"

private data class Instr(val on: Boolean, val xs: IntRange, val ys: IntRange, val zs: IntRange)

private fun String.parse(): Instr {
    val (on, xs, ys, zs) = split("""[ ,]""".toRegex())
    val (x1, x2) = xs.toRange()
    val (y1, y2) = ys.toRange()
    val (z1, z2) = zs.toRange()
    return Instr(on == "on", x1..x2, y1..y2, z1..z2)
}

private fun String.toRange() = substringAfter("=").split("..").map { it.toInt() }

fun main() {
    fun part1(input: List<String>): Int {
        val reactor =
            (0..100).map {
                (0..100).map {
                    (0..100).map {
                        false
                    }.toMutableList()
                }
            }
        input.map { it.parse() }
            .forEach { (on, xs, ys, zs) ->
                (-50..50).intersect(xs).forEach { x ->
                    (-50..50).intersect(ys).forEach { y ->
                        (-50..50).intersect(zs).forEach { z ->
                            reactor[x + 50][y + 50][z + 50] = on
                        }
                    }
                }
            }

        return reactor.sumOf { floor -> floor.sumOf { line -> line.count { it } } }
    }

    fun List<IntRange>.stops() =
        flatMap { listOf(it.first, it.last + 1) }.distinct().sorted()

    fun follow(
        step: Instr,
        xStops: List<Int>,
        yStops: List<Int>,
        zStops: List<Int>,
        on: MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>>
    ) {
        val xx = xStops.filter { it in step.xs }
        val yy = yStops.filter { it in step.ys }
        val zz = zStops.filter { it in step.zs }
        if (step.on) {
            xx.forEach { x ->
                if (x !in on) {
                    on[x] = HashMap(835)
                }
                val onx = on[x]!!
                yy.forEach { y ->
                    if (y !in onx) {
                        onx[y] = HashMap(835)
                    }
                    val onxy = onx[y]!!
                    zz.forEach { z ->
                        onxy[z] = true
                    }
                }
            }
        } else {
            xx.forEach { x ->
                if (x in on) {
                    val onx = on[x]!!
                    yy.forEach { y ->
                        if (y in onx) {
                            zz.forEach { z ->
                                onx[y]!!.remove(z)
                            }
                        }
                    }
                    on[x] = onx.filterValues { it.isNotEmpty() }.toMutableMap()
                }
            }
            on.filterValues { it.isEmpty() }.forEach { (k, _) -> on.remove(k) }
        }
    }

    fun howManyOn(
        on: Map<Int, Map<Int, Map<Int, Boolean>>>,
        xStops: List<Int>,
        yStops: List<Int>,
        zStops: List<Int>
    ): Long {
        var total = 0L
        on.forEach { (x, xMap) ->
            val xSize = xStops[xStops.indexOf(x) + 1] - x
            xMap.forEach { (y, yMap) ->
                val ySize = yStops[yStops.indexOf(y) + 1] - y
                yMap.forEach { (z, on: Boolean) ->
                    val zSize = zStops[zStops.indexOf(z) + 1] - z
                    if (on) {
                        check(xSize > 0)
                        check(ySize > 0)
                        check(zSize > 0)
                        total += xSize.toLong() * ySize.toLong() * zSize.toLong()
                    }
                }
            }
        }
        return total
    }

    fun part2(input: List<String>): Long {
        val instructions = input.map { it.parse() }
        val xStops = instructions.map { it.xs }.stops()
        val yStops = instructions.map { it.ys }.stops()
        val zStops = instructions.map { it.zs }.stops()
        println(
            """
            ${xStops.size}: ${xStops.minOrNull()} .. ${xStops.maxOrNull()}
            ${yStops.size}: ${yStops.minOrNull()} .. ${yStops.maxOrNull()}
            ${zStops.size}: ${zStops.minOrNull()} .. ${zStops.maxOrNull()}
        """.trimIndent()
        )

        var total = 0L
        xStops.chunked(3).forEachIndexed { index, someX ->
            val on: MutableMap<Int, MutableMap<Int, MutableMap<Int, Boolean>>> = mutableMapOf()
            instructions.forEachIndexed { i, step ->
                follow(step, someX, yStops, zStops, on)
            }
            total += howManyOn(on, xStops, yStops, zStops)
        }
        return total
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
