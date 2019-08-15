package pro.marcb.observer

class ObservableSet<T>(initialSources: Set<Observable<T>> = setOf()) : MutableSet<Observable<T>>,
        Observer<T> {

    private val _set: MutableSet<Observable<T>> = initialSources.toMutableSet()
    private val observers = mutableSetOf<CollectionObserver<T>>()

    init {
        _set.forEach { it.subscribe(this) }
    }

    override fun onUpdate(element: T) {
        val index = _set.map { it.value }.indexOf(element)
        observers.forEach {
            it.onUpdate(index, element)
        }
    }

    fun subscribe(observer: CollectionObserver<T>) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun unsubscribe(observer: CollectionObserver<T>) {
        observers.remove(observer)
    }

    override fun add(element: Observable<T>): Boolean {
        val added = _set.add(element)
        if (added) {
            element.subscribe(this)
        }
        return added
    }

    override fun addAll(elements: Collection<Observable<T>>): Boolean = elements.map { add(it) }.fold(false) { acc, b -> acc || b }

    override fun clear() {
        val oldElements = _set
        _set.clear()
        oldElements.forEach { element ->
            element.unsubscribe(this)
            observers.forEach { it.onUpdate(-1, element.value) }
        }
    }

    override fun iterator(): MutableIterator<Observable<T>> = throw NotImplementedError()

    override fun remove(element: Observable<T>): Boolean {
        val removed = _set.remove(element)
        if (removed) {
            element.unsubscribe(this)
            observers.forEach { it.onUpdate(-1, element.value) }
        }
        return removed
    }

    override fun removeAll(elements: Collection<Observable<T>>): Boolean = elements.map { remove(it) }.fold(false) { acc, b -> acc || b }

    override fun retainAll(elements: Collection<Observable<T>>): Boolean = throw NotImplementedError()

    override val size: Int get() = _set.size
    override fun contains(element: Observable<T>): Boolean = _set.contains(element)
    override fun containsAll(elements: Collection<Observable<T>>): Boolean = _set.containsAll(elements)
    override fun isEmpty(): Boolean = _set.isEmpty()
}