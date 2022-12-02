fun <T> Sequence<T>.delimited(predicate: (T, T) -> Boolean): Sequence<List<T>> {
    val underlying = this
    return sequence {
        val buffer = mutableListOf<T>()
        var last: T? = null
        for (current in underlying) {
            val shouldSplit = last?.let { predicate(it, current) } ?: false
            if (shouldSplit) {
                yield(buffer.toList())
                buffer.clear()
            }
            buffer.add(current)
            last = current
        }
        if (buffer.isNotEmpty()) {
            yield(buffer)
        }
    }
}
