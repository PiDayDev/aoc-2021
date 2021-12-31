package y16

private const val day = 15

private data class Disc(val positions: Int, val modulus: Int) {
    operator fun contains(k: Int) = (k + modulus) % positions == 0
}

fun main() {
    fun parse(s: String): Disc {
        val disc = s.substringAfter("#").take(1).toInt()
        val pos = s.substringAfter(" has ").substringBefore(" pos").toInt()
        val start = s.substringAfter("at position ").substringBefore(".").toInt()
        return Disc(positions = pos, modulus = (start + disc) % pos)
    }

    fun solve(discs: List<Disc>) = generateSequence(1) { it + 1 }
        .dropWhile { discs.any { disc -> it !in disc } }
        .first()

    val input = readInput("Day${day}")
    val discs = input.map { parse(it) }
    println(solve(discs))
    println(solve(discs + Disc(positions = 11, modulus = discs.size + 1)))
}
