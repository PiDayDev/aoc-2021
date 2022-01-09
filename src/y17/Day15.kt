package y17

private const val day = 15
private const val aFactor: ULong = 16807u
private const val bFactor: ULong = 48271u
private const val modulus: ULong = 2147483647u
fun main() {
    fun ULong.bit16() = toString(2).padStart(128, '0').takeLast(16)

    fun part1(starts: List<ULong>) =
        generateSequence(starts.first() to starts.last()) { (a, b) -> a * aFactor % modulus to b * bFactor % modulus }
            .drop(1)
            .take(40_000_000)
            .count { (a, b) -> a.bit16() == b.bit16() }

    fun part2(starts: List<ULong>): Int {
        val zero = 0u.toULong()
        val genA = generateSequence(starts.first()) { it * aFactor % modulus }
            .drop(1)
            .filter { it % 4u == zero }
        val genB = generateSequence(starts.last()) { it * bFactor % modulus }
            .drop(1)
            .filter { it % 8u == zero }

        return genA.zip(genB).take(5_000_000).count { (a, b) -> a.bit16() == b.bit16() }
    }

    val input = readInput("Day${day}")
    val starts = input.map { it.substringAfter("with ").toULong() }
    println(part1(starts))
    println(part2(starts))
}
