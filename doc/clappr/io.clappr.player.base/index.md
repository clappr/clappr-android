[clappr](../index.md) / [io.clappr.player.base](./index.md)

## Package io.clappr.player.base

### Types

| Name | Summary |
|---|---|
| [BaseObject](-base-object/index.md) | `open class BaseObject : `[`EventInterface`](-event-interface/index.md) |
| [ClapprOption](-clappr-option/index.md) | `enum class ClapprOption` |
| [ErrorCode](-error-code/index.md) | `object ErrorCode` |
| [ErrorInfo](-error-info/index.md) | `data class ErrorInfo : `[`Parcelable`](https://developer.android.com/reference/android/os/Parcelable.html) |
| [ErrorInfoData](-error-info-data/index.md) | `enum class ErrorInfoData`<br>Event bundle data keys for error info |
| [Event](-event/index.md) | `enum class Event` |
| [EventData](-event-data/index.md) | `enum class EventData`<br>Event bundle data keys for selected Events |
| [EventInterface](-event-interface/index.md) | `interface EventInterface` |
| [InternalEvent](-internal-event/index.md) | `enum class InternalEvent` |
| [NamedType](-named-type/index.md) | `interface NamedType` |
| [Options](-options/index.md) | `class Options : `[`MutableMap`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |
| [UIObject](-u-i-object/index.md) | `open class UIObject : `[`BaseObject`](-base-object/index.md) |
| [Utils](-utils/index.md) | `object Utils` |

### Type Aliases

| Name | Summary |
|---|---|
| [EventHandler](-event-handler.md) | `typealias EventHandler = (`[`Bundle`](https://developer.android.com/reference/android/os/Bundle.html)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
