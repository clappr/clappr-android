[clappr](../index.md) / [io.clappr.player.base](.)

## Package io.clappr.player.base

### Types

| Name | Summary |
|---|---|
| [BaseObject](-base-object/index.md) | `open class BaseObject : `[`EventInterface`](-event-interface/index.md) |
| [Callback](-callback/index.md) | `interface Callback : Any` |
| [ErrorCode](-error-code/index.md) | `object ErrorCode : Any` |
| [ErrorInfo](-error-info/index.md) | `data class ErrorInfo : Parcelable` |
| [Event](-event/index.md) | `enum class Event : Enum<`[`Event`](-event/index.md)`>` |
| [EventInterface](-event-interface/index.md) | `interface EventInterface : Any` |
| [InternalEvent](-internal-event/index.md) | `enum class InternalEvent : Enum<`[`InternalEvent`](-internal-event/index.md)`>` |
| [NamedType](-named-type/index.md) | `interface NamedType : Any` |
| [Options](-options/index.md) | `class Options : Map<String, Any>` |
| [UIObject](-u-i-object/index.md) | `open class UIObject : `[`BaseObject`](-base-object/index.md) |
| [Utils](-utils/index.md) | `object Utils : Any` |

### Functions

| Name | Summary |
|---|---|
| [readBundle](read-bundle.md) | `fun readBundle(source: Parcel): Bundle?` |
| [writeBundle](write-bundle.md) | `fun writeBundle(dest: Parcel, value: Bundle?): Unit` |
