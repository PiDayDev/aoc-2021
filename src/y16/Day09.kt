package y16

private const val day = "09"

fun main() {
    fun String.decompress(): StringBuilder {
        var inMarker = false
        var marker = ""
        var markLen = 0
        var markRep = 0
        var data = ""
        val buffer = StringBuilder()
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
                    buffer.append(c)
                }
            } else if (data.length < markLen) {
                data += c
                if (data.length == markLen) {
                    buffer.append(data.repeat(markRep))
                    data = ""
                    markRep = 0
                    markLen = 0
                }
            } else {
                buffer.append(c)
            }
        }
        return buffer
    }

    fun String.decompressedSize() = decompress().length.toLong()

    fun part1(input: String) = input.decompressedSize()

    fun part2(input: String): Int {
        return input.length
    }

    val input = readInput("Day${day}").joinToString("").replace("""\s""".toRegex(), "")
    println(part1(input))
    println(part2(input))
}
