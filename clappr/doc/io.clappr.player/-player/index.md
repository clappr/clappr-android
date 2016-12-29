[clappr](../../index.md) / [io.clappr.player](../index.md) / [Player](.)

# Player

`open class Player : Fragment, `[`EventInterface`](../../io.clappr.player.base/-event-interface/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/Player.kt#L22)

Main Player class.

Once instantiated it should be [configured](configure.md) and added to a view hierarchy before playback can begin.

### Types

| Name | Summary |
|---|---|
| [State](-state/index.md) | `enum class State : Enum<`[`State`](-state/index.md)`>`<br>Player state |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Player(base: `[`BaseObject`](../../io.clappr.player.base/-base-object/index.md)` = BaseObject())`<br>Main Player class. |

### Properties

| Name | Summary |
|---|---|
| [duration](duration.md) | `val duration: Double`<br>Media duration in seconds. |
| [fullscreen](fullscreen.md) | `var fullscreen: Boolean`<br>Whether the player is in fullscreen mode |
| [position](position.md) | `val position: Double`<br>Media current position in seconds. |
| [state](state.md) | `val state: `[`State`](-state/index.md)<br>Current Player state. |

### Functions

| Name | Summary |
|---|---|
| [configure](configure.md) | `fun configure(options: `[`Options`](../../io.clappr.player.base/-options/index.md)`): Unit`<br>Configure Player. This configuration must be performed before adding fragment to a view hierarchy. |
| [load](load.md) | `fun load(source: String, mimeType: String? = null): Boolean`<br>Load a new media`fun load(source: String): Boolean` |
| [onCreateView](on-create-view.md) | `open fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View` |
| [pause](pause.md) | `fun pause(): Boolean`<br>Pause media playing. Media playback may be resumed. |
| [play](play.md) | `fun play(): Boolean`<br>Start or resume media playing. |
| [seek](seek.md) | `fun seek(position: Int): Boolean`<br>Move current playback position. |
| [stop](stop.md) | `fun stop(): Boolean`<br>Stop media playing. Media playback is ended. |

### Companion Object Functions

| Name | Summary |
|---|---|
| [initialize](initialize.md) | `fun initialize(context: Context): Unit`<br>Initialize Player for the application. This method need to be called before any Player instantiation. |
