[clappr](../../index.md) / [io.clappr.player.plugin](../index.md) / [PosterPlugin](.)

# PosterPlugin

`class PosterPlugin : `[`UIContainerPlugin`](../../io.clappr.player.plugin.container/-u-i-container-plugin/index.md)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `PosterPlugin(container: `[`Container`](../../io.clappr.player.components/-container/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [state](state.md) | `var state: `[`State`](../-plugin/-state/index.md) |
| [view](view.md) | `val view: `[`View`](https://developer.android.com/reference/android/view/View.html)`?` |

### Inherited Properties

| Name | Summary |
|---|---|
| [container](../../io.clappr.player.plugin.container/-u-i-container-plugin/container.md) | `val container: `[`Container`](../../io.clappr.player.components/-container/index.md) |

### Functions

| Name | Summary |
|---|---|
| [bindEventListeners](bind-event-listeners.md) | `fun bindEventListeners(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [bindPlaybackListeners](bind-playback-listeners.md) | `fun bindPlaybackListeners(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [updatePoster](update-poster.md) | `fun updatePoster(bundle: `[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [httpClient](http-client.md) | `val httpClient: OkHttpClient` |
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [picasso](picasso.md) | `val picasso: Picasso` |
