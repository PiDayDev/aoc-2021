package y20

import y20.Border.*

private const val day = "20"

enum class Border { N_CW, E_CW, S_CW, W_CW, W_CCW, S_CCW, E_CCW, N_CCW }

private val matchingSides = mapOf(
    N_CW to S_CCW,
    E_CW to W_CCW,
    S_CCW to N_CW,
    W_CCW to E_CW,
    N_CCW to S_CW,
    E_CCW to W_CW,
    S_CW to N_CCW,
    W_CW to E_CCW,
)

private data class Tile(val id: Int = 0, val image: List<String>) {
    val borders by lazy {
        mapOf(
            N_CW to image.first(),
            E_CW to image.joinToString("") { "${it.last()}" },
            S_CCW to image.last(),
            W_CCW to image.joinToString("") { "${it.first()}" },
            N_CCW to image.first().reversed(),
            E_CCW to image.joinToString("") { "${it.last()}" }.reversed(),
            S_CW to image.last().reversed(),
            W_CW to image.joinToString("") { "${it.first()}" }.reversed(),
        )
    }

    fun matchingBorder(tile: Tile): Pair<Border, Border>? {
        if (this == tile) return null
        borders.forEach { (mySide, myContent) ->
            tile.borders.forEach { (theirSide, theirContent) ->
                if (myContent == theirContent) {
                    return mySide to theirSide
                }
            }
        }
        return null
    }

    fun shave(): Tile =
        copy(image = image.drop(1).dropLast(1).map { it.drop(1).dropLast(1) })

    fun transform(src: Border, dst: Border): Tile =
        if (src == dst) this else transitions[src to dst]!!.invoke(this)

    fun rotateCounterclockwise() = copy(image = image.first().indices.map { j ->
        image.map { it.reversed()[j] }.joinToString("")
    })

    fun rotateClockwise() = copy(image = image.first().indices.map { j ->
        image.map { it[j] }.reversed().joinToString("")
    })

    fun flipVertical() = copy(image = image.reversed())

    fun flipHorizontal() = copy(image = image.map { it.reversed() })

    companion object {
        val baseTransitions: Map<Pair<Border, Border>, Tile.() -> Tile> = mapOf(
            N_CW to N_CCW to Tile::flipHorizontal,
            S_CW to S_CCW to Tile::flipHorizontal,
            E_CW to W_CCW to Tile::flipHorizontal,
            W_CW to E_CCW to Tile::flipHorizontal,
            N_CCW to N_CW to Tile::flipHorizontal,
            S_CCW to S_CW to Tile::flipHorizontal,
            W_CCW to E_CW to Tile::flipHorizontal,
            E_CCW to W_CW to Tile::flipHorizontal,
            N_CW to S_CCW to Tile::flipVertical,
            S_CW to N_CCW to Tile::flipVertical,
            E_CW to E_CCW to Tile::flipVertical,
            W_CW to W_CCW to Tile::flipVertical,
            S_CCW to N_CW to Tile::flipVertical,
            N_CCW to S_CW to Tile::flipVertical,
            E_CCW to E_CW to Tile::flipVertical,
            W_CCW to W_CW to Tile::flipVertical,
            N_CW to E_CW to Tile::rotateClockwise,
            E_CW to S_CW to Tile::rotateClockwise,
            S_CW to W_CW to Tile::rotateClockwise,
            W_CW to N_CW to Tile::rotateClockwise,
            E_CCW to N_CCW to Tile::rotateCounterclockwise,
            S_CCW to E_CCW to Tile::rotateCounterclockwise,
            W_CCW to S_CCW to Tile::rotateCounterclockwise,
            N_CCW to W_CCW to Tile::rotateCounterclockwise,
        )
        val transitions: Map<Pair<Border, Border>, Tile.() -> Tile> by lazy {
            val t2 = join(baseTransitions, baseTransitions)
            val t3 = join(t2, baseTransitions)
            t3 + t2 + baseTransitions
        }
    }

}


private fun join(
    t1: Map<Pair<Border, Border>, Tile.() -> Tile>,
    t2: Map<Pair<Border, Border>, Tile.() -> Tile>
) =
    t1.flatMap { (pair1, f1) ->
        t2.mapNotNull { (pair2, f2) ->
            if (pair1.second == pair2.first && pair1.first != pair2.second)
                pair1.first to pair2.second to { tile: Tile -> tile.let(f1).let(f2) }
            else null
        }
    }.toMap()

