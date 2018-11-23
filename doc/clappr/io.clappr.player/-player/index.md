[clappr](../../index.md) / [io.clappr.player](../index.md) / [Player](./index.md)

# Player

`open class Player : `[`Fragment`](https://developer.android.com/reference/android/app/Fragment.html)`, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md)

Main Player class.

Once instantiated it should be [configured](configure.md) and added to a view hierarchy before playback can begin.

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State`<br>Player state |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Player(base: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)` = BaseObject())`<br>Main Player class. |

### Properties

| Name | Summary |
|---|---|
| [core](core.md) | `var core: `[`Core`](../../io.clappr.player.components/-core/index.md)`?` |
| [duration](duration.md) | `val duration: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>Media duration in seconds. |
| [fullscreen](fullscreen.md) | `var fullscreen: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Whether the player is in fullscreen mode |
| [position](position.md) | `val position: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)<br>Media current position in seconds. |
| [state](state.md) | `val state: `[`State`](-state/index.md)<br>Current Player state. |

### Functions

| Name | Summary |
|---|---|
| [configure](configure.md) | `open fun configure(options: `[`Options`](../../io.clappr.player.base/-options/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Configure Player. This configuration must be performed before adding fragment to a view hierarchy. |
| [load](load.md) | `fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, mimeType: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Load a new media. Always make sure that the stop() method was called before invoking this`fun load(source: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onCreateView](on-create-view.md) | `open fun onCreateView(inflater: `[`LayoutInflater`](https://developer.android.com/reference/android/view/LayoutInflater.html)`, container: `[`ViewGroup`](https://developer.android.com/reference/android/view/ViewGroup.html)`?, savedInstanceState: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [onDestroyView](on-destroy-view.md) | `open fun onDestroyView(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onPause](on-pause.md) | `open fun onPause(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [pause](pause.md) | `fun pause(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Pause media playing. Media playback may be resumed. |
| [play](play.md) | `fun play(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Start or resume media playing. |
| [seek](seek.md) | `fun seek(position: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Move current playback position. |
| [stop](stop.md) | `fun stop(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Stop media playing. Media playback is ended. |

### Companion Object Properties

| Name | Summary |
|---|---|
| [containerEventsToListen](container-events-to-listen.md) | `val containerEventsToListen: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [playbackEventsToListen](playback-events-to-listen.md) | `val playbackEventsToListen: `[`MutableSet`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [initialize](initialize.md) | `fun initialize(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Initialize Player for the application. This method need to be called before any Player instantiation. |
