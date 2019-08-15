package pro.marcb.observer

interface CollectionObserver<T> {
    fun onUpdate(index: Int, element: T)
}