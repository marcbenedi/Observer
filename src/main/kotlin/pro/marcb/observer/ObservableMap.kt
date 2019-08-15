package pro.marcb.observer

import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.UnaryOperator

class ObservableMap<J, T>(private val sources: MutableMap<J, Observable<T>> = mutableMapOf()) : MutableMap<J, Observable<T>> by sources,
        Observer<T> {

    private val observers = mutableSetOf<CollectionObserver<T>>()

    init {
        sources.forEach { it.value.subscribe(this) }
    }

    override fun onUpdate(element: T) {
        val index = sources.map { it.value.value }.indexOf(element)
        observers.notify(index, element)
    }

    fun subscribe(observer: CollectionObserver<T>) {
        observers.add(observer)
    }

    fun unsubscribe(observer: CollectionObserver<T>) {
        observers.remove(observer)
    }

    private fun Collection<CollectionObserver<T>>.notify(index: Int, element: T) {
        this.forEach {
            it.onUpdate(index, element)
        }
    }

    override fun clear() {
        sources.forEach { element ->
            element.value.unsubscribe(this)
            observers.notify(-1, element.value.value)
        }
        sources.clear()
    }

    override fun compute(key: J, remappingFunction: BiFunction<in J, in Observable<T>?, out Observable<T>?>): Observable<T>? {
        throw NotImplementedError()
    }

    override fun computeIfAbsent(key: J, mappingFunction: Function<in J, out Observable<T>>): Observable<T> {
        throw NotImplementedError()
    }

    override fun computeIfPresent(key: J, remappingFunction: BiFunction<in J, in Observable<T>, out Observable<T>?>): Observable<T>? {
        throw NotImplementedError()
    }

    override fun merge(key: J, value: Observable<T>, remappingFunction: BiFunction<in Observable<T>, in Observable<T>, out Observable<T>?>): Observable<T>? {
        throw NotImplementedError()
    }

    override fun put(key: J, value: Observable<T>): Observable<T>? {
        val old = sources.put(key, value)
        val index = sources.map { value }.indexOf(value)
        observers.notify(index, value.value)
        return old
    }

    override fun putAll(from: Map<out J, Observable<T>>) =
            from.forEach {
                put(it.key, it.value)
            }

    override fun putIfAbsent(key: J, value: Observable<T>): Observable<T>? {
        throw NotImplementedError()
    }

    override fun remove(key: J): Observable<T>? {
        val removed = sources.remove(key)
        removed?.let {
            it.unsubscribe(this)
            observers.notify(-1, it.value)
        }
        return removed
    }

    override fun remove(key: J, value: Observable<T>): Boolean {
        throw NotImplementedError()
    }

    override fun replace(key: J, oldValue: Observable<T>, newValue: Observable<T>): Boolean {
        val replaced = sources.replace(key, oldValue, newValue)
        if (replaced) {
            oldValue.unsubscribe(this)
            val index = sources.map { it.value }.indexOf(newValue)
            observers.notify(index, newValue.value)
        }
        return replaced
    }

    override fun replace(key: J, value: Observable<T>): Observable<T>? {
        throw NotImplementedError()
    }

    override fun replaceAll(function: BiFunction<in J, in Observable<T>, out Observable<T>>) {
        throw NotImplementedError()
    }
}

