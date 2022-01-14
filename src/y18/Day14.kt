package y18

private const val input = 236021

private data class Recipe14(val recipes: StringBuilder, var elf1: Int, var elf2: Int) {
    constructor(s: String) : this(StringBuilder(s), 0, 1)

    fun next(): Recipe14 {
        val v1 = recipes[elf1].digitToInt()
        val v2 = recipes[elf2].digitToInt()
        recipes.append(v1 + v2)
        val len = recipes.length
        elf1 = (elf1 + v1 + 1) % len
        elf2 = (elf2 + v2 + 1) % len
        return this
    }
}

fun main() {
    fun part1(input: Int) = generateSequence(Recipe14("37")) { it.next() }
        .dropWhile { it.recipes.length < input + 10 }
        .first()
        .recipes
        .drop(input)
        .take(10)

    fun part2(input: String) = generateSequence(Recipe14("37")) { it.next() }
        .dropWhile { input !in it.recipes.takeLast(8) }
        .first()
        .recipes
        .indexOf(input)

    println(part1(input))
    println(part2(input.toString()))
}
