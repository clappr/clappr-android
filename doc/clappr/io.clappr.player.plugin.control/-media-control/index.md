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
| [allowedKeysToToggleMediaControlVisibility](allowed-keys-to-toggle-media-control-visibility.md) | `open val allowedKeysToToggleMediaControlVisibility: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Key`](../../io.clappr.player.base.keys/-key/index.md)`>` |
| [hideAnimationEnded](hide-animation-ended.md) | `var hideAnimationEnded: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isEnabled](is-enabled.md) | `val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isVisible](is-visible.md) | `val isVisible: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [keysNotAllowedToIteractWithMediaControl](keys-not-allowed-to-iteract-with-media-control.md) | `open val keysNotAllowedToIteractWithMediaControl: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Key`](../../io.clappr.player.base.keys/-key/index.md)`>` |
| [longShowDuration](long-show-duration.md) | `val longShowDuration: `[`Millisecond`](../-millisecond.md) |
| [modalPanel](modal-panel.md) | `val modalPanel: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |
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
| [animateFadeIn](animate-fade-in.md) | `open fun animateFadeIn(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`, onAnimationEnd: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)` = {}): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [cancelPendingHideDelayed](cancel-pending-hide-delayed.md) | `fun cancelPendingHideDelayed(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [closeModal](close-modal.md) | `open fun closeModal(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handleDidPauseEvent](handle-did-pause-event.md) | `open fun handleDidPauseEvent(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hide](hide.md) | `open fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hideDefaultMediaControlPanels](hide-default-media-control-panels.md) | `open fun hideDefaultMediaControlPanels(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hideDelayedWithCleanHandler](hide-delayed-with-clean-handler.md) | `fun hideDelayedWithCleanHandler(duration: `[`Millisecond`](../-millisecond.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hideModalPanel](hide-modal-panel.md) | `fun hideModalPanel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [modalPanelIsOpen](modal-panel-is-open.md) | `fun modalPanelIsOpen(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [openModal](open-modal.md) | `open fun openModal(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBackground](set-background.md) | `fun setBackground(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, resource: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](show.md) | `open fun show(duration: `[`Millisecond`](../-millisecond.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showDefaultMediaControlPanels](show-default-media-control-panels.md) | `fun showDefaultMediaControlPanels(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [showModalPanel](show-modal-panel.md) | `fun showModalPanel(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toggleVisibility](toggle-visibility.md) | `fun toggleVisibility(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [entry](entry.md) | `val entry: `[`Core`](../../io.clappr.player.plugin/-plugin-entry/-core/index.md) |
| [modalPanelViewKey](modal-panel-view-key.md) | `const val modalPanelViewKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
