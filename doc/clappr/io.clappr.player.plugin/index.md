[clappr](../index.md) / [io.clappr.player.plugin](./index.md)

## Package io.clappr.player.plugin

### Types

| Name | Summary |
|---|---|
| [Loader](-loader/index.md) | `object Loader` |
| [LoadingPlugin](-loading-plugin/index.md) | `class LoadingPlugin : `[`UIContainerPlugin`](../io.clappr.player.plugin.container/-u-i-container-plugin/index.md) |
| [PlaybackConfig](-playback-config/index.md) | `object PlaybackConfig` |
| [Plugin](-plugin/index.md) | `interface Plugin : `[`EventInterface`](../io.clappr.player.base/-event-interface/index.md)`, `[`NamedType`](../io.clappr.player.base/-named-type/index.md) |
| [PluginConfig](-plugin-config/index.md) | `object PluginConfig` |
| [PluginEntry](-plugin-entry/index.md) | `sealed class PluginEntry` |
| [PosterPlugin](-poster-plugin/index.md) | `class PosterPlugin : `[`UIContainerPlugin`](../io.clappr.player.plugin.container/-u-i-container-plugin/index.md) |
| [UIPlugin](-u-i-plugin/index.md) | `interface UIPlugin : `[`Plugin`](-plugin/index.md) |

### Type Aliases

| Name | Summary |
|---|---|
| [ContainerPluginFactory](-container-plugin-factory.md) | `typealias ContainerPluginFactory = `[`PluginFactory`](-plugin-factory.md)`<`[`Container`](../io.clappr.player.components/-container/index.md)`, `[`ContainerPlugin`](../io.clappr.player.plugin.container/-container-plugin/index.md)`>` |
| [CorePluginFactory](-core-plugin-factory.md) | `typealias CorePluginFactory = `[`PluginFactory`](-plugin-factory.md)`<`[`Core`](../io.clappr.player.components/-core/index.md)`, `[`CorePlugin`](../io.clappr.player.plugin.core/-core-plugin/index.md)`>` |
| [PluginFactory](-plugin-factory.md) | `typealias PluginFactory<Context, Plugin> = (`[`Context`](-plugin-factory.md#Context)`) -> `[`Plugin`](-plugin-factory.md#Plugin) |
