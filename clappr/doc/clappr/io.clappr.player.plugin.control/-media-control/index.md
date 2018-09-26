[clappr](../../index.md) / [io.clappr.player.plugin.control](../index.md) / [MediaControl](./index.md)

# MediaControl

`open class MediaControl : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md)

### Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | `companion object Companion : `[`NamedType`](../../io.clappr.player.base/-named-type/index.md) |
| [Plugin](-plugin/index.md) | `abstract class Plugin : `[`UICorePlugin`](../../io.clappr.player.plugin.core/-u-i-core-plugin/index.md) |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MediaControl(core: `[`Core`](../../io.clappr.player.components/-core/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [backgroundView](background-view.md) | `open val backgroundView: `[`View`](https://developer.android.com/reference/android/view/View.html) |
| [bottomLeftPanel](bottom-left-panel.md) | `open val bottomLeftPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [bottomPanel](bottom-panel.md) | `open val bottomPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [bottomRightPanel](bottom-right-panel.md) | `open val bottomRightPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [centerPanel](center-panel.md) | `open val centerPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [controlPlugins](control-plugins.md) | `open val controlPlugins: `[`MutableList`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-list/index.html)`<`[`Plugin`](-plugin/index.md)`>` |
| [controlsPanel](controls-panel.md) | `open val controlsPanel: `[`RelativeLayout`](https://developer.android.com/reference/android/widget/RelativeLayout.html) |
| [defaultShowTimeout](default-show-timeout.md) | `open val defaultShowTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [foregroundControlsPanel](foreground-controls-panel.md) | `open val foregroundControlsPanel: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |
| [handler](handler.md) | `open val handler: `[`Handler`](https://developer.android.com/reference/android/os/Handler.html) |
| [isEnabled](is-enabled.md) | `val isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isPlaybackIdle](is-playback-idle.md) | `val isPlaybackIdle: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [lastInteractionTime](last-interaction-time.md) | `open var lastInteractionTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [longShowTimeout](long-show-timeout.md) | `open val longShowTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [modalPanel](modal-panel.md) | `open val modalPanel: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |
| [state](state.md) | `open var state: `[`State`](../../io.clappr.player.plugin/-plugin/-state/index.md) |
| [topLeftPanel](top-left-panel.md) | `open val topLeftPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [topPanel](top-panel.md) | `open val topPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [topRightPanel](top-right-panel.md) | `open val topRightPanel: `[`LinearLayout`](https://developer.android.com/reference/android/widget/LinearLayout.html) |
| [view](view.md) | `open val view: `[`FrameLayout`](https://developer.android.com/reference/android/widget/FrameLayout.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [core](../../io.clappr.player.plugin.core/-u-i-core-plugin/core.md) | `val core: `[`Core`](../../io.clappr.player.components/-core/index.md) |

### Functions

| Name | Summary |
|---|---|
| [destroy](destroy.md) | `open fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hide](hide.md) | `open fun hide(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hideDelayed](hide-delayed.md) | `open fun hideDelayed(timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [layoutPlugins](layout-plugins.md) | `open fun layoutPlugins(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.md) | `open fun render(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setupPlugins](setup-plugins.md) | `open fun setupPlugins(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [show](show.md) | `open fun show(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun show(timeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toggleVisibility](toggle-visibility.md) | `open fun toggleVisibility(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updateInteractionTime](update-interaction-time.md) | `open fun updateInteractionTime(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [modalPanelViewKey](modal-panel-view-key.md) | `const val modalPanelViewKey: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
