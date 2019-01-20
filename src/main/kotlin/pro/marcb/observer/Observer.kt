package main.kotlin.pro.marcb.observer

open class Observable<T>(value: T) {

    protected val observers: MutableCollection<Observer<T>> = mutableListOf()

    open var value: T = value
        set(newValue) {
            field = newValue
            observers.forEach {
                it.onUpdate(newValue)
            }
        }

    fun subscribe(observer: Observer<T>) {
        if (!observers.contains(observer)) { observers.add(observer) }
        observer.onUpdate(value)
    }

    fun unsubscribe(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun <R> map(map: (T) -> R) = Observable(map(this.value))
}

class Observer<T> constructor(val onUpdate: (T) -> Unit)

class CraftedObservable<R>(sources: Map<String, Observable<Any>>, build: (Map<String, Observable<Any>>) -> R)
    : Observable<R>(build(sources)) {

    init {
        val observer = Observer<Any> {
            value = build(sources)
        }
        sources.values.forEach {
            it.subscribe(observer)
        }
    }
}

fun main(args: Array<String>) {

    val o1 = Observable(mutableListOf("a", "b", "c", "d"))
    val o2 = Observable(listOf(1, 2, 3, 4, 5))

    data class tmp(val i : Long, val j : String, val k: String)

    val c = CraftedObservable(mapOf("o1" to o1, "o2" to o2) as Map<String, Observable<Any>>){
        val s = (it["o1"] as Observable<List<String>>).value
        val i = (it["o2"] as Observable<List<Int>>).value

        s.map { a -> "mapped $a" }.zip(i.map { a -> a.toString() }){t, r ->
            tmp(1, t, r)
        }
    }

    c.subscribe(Observer {
        println(it.toString())
    })

    val x = o1.value
    x[0] = "aa"
    o1.value = x
}