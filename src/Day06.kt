private const val day = "06"

fun main() {
    fun Map<Int, Long>.evolve(): Map<Int, Long> {
        val births = this[0] ?: 0L
        val after = (this - 0).mapKeys { (k, _) -> k - 1 }.toMutableMap()
        after[8] = births
        after[6] = births + (after[6] ?: 0)
        return after
    }

    fun fish(input: List<String>, days: Int): Long {
        val fish = input.first().split(",").map { it.toInt() }
        val fishCount = fish.groupBy { it }.map { (k, v) -> k to v.size.toLong() }.toMap()
        val endFish = (1..days).fold(fishCount) { a, _ -> a.evolve() }
        return endFish.values.fold(0L) { a, v -> a + v }
    }

    val input = readInput("Day${day}")
    println(fish(input, 80))
    println(fish(input, 256))
}
