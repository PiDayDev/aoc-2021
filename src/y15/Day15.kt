package y15

private const val day = "15"

data class Ingredient(
    val name: String,
    val capacity: Int,
    val durability: Int,
    val flavor: Int,
    val texture: Int,
    val calories: Int
) {
    constructor(
        name: String,
        capacity: String,
        durability: String,
        flavor: String,
        texture: String,
        calories: String
    ) : this(name, capacity.toInt(), durability.toInt(), flavor.toInt(), texture.toInt(), calories.toInt())
}

fun main() {
    val rowRegex =
        Regex("""(\w+): capacity (-?[0-9]+), durability (-?[0-9]+), flavor (-?[0-9]+), texture (-?[0-9]+), calories (-?[0-9]+)""")

    fun parse(input: List<String>) = input.map { row ->
        val (name, capacity, durability, flavor, texture, calories) = rowRegex.find(row)!!.destructured
        Ingredient(name, capacity, durability, flavor, texture, calories)
    }

    fun Map<Ingredient, Int>.score(): Int {
        val list = toList()
        val capacity = list.sumOf { (ing, q) -> ing.capacity * q }.coerceAtLeast(0)
        val durability = list.sumOf { (ing, q) -> ing.durability * q }.coerceAtLeast(0)
        val flavor = list.sumOf { (ing, q) -> ing.flavor * q }.coerceAtLeast(0)
        val texture = list.sumOf { (ing, q) -> ing.texture * q }.coerceAtLeast(0)
        return capacity * durability * flavor * texture
    }

    fun Map<Ingredient, Int>.calories() = toList().sumOf { (ing, q) -> ing.calories * q }

    fun variants(ingredients: List<Ingredient>, quantity: Int): List<Map<Ingredient, Int>> {
        val first = ingredients.first()
        val rest = ingredients.drop(1)
        if (rest.isEmpty()) return listOf(mapOf(first to quantity))
        return (0..quantity).flatMap { q ->
            variants(rest, quantity - q).map { it + (first to q) }
        }
    }

    val input = readInput("Day${day}")

    val ingredients = parse(input)
    val variants = variants(ingredients, 100)

    println(variants.maxOf { it.score() })
    println(variants.filter { it.calories() == 500 }.maxOf { it.score() })
}
