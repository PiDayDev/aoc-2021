import kotlin.math.absoluteValue

private const val day = "19"

private typealias Beacon = Triple<Int, Int, Int>

private operator fun Beacon.minus(b: Beacon) =
    Beacon(first - b.first, second - b.second, third - b.third)

private operator fun Beacon.plus(b: Beacon) =
    Beacon(first + b.first, second + b.second, third + b.third)

private data class Scanner(val id: Int, val beacons: List<Beacon>, val self: Beacon = Beacon(0, 0, 0)) {

    val memoizedMatch = mutableMapOf<Scanner, Scanner?>()

    fun findBestMatch(s: Scanner) = when {
        memoizedMatch.containsKey(s) -> {
            memoizedMatch[s]
        }
        else -> {
            val result = s.rotations().firstNotNullOfOrNull { matchingWith(it) }
            memoizedMatch[s] = result
            result
        }
    }

    private fun matchingWith(other: Scanner): Scanner? {
        beacons.forEach { myBeacon ->
            val theirAliases = other.rotations()
            theirAliases.forEach { alias ->
                val theirBeacons = alias.beacons
                theirBeacons.forEach { theirBeacon ->
                    val delta = myBeacon - theirBeacon
                    val translatedBeacons = theirBeacons.map { it + delta }
                    val matches = translatedBeacons.intersect(beacons.toSet())
                    if (matches.size >= 12) {
                        return alias.copy(beacons = translatedBeacons, self = alias.self + delta)
                    }
                }
            }
        }
        return null
    }

    fun rotations(): List<Scanner> {
        val rotatedBeacons: List<List<Beacon>> = beacons.map { it.rotations() }
        val indices = rotatedBeacons.first().indices
        return indices.map { j ->
            Scanner(id, rotatedBeacons.map { it[j] })
        }
    }
}

private fun String.toBeacon(): Beacon {
    val (a, b, c) = split(",").map { it.toInt() }
    return Triple(a, b, c)
}

private fun Beacon.rotations() = listOf(
    Triple(+first, +second, third),
    Triple(-second, +first, third),
    Triple(-first, -second, third),
    Triple(+second, -first, third),
    Triple(-first, +second, -third),
    Triple(-second, -first, -third),
    Triple(+first, -second, -third),
    Triple(+second, +first, -third),
).flatMap {
    listOf(
        Triple(it.first, it.second, it.third),
        Triple(it.second, it.third, it.first),
        Triple(it.third, it.first, it.second),
    )
}

fun main() {
    fun build(some: List<String>): Scanner {
        val id = some.first().substringAfter("scanner ").substringBefore(" ").toInt()
        val beacons = some.drop(1).takeWhile { it.isNotBlank() }.map { it.toBeacon() }
        return Scanner(id, beacons)
    }

    fun normalizeScanners(input: List<String>): MutableList<Scanner> {
        val positions = input.mapIndexedNotNull { index, s -> if ("scanner" in s) index else null }
        val scanners = positions.map { line -> build(input.drop(line)) }.toMutableList()

        val reference = 0
        val normalized = scanners.filter { it.id == reference }.toMutableList()
        scanners.removeIf { s -> normalized.any { it.id == s.id } }

        var next = normalized.toList()

        while (scanners.isNotEmpty()) {
            println("${scanners.map { it.id }} ==> VALID: ${normalized.map { it.id }}")
            next = scanners.mapNotNull {  candidate ->
                next.firstNotNullOfOrNull { v ->
                    v.findBestMatch(candidate)
                }
            }
            val nextIds = next.map { e -> e.id }
            normalized += next
            scanners.removeIf { it.id in nextIds }
            scanners.shuffle()
        }
        return normalized
    }

    fun part1(scanners: List<Scanner>) = scanners.flatMap { it.beacons }.distinct().size

    fun part2(scanners: List<Scanner>): Int {
        val manhattans = scanners.mapIndexedNotNull { i, a ->
            scanners
                .filterIndexed { j, _ -> j > i }
                .maxOfOrNull { b ->
                    val (x, y, z) = a.self - b.self
                    x.absoluteValue + y.absoluteValue + z.absoluteValue
                }
        }
        return manhattans.maxOf { it }
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${day}_test")
        check(part1(normalizeScanners(testInput)) == 79)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    val scanners = normalizeScanners(input)
    println(part1(scanners))
    println(part2(scanners))
}
