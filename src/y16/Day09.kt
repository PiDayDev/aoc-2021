package y16

private const val day = "09"

fun main() {
    fun String.decompress(protocolVersion: Int = 1): Long {
        var inMarker = false
        var marker = ""
        var markLen = 0
        var markRep = 0
        var data = ""
        var len = 0L
        for (c in this) {
            if (inMarker) {
                if (c == ')') {
                    inMarker = false
                    data = ""
                    val (l, r) = marker.split("x").map { it.toInt() }
                    markLen = l
                    markRep = r
                } else {
                    marker += c
                }
            } else if (markLen == 0) {
                if (c == '(') {
                    inMarker = true
                    marker = ""
                } else {
                    len++
                }
            } else if (data.length < markLen) {
                data += c
                if (data.length == markLen) {
                    len += markRep * when (protocolVersion) {
                        2 -> data.decompress(2)
                        else -> data.length.toLong()
                    }
                    data = ""
                    markRep = 0
                    markLen = 0
                }
            } else {
                len++
            }
        }
        return len
    }

    fun part1(input: String) = input.decompress()

    fun part2(input: String) = input.decompress(2)

    check("X(8x2)(3x3)ABCY".decompress() == "X(3x3)ABC(3x3)ABCY".length.toLong())
    check("X(8x2)(3x3)ABCY".decompress(2) == "XABCABCABCABCABCABCY".length.toLong())
    check("(6x1)(1x3)A".decompress() == "(1x3)A".length.toLong())
    check("(6x1)(1x3)A".decompress(2) == "AAA".length.toLong())
    check("(27x12)(20x12)(13x14)(7x10)(1x12)A".decompress(2) == 241_920L)

    val input = readInput("Day${day}").joinToString("").replace("""\s""".toRegex(), "")
    println(part1(input))
    println(part2(input))
}
