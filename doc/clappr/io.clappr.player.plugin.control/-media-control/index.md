[clappr](../../index.md) / [io.clappr.player.plugin.control](../index.md) / [MediaControl](./index.md)

# MediaControl

`open class MediaControl : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |
| [MediaControlDoubleTapListener](-media-control-double-tap-listener/index.md) | `inner class MediaControlDoubleTapListener : `[`OnDoubleTapListener`](https://developer.android.com/reference/android/view/GestureDetector/OnDoubleTapListener.html) |
| [MediaControlGestureDetector](-media-control-gesture-detector/index.md) | `class MediaControlGestureDetector : `[`OnGestureListener`](https://developer.android.com/reference/android/view/GestureDetector/OnGestureListener.html) |
| [Plugin](-plugin/index.md) | `abstract class Plugin : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MediaControl(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`, pluginName: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = name)` |

### Properties

| Name | Summary |
|---|---|
| [defaultShowDuration](default-show-duration.md) | `val defaultShowDuration: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [hideAnimationEnded](hide-animation-ended.md) | `var hideAnimationEnded: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [invalidActivationKeys](invalid-activation-keys.md) | `open val invalidActivationKeys: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Key`](../../io.clappr.player.base.keys/-key/index.md)`>` |
| [isEnabled](is-enabled.md) | `val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isVisible](is-visible.md) | `val isVisible: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [longShowDuration](long-show-duration.md) | `val longShowDuration: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [state](state.md) | `open var state: `[`State`](../../io.clappr.player.plugin/-plugin/-state/index.md) |
| [view](view.md) | `open val view: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [base](../../io.clappr.player.plugin.core/-u-i-core-plugin/base.md) | `open val base: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [uiObject](../../io.clappr.player.plugin.core/-u-i-core-plugin/ui-object.md) | `open val uiObject: `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
| [visibility](../../io.clappr.player.plugin.core/-u-i-core-plugin/visibility.md) | `open var visibility: `[`Visibility`](../../io.clappr.player.plugin/-u-i-plugin/-visibility/index.md) |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handleDidPauseEvent](handle-did-pause-event.md) | `open fun handleDidPauseEvent(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hide](hide.md) | `open fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](show.md) | `open fun show(duration: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toggleVisibility](toggle-visibility.md) | `fun toggleVisibility(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updateInteractionTime](update-interaction-time.md) | `fun updateInteractionTime(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [entry](entry.md) | `val entry: `[`Core`](../../io.clappr.player.plugin/-plugin-entry/-core/index.md) |
| [modalPanelViewKey](modal-panel-view-key.md) | `const val modalPanelViewKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
