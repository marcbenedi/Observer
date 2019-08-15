package pro.marcb.observer

open class Observable<T>(value: T) {

    private val observers: MutableCollection<Observer<T>> = mutableListOf()

    open var value: T = value
        set(newValue) {
            field = newValue
            notifyNewValue(newValue)
        }

    fun subscribe(observer: Observer<T>) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
        observer.onUpdate(value)
    }

    fun unsubscribe(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun <R> map(map: (T) -> R) = Observable(map(this.value))

    private fun notifyNewValue(newValue: T) {
        observers.forEach {
            it.onUpdate(newValue)
        }
    }
}
