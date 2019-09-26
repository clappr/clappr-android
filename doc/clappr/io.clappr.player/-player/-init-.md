[clappr](../../index.md) / [io.clappr.player](../index.md) / [Player](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`Player(base: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)` = BaseObject(), coreEventsToListen: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableSetOf(), playbackEventsToListen: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableSetOf(), containerEventsToListen: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = mutableSetOf())`

Main Player class.

Once instantiated it should be [configured](configure.md) and added to a view hierarchy before playback can begin.

