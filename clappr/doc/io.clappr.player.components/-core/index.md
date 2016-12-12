[clappr](../../index.md) / [io.clappr.player.components](../index.md) / [Core](.)

# Core

`class Core : `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/components/Core.kt#L13)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Core(loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md)`, options: `[`Options`](../../io.clappr.player.base/-options/index.md)`)` |

### Properties

| Name | Summary |
|---|---|
| [activeContainer](active-container.md) | `var activeContainer: `[`Container`](../-container/index.md)`?` |
| [activePlayback](active-playback.md) | `val activePlayback: `[`Playback`](../-playback/index.md)`?` |
| [containers](containers.md) | `val containers: MutableList<`[`Container`](../-container/index.md)`>` |
| [frameLayout](frame-layout.md) | `val frameLayout: FrameLayout` |
| [loader](loader.md) | `val loader: `[`Loader`](../../io.clappr.player.plugin/-loader/index.md) |
| [options](options.md) | `val options: `[`Options`](../../io.clappr.player.base/-options/index.md) |
| [plugins](plugins.md) | `val plugins: List<`[`Plugin`](../../io.clappr.player.plugin/-plugin/index.md)`>` |
| [viewClass](view-class.md) | `val viewClass: `[`Class`](http://docs.oracle.com/javase/6/docs/api/java/lang/Class.html)`<*>` |

### Inherited Properties

| Name | Summary |
|---|---|
| [view](../../io.clappr.player.base/-u-i-object/view.md) | `var view: View?` |

### Functions

| Name | Summary |
|---|---|
| [render](render.md) | `fun render(): Core` |

### Inherited Functions

| Name | Summary |
|---|---|
| [ensureView](../../io.clappr.player.base/-u-i-object/ensure-view.md) | `fun ensureView(): Unit` |
| [remove](../../io.clappr.player.base/-u-i-object/remove.md) | `fun remove(): `[`UIObject`](../../io.clappr.player.base/-u-i-object/index.md) |
