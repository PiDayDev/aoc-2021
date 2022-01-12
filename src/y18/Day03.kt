package y18

private const val day = "03"


private data class Claim(val id: Int, val xRange: IntRange, val yRange: IntRange) {
    fun contains(x: Int, y: Int) = x in xRange && y in yRange

    fun overlaps(c: Claim) =
        xRange.any { x ->
            yRange.any { y ->
                c.contains(x, y)
            }
        }
}

fun main() {
    val regex = """[^0-9]+""".toRegex()

    fun String.claim(): Claim {
        val parts = split(regex).filter { it.isNotBlank() }.map { it.toInt() }
        val (id, x, y, w, h) = parts
        return Claim(id, x until x + w, y until y + h)
    }

    fun part1(input: List<String>): Int {
        val claims = input.map { it.claim() }
        val counts = mutableMapOf<Pair<Int, Int>, Int>()
        claims.forEach {
            it.xRange.forEach { x ->
                it.yRange.forEach { y ->
                    counts[x to y] = 1 + (counts[x to y] ?: 0)
                }
            }
        }
        return counts.count { (_, v) -> v > 1 }
    }

    fun part2(input: List<String>): Int {
        val claims = input.map { it.claim() }
        val seen = mutableSetOf<Int>()
        val overlaps = mutableSetOf<Int>()
        claims.forEach { claim1 ->
            seen.add(claim1.id)
            claims.filter { it.id > claim1.id }
                .forEach { claim2 ->
                    seen.add(claim2.id)
                    if (claim1.overlaps(claim2)) {
                        overlaps.add(claim1.id)
                        overlaps.add(claim2.id)
                    }
                }
        }
        return (seen - overlaps).first()
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
