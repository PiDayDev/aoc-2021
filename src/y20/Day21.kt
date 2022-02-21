package y20

private const val day = "21"

data class Food(val ingredients: List<String>, val allergens: List<String>)

fun main() {

    fun List<String>.toFoods(): List<Food> = map {
        val i = it.substringBefore("(").split(" ").filter { s -> s.isNotBlank() }
        val a = it.substringAfter("(contains ").substringBefore(")").split(", ").filter { s -> s.isNotBlank() }
        Food(i, a)
    }

    fun List<Food>.findAllergenMap(): Map<String, String> {
        val allergenToPossibleIngredients = flatMap { it.allergens }
            .distinct()
            .associateWith { a ->
                filter { a in it.allergens }.map { it.ingredients.toSet() }.reduce { f1, f2 -> f1.intersect(f2) }
            }
            .toMutableMap()

        val allergenToActualIngredient = mutableMapOf<String, String>()
        while (allergenToPossibleIngredients.isNotEmpty()) {
            val certain = allergenToPossibleIngredients.filterValues { it.size == 1 }
            certain.forEach { (a, i) ->
                allergenToActualIngredient[a] = i.first()
                allergenToPossibleIngredients.remove(a)
            }
            val assigned = allergenToActualIngredient.values.toSet()
            allergenToPossibleIngredients += allergenToPossibleIngredients.mapValues { (_, v) -> v - assigned }
        }
        return allergenToActualIngredient
    }

    fun part1(input: List<String>): Int {
        val foods = input.toFoods()
        val allergenToActualIngredient = foods.findAllergenMap()
        return foods.flatMap { it.ingredients }.count { it !in allergenToActualIngredient.values }
    }

    fun part2(input: List<String>) = input
        .toFoods()
        .findAllergenMap()
        .toList()
        .sortedBy { (a) -> a }
        .joinToString(",") { (_, i) -> i }


    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 2280)
    val p2 = part2(input)
    println(p2)
    check(p2 == "vfvvnm,bvgm,rdksxt,xknb,hxntcz,bktzrz,srzqtccv,gbtmdb")
}
