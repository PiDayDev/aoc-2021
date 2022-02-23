package y20

private const val day = "24"

private enum class Color {
    B, W;

    fun invert() = values().first { it != this }
}

private data class Loc(val e: Int = 0, val ne: Int = 0) {
    fun move(dir: String) = when (dir) {
        "e" -> copy(e = e + 1)
        "ne" -> copy(ne = ne + 1)
        "se" -> copy(e = e + 1, ne = ne - 1)
        "w" -> copy(e = e - 1)
        "nw" -> copy(e = e - 1, ne = ne + 1)
        "sw" -> copy(ne = ne - 1)
        else -> this
    }

    fun moveAll() = listOf(move("e"), move("ne"), move("se"), move("w"), move("nw"), move("sw"))
}

fun main() {
    fun String.toMoves() = this
        .split("""(?<=[ew])""".toRegex())
        .filter { p -> p.isNotBlank() }

    fun List<String>.floor() = this
        .map { it.toMoves() }
        .map { it.fold(Loc(0, 0)) { loc, dir -> loc.move(dir) } }
        .fold(mapOf<Loc, Color>()) { map, t -> map + (t to (map[t] ?: Color.W).invert()) }
        .filter { it.value == Color.B }
        .keys

    fun Set<Loc>.gameOfTiles() = this
        .flatMap { it.moveAll() }
        .groupBy { it }
        .mapValues { (_, list) -> list.size }
        .filter { (tile, count) -> count == 2 || tile in this && count == 1 }
        .keys

    fun part1(input: List<String>): Int = input.floor().size

    fun part2(input: List<String>): Int =
        generateSequence(input.floor()) { it.gameOfTiles() }
            .take(101)
            .last()
            .size

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 341)
    val p2 = part2(input)
    println(p2)
    check(p2 == 3700)
}
