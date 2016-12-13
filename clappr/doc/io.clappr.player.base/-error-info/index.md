[clappr](../../index.md) / [io.clappr.player.base](../index.md) / [ErrorInfo](.)

# ErrorInfo

`data class ErrorInfo : Parcelable` [(source)](https://github.com/clappr/clappr-android/tree/dev/clappr/src/main/kotlin/io/clappr/player/base/ErrorInfo.kt#L18)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ErrorInfo(message: String, code: Int, extras: Bundle? = null)` |

### Properties

| Name | Summary |
|---|---|
| [code](code.md) | `val code: Int` |
| [extras](extras.md) | `val extras: Bundle?` |
| [message](message.md) | `val message: String` |

### Functions

| Name | Summary |
|---|---|
| [describeContents](describe-contents.md) | `fun describeContents(): Int` |
| [writeToParcel](write-to-parcel.md) | `fun writeToParcel(dest: Parcel, flags: Int): Unit` |

### Companion Object Properties

| Name | Summary |
|---|---|
| [CREATOR](-c-r-e-a-t-o-r.md) | `val CREATOR: Creator<ErrorInfo>` |