fun main() {
    fun List<String>.toTiles() =
        chunked(12).map { list ->
            val id = list.first().removeSurrounding("Tile ", ":").toInt()
            val image = list.drop(1).takeWhile { it.isNotBlank() }
            Tile(id, image)
        }

    fun part1(input: List<String>): Long {
        val tiles = input.toTiles()
        val corners = tiles.filter { tile ->
            tiles.mapNotNull { it.matchingBorder(tile) }.size == 2
        }
        return corners.map { it.id.toLong() }.reduce { a, b -> a * b }
    }

    fun compose(tiles: List<Tile>): Tile {
        val rest = tiles.toMutableList()
        val tilesById = rest.associateBy { it.id }
        val matches = rest.associate { t1 ->
            t1.id to rest.filter { t2 -> t1.matchingBorder(t2) != null }.map { it.id }
        }
        val starter = rest.removeAt(0)
        val jigsaw = mutableMapOf(starter to (0 to 0))
        val current = mutableListOf(starter)
        while (rest.isNotEmpty()) {
            val next = mutableListOf<Tile>()
            val candidates: List<Tile> = current.flatMap { matches[it.id] ?: listOf() }
                .distinct()
                .mapNotNull { tilesById[it] }
                .filterNot { it.id in jigsaw.keys.map { e -> e.id } }
            candidates.forEach { me ->
                val (borders, them) = current.firstNotNullOf { me.matchingBorder(it)?.let { p -> p to it } }
                val (myBorder, theirBorder) = borders
                val result = me.transform(myBorder, matchingSides[theirBorder]!!)
                next += result
                val dx = when (theirBorder) {
                    E_CW, E_CCW -> +1
                    W_CW, W_CCW -> -1
                    else -> 0
                }
                val dy = when (theirBorder) {
                    N_CW, N_CCW -> -1
                    S_CW, S_CCW -> +1
                    else -> 0
                }
                jigsaw[result] = (jigsaw[them] ?: (0 to 0)).let { (x, y) -> x + dx to y + dy }
            }
            current.clear()
            current.addAll(next)
            rest.removeAll(candidates)
        }

        val posToTile = jigsaw.toList().associate { (a, b) -> b to a }.toMap()

        val image = mutableListOf<String>()
        for (y in -11..11) {
            val rowOfTiles = posToTile
                .filterKeys { y == it.second }
                .toList()
                .sortedBy { (pos, _) -> pos.first }
                .map { it.second.shave().image }
            if (rowOfTiles.isNotEmpty())
                image.addAll(rowOfTiles.reduce { rows, tile -> rows.zip(tile) { a, b -> a + b } })
        }
        return Tile(image = image)
    }

    fun List<String>.findPattern(positions: List<Pair<Int, Int>>): Int {
        val ys = indices
        val xs = first().indices
        var count = 0
        for (y in ys)
            for (x in xs)
                if (positions.map { (dx, dy) -> getOrNull(y + dy)?.getOrNull(x + dx) }.all { it == '#' })
                    count++
        return count
    }

    fun part2(input: List<String>): Int {
        val tiles = input.toTiles().shuffled()
        val picture = compose(tiles)

        val nessie = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
        """.trimIndent().split('\n')
        val nessiePositions = nessie.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == '#') x to y else null
            }
        }

        val pictureMorphs = listOf(
            picture,
            picture.rotateCounterclockwise(),
            picture.rotateClockwise(),
            picture.rotateClockwise().rotateClockwise()
        ).flatMap {
            listOf(it, it.flipHorizontal())
        }

        val monsterCount = pictureMorphs.maxOf { it.image.findPattern(nessiePositions) }
        val pixelCount = picture.image.sumOf { it.count { c -> c == '#' } }
        return pixelCount - monsterCount * nessiePositions.size
    }

    val input = readInput("Day$day")
    val p1 = part1(input)
    println(p1)
    check(p1 == 17148689442341L)
    val p2 = part2(input)
    println(p2)
    check(p2 == 2009)
}
