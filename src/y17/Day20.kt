package y17

private const val day = 20

fun main() {
    fun String.acceleration(): Int = this
        .substringAfter("a=<")
        .substringBefore(">")
        .split(",")
        .map { it.toInt() }
        .let { (x, y, z) -> x * x + y * y + z * z }

    fun part1(input: List<String>) =
        input.indices.minByOrNull { input[it].acceleration() }!!

    fun String.toParticle(): Sequence<Triple<Long, Long, Long>> {
        val (p, v, a) = split("""[pva><=]+(, [pva><=]+)?""".toRegex()).filter { it.isNotBlank() }
        val (px, py, pz) = p.split(",").map { it.toLong() }
        var (vx, vy, vz) = v.split(",").map { it.toLong() }
        val (ax, ay, az) = a.split(",").map { it.toLong() }
        return generateSequence(Triple(px, py, pz)) { (x, y, z) ->
            vx += ax
            vy += ay
            vz += az
            Triple(x + vx, y + vy, z + vz)
        }
    }

    fun part2(input: List<String>): Int {
        var particles = input.map { it.toParticle() }.map { it.iterator() }
        repeat(5000) {
            val step = particles.map { it.next() }
            val repeated = step.groupBy { it }.filter { (_, v) -> v.size > 1 }.keys
            val keep = step.mapIndexedNotNull { index, triple ->
                if (triple in repeated) null else index
            }
            particles = particles.filterIndexed { j, _ -> j in keep }
        }
        return particles.size
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
