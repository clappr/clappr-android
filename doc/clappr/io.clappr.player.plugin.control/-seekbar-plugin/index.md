[clappr](../../index.md) / [io.clappr.player.plugin.control](../index.md) / [SeekbarPlugin](./index.md)

# SeekbarPlugin

`open class SeekbarPlugin : `[`Plugin`](../-media-control/-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SeekbarPlugin(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [backgroundView](background-view.md) | `open val backgroundView: `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [bufferedBar](buffered-bar.md) | `open val bufferedBar: `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [dragging](dragging.md) | `open var dragging: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [panel](panel.md) | `open var panel: `[`Panel`](../-media-control/-plugin/-panel/index.md) |
| [positionBar](position-bar.md) | `open val positionBar: `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [scrubberView](scrubber-view.md) | `open val scrubberView: `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [view](view.md) | `open val view: `[`ViewGroup`](https://developer.android.com/reference/android/view/ViewGroup.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [isEnabled](../-media-control/-plugin/is-enabled.md) | `open val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isPlaybackIdle](../-media-control/-plugin/is-playback-idle.md) | `open val isPlaybackIdle: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [position](../-media-control/-plugin/position.md) | `open var position: `[`Position`](../-media-control/-plugin/-position/index.md) |

### Functions

| Name | Summary |
|---|---|
| [bindEventListeners](bind-event-listeners.md) | `open fun bindEventListeners(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handleStopDrag](handle-stop-drag.md) | `open fun handleStopDrag(position: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handleTouch](handle-touch.md) | `open fun handleTouch(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`, motionEvent: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [recalculatePositionBarWidth](recalculate-position-bar-width.md) | `fun recalculatePositionBarWidth(percentage: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [removeGlobalLayoutListener](remove-global-layout-listener.md) | `fun removeGlobalLayoutListener(listener: `[`OnGlobalLayoutListener`](https://developer.android.com/reference/android/view/ViewTreeObserver/OnGlobalLayoutListener.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [shouldPresentSeekbar](should-present-seekbar.md) | `open fun shouldPresentSeekbar(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [stopDrag](stop-drag.md) | `open fun stopDrag(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updateBuffered](update-buffered.md) | `open fun updateBuffered(bundle: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updateDrag](update-drag.md) | `open fun updateDrag(position: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updatePosition](update-position.md) | `open fun updatePosition(bundle: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun updatePosition(percentage: `[`Double`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)`, dragEvent: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updatePositionOnResize](update-position-on-resize.md) | `open fun updatePositionOnResize(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
