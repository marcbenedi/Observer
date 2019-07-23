package pro.marcb.observer

fun main(args: Array<String>) {

    val o1 = Observable(mutableListOf("a", "b", "c", "d"))
    val o2 = Observable(listOf(1, 2, 3, 4, 5))

    data class tmp(val i: Long, val j: String, val k: String)

    val c = ComposedObservable(mapOf("o1" to o1, "o2" to o2) as Map<String, Observable<Any>>) {
        val s = (it["o1"] as Observable<List<String>>).value
        val i = (it["o2"] as Observable<List<Int>>).value

        s.map { a -> "mapped $a" }.zip(i.map { a -> a.toString() }) { t, r ->
            tmp(1, t, r)
        }
    }

    c.subscribe(object : Observer<List<tmp>> {
        override fun onUdate(element: List<tmp>) {
            println(element.toString())
        }
    })

    val x = o1.value
    x[0] = "aa"
    o1.value = x

}