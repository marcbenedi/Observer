package pro.marcb.observer

import java.util.function.UnaryOperator

class ObservableList<T>(private val sources: MutableList<Observable<T>> = mutableListOf()) : MutableList<Observable<T>> by sources,
        Observer<T> {

    private val observers = mutableSetOf<CollectionObserver<T>>()

    init {
        sources.forEach { it.subscribe(this) }
    }

    override fun onUpdate(element: T) {
        val index = sources.map { it.value }.indexOf(element)
        observers.notify(index, element)
    }

    fun subscribe(observer: CollectionObserver<T>) {
        observers.add(observer)
    }

    fun unsubscribe(observer: CollectionObserver<T>) {
        observers.remove(observer)
    }

    override fun add(element: Observable<T>): Boolean {
        val added = sources.add(element)
        if (added) {
            element.subscribe(this)
        }
        return added
    }

    override fun add(index: Int, element: Observable<T>) {
        sources.add(index, element)
        element.subscribe(this)
    }

    override fun addAll(index: Int, elements: Collection<Observable<T>>): Boolean {
        val added = sources.addAll(index, elements)
        if (added) {
            elements.forEach { it.subscribe(this) }
        }
        return added
    }

    override fun addAll(elements: Collection<Observable<T>>): Boolean = elements.map { add(it) }.fold(false) { acc, b -> acc || b }

    override fun clear() {
        sources.forEach { element ->
            element.unsubscribe(this)
            observers.notify(-1, element.value)
        }
        sources.clear()
    }

    override fun remove(element: Observable<T>): Boolean {
        val removed = sources.remove(element)
        if (removed) {
            element.unsubscribe(this)
            observers.notify(-1, element.value)
        }
        return removed
    }

    override fun removeAll(elements: Collection<Observable<T>>): Boolean = elements.map { remove(it) }.fold(false) { acc, b -> acc || b }

    override fun removeAt(index: Int): Observable<T> {
        val element = sources.removeAt(index)
        element.unsubscribe(this)
        observers.notify(-1, element.value)
        return element
    }

    override fun replaceAll(operator: UnaryOperator<Observable<T>>) {
        throw NotImplementedError()
    }

    override fun retainAll(elements: Collection<Observable<T>>): Boolean {
        throw  NotImplementedError()
    }

    override fun set(index: Int, element: Observable<T>): Observable<T> {
        val old = sources.set(index, element)
        element.subscribe(this)
        old.unsubscribe(this)
        return old
    }

    private fun Collection<CollectionObserver<T>>.notify(index: Int, element: T) {
        this.forEach {
            it.onUpdate(index, element)
        }
    }
}

