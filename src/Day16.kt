private const val day = "16"
private const val marker = "!"

private data class Packet(val version: Int, val typeId: Int, val body: String) {
    val isLiteral = typeId == 4
    val isOperator = !isLiteral

    fun parseBody(): Triple<Long?, List<Packet>, String> = when {
        isLiteral -> {
            val (value, rest) = valueAndRest()
            Triple(value, emptyList(), rest)
        }
        else -> {
            val (packets, rest) = subPacketsAndRest()
            Triple(null, packets, rest)
        }
    }

    fun valueAndRest(): Pair<Long, String> {
        check(isLiteral)
        val value = body
            .chunked(5)
            .asSequence()
            .takeWhile { it.length == 5 }
            .scan(marker) { acc, curr ->
                val result = acc + curr.drop(1).toLong(2).toString(16)
                if (curr.startsWith("0")) result.drop(1) else result
            }
            .dropWhile { marker in it }
            .first()
        val rest = body.drop(5 * value.length)
        return value.toLong(16) to rest
    }

    fun subPacketsAndRest(): Pair<List<Packet>, String> {
        check(isOperator)
        val lengthTypeId = body.first().digitToInt()
        val data = body.drop(1)
        val packets = mutableListOf<Packet>()
        var rest: String
        if (lengthTypeId == 0) {
            val bitLength = data.take(15).toInt(2)
            var subPackets = data.drop(15).take(bitLength)
            while (subPackets.any { it == '1' }) {
                val (packet, rem) = parseNextSub(subPackets)
                packets += packet
                subPackets = rem
            }
            rest = data.drop(15 + bitLength)
        } else {
            val count = data.take(11).toInt(2)
            rest = data.drop(11)
            while (packets.size < count) {
                val (packet, rem) = parseNextSub(rest)
                packets += packet
                rest = rem
            }
        }
        return packets to rest
    }

    private fun parseNextSub(rest: String): Pair<Packet, String> {
        val packet = rest.toPacket()!!
        val (_, _, rem) = packet.parseBody()
        return packet to rem
    }

    fun getVersions(): List<Int> {
        val (_, packets, _) = parseBody()
        return packets.flatMap { it.getVersions() } + version
    }

}

private fun String.unpack() = this
    .map { it.digitToInt(16).toString(2).padStart(4, '0') }
    .joinToString("")

private fun String.toPacket() =
    if (length <= 6) null
    else Packet(
        version = take(3).toInt(2),
        typeId = drop(3).take(3).toInt(2),
        body = drop(6)
    )

fun main() {


    fun part1(input: List<String>) = input.map {
        it.unpack().toPacket()!!.getVersions().sum()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    try {
        val testInput = readInput("Day${day}_test")
        val part1 = part1(testInput)
        check(part1 == listOf(16, 12, 23, 31)) { "Part1 = $part1" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day${day}")
    println(part1(input))
    println(part2(input))
}
