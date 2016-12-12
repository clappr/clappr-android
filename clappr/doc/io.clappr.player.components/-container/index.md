[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Container](.)

# Container

`class Container : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/components/Container.kt#L13)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Container(loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md)`, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [frameLayout](frame-layout.md) | `val frameLayout: FrameLayout` |
| [loader](loader.md) | `val loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md) |
| [options](options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [playback](playback.md) | `var playback: `[`Playback`](../-playback/index.md)`?` |
| [plugins](plugins.md) | `val plugins: List<`[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`>` |
| [viewClass](view-class.md) | `val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [view](../../io.clappr.player.base/-u-i-object/view.md) | `var view: View?` |

### Functions

| Name | Summary |
|---|---|
| [load](load.md) | `fun load(source: String, mimeType: String? = null): Boolean` |
| [render](render.md) | `fun render(): Container` |

### Inherited Functions

| Name | Summary |
|---|---|
| [ensureView](../../io.clappr.player.base/-u-i-object/ensure-view.md) | `fun ensureView(): Unit` |
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
