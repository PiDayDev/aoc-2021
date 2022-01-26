package y18

private const val day = 24

private data class Unit24(
    val id: String,
    var units: Int,
    val hp: Int,
    val weak: List<String>,
    val immune: List<String>,
    val attack: Int,
    val type: String,
    val initiative: Int,
    val isInfection: Boolean = false
) {
    fun effectivePower() = units.coerceAtLeast(0) * attack

    fun damage(enemy: Unit24): Int = effectivePower() * multiplier(enemy)

    private fun multiplier(enemy: Unit24) = when (type) {
        in enemy.immune -> 0
        in enemy.weak -> 2
        else -> 1
    }

    fun attack(enemy: Unit24) {
        enemy.units -= (damage(enemy) / enemy.hp).coerceAtMost(enemy.units)
    }

    override fun toString() =
        "$id($units units, $hp HP, $type $attack, init.$initiative, weak=$weak, immune=$immune)"

}

private fun String.toUnit(id: String, isInfection: Boolean): Unit24 {
    val units = substringBefore(" units each with ").toInt()
    val hp = substringAfter(" units each with ").substringBefore(" hit points").toInt()
    val attack = substringAfter("that does ").substringBefore(" ").toInt()
    val attackType = substringBefore(" damage at").takeLastWhile { it != ' ' }
    val initiative = substringAfter("initiative ").toInt()
    val weaknesses = when {
        "weak" !in this -> emptyList()
        else -> substringAfter("weak to ").substringBefore(";").substringBefore(")").split(", ")
    }
    val immunities = when {
        "immune" !in this -> emptyList()
        else -> substringAfter("immune to ").substringBefore(";").substringBefore(")").split(", ")
    }
    return Unit24(id, units, hp, weaknesses, immunities, attack, attackType, initiative, isInfection)
}

fun main() {
    fun MutableMap<Unit24, Unit24>.choose(
        attackers: List<Unit24>,
        defenders: List<Unit24>
    ): MutableMap<Unit24, Unit24> {
        val defenders2 = defenders.toMutableList()
        attackers
            .sortedWith(compareByDescending<Unit24> { it.effectivePower() }.thenComparingInt { -it.initiative })
            .forEach { a ->
                val damageMap = defenders2.associateWith { a.damage(it) }
                val max = damageMap.values.maxOfOrNull { it }
                if (max != null && max > 0) {
                    val target = damageMap
                        .filterValues { it == max }
                        .keys
                        .maxByOrNull { it.effectivePower().toLong() * 100L + it.initiative }!!
                    defenders2.remove(target)
                    this[a] = target
                }
            }
        return this
    }

    fun fight(immuneSystem: List<Unit24>, infection: List<Unit24>): Pair<List<Unit24>, List<Unit24>>? {
        val a2d = mutableMapOf<Unit24, Unit24>()
        a2d.choose(immuneSystem, infection)
        a2d.choose(infection, immuneSystem)
        if (a2d.isEmpty())
            return null
        val targets = a2d.values.toSet().map { it.id }
        val safe1 = immuneSystem.filter { it.id !in targets }
        val safe2 = infection.filter { it.id !in targets }
        a2d.toList()
            .sortedByDescending { it.first.initiative }
            .forEach { (attacker, defender) -> attacker.attack(defender) }
        val (survivors2, survivors1) = a2d.values.partition { it.isInfection }

        return (safe1 + survivors1).filter { it.units > 0 } to (safe2 + survivors2).filter { it.units > 0 }
    }

    fun parse(input: List<String>): Pair<MutableList<Unit24>, MutableList<Unit24>> {
        val good = mutableListOf<Unit24>()
        val bad = mutableListOf<Unit24>()
        var isInfection = false
        input.forEachIndexed { j,it->
            when {
                "Infection:" in it -> isInfection = true
                "units" in it ->
                    if (isInfection) bad += it.toUnit("🦠$j", true)
                    else good += it.toUnit("🩺$j", false)
            }
        }
        return good to bad
    }

    fun fightToDeath(
        good: List<Unit24>,
        bad: List<Unit24>,
        log: Boolean = false
    ): Pair<List<Unit24>, List<Unit24>>? {
        var immuneSystem = good.toList()
        var infection = bad.toList()
        while (immuneSystem.isNotEmpty() && infection.isNotEmpty()) {
            if (log) {
                println("-".repeat(120))
                immuneSystem.forEach(::println)
                println(" VS ")
                infection.forEach(::println)
            }

            val result = fight(immuneSystem, infection) ?: return null
            immuneSystem = result.first
            infection = result.second
        }
        return immuneSystem to infection
    }

    fun part1(input: List<String>): Int {
        val (good, bad) = parse(input)
        val (immuneSystem, infection) = fightToDeath(good, bad, true)!!
        return (immuneSystem + infection).sumOf { it.units }
    }

    fun part2(input: List<String>): Int {
        val (winner) = generateSequence(1) { it + 1 }
            .takeWhile { it > 0 }
            .mapNotNull { boost ->
                val (good, bad) = parse(input)
                val boosted = good.map { it.copy(attack = it.attack + boost) }
                val fightToDeath = fightToDeath(boosted, bad)
                println("$boost => $fightToDeath")
                fightToDeath
            }
            .first { (good, bad) -> good.isNotEmpty() && bad.isEmpty() }
        return winner.sumOf { it.units }
    }

    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(testInput)
        check(test1 == 5216) { "Expected 5216 - Actual $test1 " }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println("\n" + "#".repeat(80) + "\n")
    println(part2(input))
}
