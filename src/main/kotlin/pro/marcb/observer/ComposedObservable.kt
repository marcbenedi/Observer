package pro.marcb.observer

class ComposedObservable<R>(private val sources: Map<String, Observable<Any>>,
                            private val build: (Map<String, Observable<Any>>) -> R)
    : Observable<R>(build(sources)), Observer<Any> {

    init {
        sources.values.forEach {
            it.subscribe(this)
        }
    }

    override fun onUpdate(element: Any) {
        value = build(sources)
    }
}