package pro.marcb.observer

interface Observer<T> {
    fun onUdate(element: T)
}
