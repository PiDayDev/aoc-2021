package y20

private const val day = "16"

private data class TicketField(val name: String, val ranges: List<IntRange>) {
    operator fun contains(n: Int) = ranges.any { n in it }
}

fun main() {
    fun String.toRange() = split("-").let { (a, b) -> a.toInt()..b.toInt() }

    fun List<String>.validRanges(): List<TicketField> = this
        .takeWhile { it.isNotBlank() }
        .map { it.split("""(: | or )""".toRegex()) }
        .map { TicketField(it.first(), it.drop(1).map { r -> r.toRange() }) }

    fun List<String>.theirTickets() = this
        .takeLastWhile { it[0] != 'n' }
        .map { ticket -> ticket.split(",").map { it.toInt() } }

    fun part1(input: List<String>): Int {
        val ranges = input.validRanges()
        return input.theirTickets()
            .flatten()
            .filter { n -> ranges.none { range -> n in range } }
            .sum()
    }

    fun part2(input: List<String>): Long {
        val fields = input.validRanges()
        val tickets = input.theirTickets()
            .filter { list -> list.all { n -> fields.any { range -> n in range } } }
        val myTicket = input
            .dropLastWhile { it.isNotBlank() }.dropLastWhile { it.isBlank() }
            .last()
            .split(",")
            .map { it.toLong() }

        val indices = fields.indices
        val candidates = fields.associateWith { field ->
            indices.filter { idx ->
                tickets.all { ticket -> ticket[idx] in field }
            }.toMutableList()
        }.toMutableMap()
        while (candidates.values.any { it.size > 1 }) {
            val unavailable = candidates.filterValues { it.size == 1 }.flatMap { it.value }.toSet()
            candidates.values
                .filter { it.size > 1 }
                .forEach { it.removeAll(unavailable) }
        }
        return candidates
            .filterKeys { it.name.startsWith("departure") }
            .values
            .flatten()
            .map { myTicket[it] }
            .reduce { a, b -> a * b }
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 25972)
    val p2 = part2(input)
    println(p2)
    check(p2 == 622670335901L)
}
