package y18

private const val day = "04"

typealias Guard = Map<Int, Int>

fun main() {
    fun String.guardId() =
        substringAfter("#").substringBefore(" ").toInt()

    fun String.minute() =
        substringAfter(":").substringBefore("]").toInt()

    fun findGuardSleepPatterns(input: List<String>): MutableMap<Int, Guard> {
        val guards = mutableMapOf<Int, Guard>()
        var currentGuard = 0
        var startSleep = 0
        input.sorted().forEach {
            if ("Guard #" in it) {
                currentGuard = it.guardId()
            } else if ("asleep" in it) {
                startSleep = it.minute()
            } else if ("wakes" in it) {
                val guard = guards.getOrDefault(currentGuard, emptyMap()).toMutableMap()
                (startSleep until it.minute()).forEach { m ->
                    guard[m] = 1 + guard.getOrDefault(m, 0)
                }
                guards[currentGuard] = guard
            }
        }
        return guards
    }

    fun part1(input: List<String>): Int {
        val guards = findGuardSleepPatterns(input)
        val (sleepy, sleepPattern) = guards.maxByOrNull { (_, map) -> map.values.sum() }!!
        return sleepy * sleepPattern.maxByOrNull { (_, c) -> c }!!.key
    }

    fun part2(input: List<String>): Int {
        val guards = findGuardSleepPatterns(input)
        val favoriteSleepMinutes = guards.mapValues { (_,v)->v.maxByOrNull { (_,c)->c }!! }
        val (dreamy, minute) = favoriteSleepMinutes.maxByOrNull { (_,v)->v.value }!!
        return dreamy * minute.key
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
