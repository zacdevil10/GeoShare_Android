package uk.co.appsbystudio.geoshare.utils

private const val NON_THIN = "[^iIl1.,']"

fun String.ellipsize(max: Int): String {
    if (textWidth(this) <= max)
        return this

    var end = this.lastIndexOf(' ', max - 3)

    if (end == -1)
        return this.substring(0, max - 3) + "..."

    var newEnd = end
    do {
        end = newEnd
        newEnd = this.indexOf(' ', end + 1)

        if (newEnd == -1)
            newEnd = this.length

    } while (textWidth(this.substring(0, newEnd) + "...") < max)

    return this.substring(0, end) + "..."
}

private fun textWidth(str: String): Int {
    return str.length - str.replace(NON_THIN.toRegex(), "").length / 2
}