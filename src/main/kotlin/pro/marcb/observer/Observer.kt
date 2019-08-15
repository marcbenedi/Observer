package pro.marcb.observer

interface Observer<T> {
    fun onUpdate(element: T)
}