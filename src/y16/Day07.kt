package y16

private const val day = "07"

fun main() {


    fun String.nets() = split("""[\[\]]""".toRegex()).chunked(2)
    fun String.hypernets() = nets().mapNotNull { it.getOrNull(1) }
    fun String.supernets() = nets().mapNotNull { it.getOrNull(0) }

    fun String.hasAbba() = length >= 4 && windowed(4).any { it[0] == it[3] && it[1] == it[2] && it[0] != it[1] }

    fun part1(input: List<String>) = input.count {
        it.hasAbba() && it.hypernets().none { net -> net.hasAbba() }
    }

    fun String.listAba() = windowed(3).filter { it[0] == it[2] && it[0] != it[1] }

    fun String.hasBab(aba: String) = listAba().any { it.take(2) == aba.takeLast(2) }

    fun part2(input: List<String>) = input.count {
        val abaList = it.supernets().flatMap { net -> net.listAba() }
        it.hypernets().any { net -> abaList.any { aba -> net.hasBab(aba) } }
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
