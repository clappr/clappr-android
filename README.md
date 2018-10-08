[![Build Status](https://travis-ci.org/clappr/clappr-android.svg?branch=master)](https://travis-ci.org/clappr/clappr-android)
[![Coverage Status](https://coveralls.io/repos/clappr/clappr-android/badge.svg?branch=master)](https://coveralls.io/r/clappr/clappr-android?branch=master)
[![License](https://img.shields.io/badge/license-BSD--3--Clause-blue.svg)](https://img.shields.io/badge/license-BSD--3--Clause-blue.svg)

<div align=center>
<img src="https://cloud.githubusercontent.com/assets/244265/6373134/a845eb50-bce7-11e4-80f2-592ba29972ab.png"><br><br>
</div>

# Clappr for Android

## Using de Player

### Dependencies
After cloning Clappr project, add Kotlin dependency to our project gradle. Add following lines to `dependencies` in `project/build.gradle`:
```
dependencies {
    ...
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation project(':clappr')
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    ...
}
```

Be sure Kotlin version is `1.2.51` or bigger, and Android Support version is `27.1.1` or bigger.

The minimal API level supported is `16` `(4.1)`.

### Initialization

Before instantiating a `io.clappr.player.Player`, it needs to be initialized with the Application context:

``` java
// Add to onCreate of the Application class
Player.initialize(this)
```
### Configuration

After instantiating, Player needs to be configured before it can be used.

:red_circle: **Attention: Remember to always call the `stop()` method before calling the `load()` again**

#### Event binding

`Player` implements the [EventInterface](doc/clappr/io.clappr.player.base/-event-interface/index.md) interface for event binding. Client application associate any event to an object following the [Callback](doc/clappr/io.clappr.player.base/-callback/index.md) interface.

The `on` or `once` `Player` methods can be used for binding. The only difference is that `once` bindings are only called a single time. These methods return a `String` id that can be used to cancel the binding with the `off` method.

The Player events are described by [Event class](doc/clappr/io.clappr.player.base/-event/index.md) and the data returned by [EventData class](doc/clappr/io.clappr.player.base/-event-data/index.md).

For example, to listen Error events: 
``` java
player.on(Event.ERROR.value, Callback.wrap { bundle: Bundle? ->
            bundle?.getParcelable<ErrorInfo>(Event.ERROR.value)?.let {
                Logger.error("App","Error: ${it.code} ${it.message}", (it.extras?.getSerializable(ErrorInfoData.EXCEPTION.value) as? Exception))
            }
        })
```

#### Options

[Options](doc/clappr/io.clappr.player.base/-options/index.md) class from Clappr presents the main options (source and mime-type) followed by a hashmap of optional parameters. An `Options` is the only parameter to `Player` `configure` method. Clappr *optional* parameters are described by [ClapprOption class](doc/clappr/io.clappr.player.base/-clappr-option/index.md).

Details of [ClapprOption.SELECTED_MEDIA_OPTIONS](doc/clappr/io.clappr.player.base/-clappr-option/-s-e-l-e-c-t-e-d_-m-e-d-i-a_-o-p-t-i-o-n-s.md) are included on section [Media Options](#media-options).

``` java
val optionMap = hashMapOf(ClapprOption.START_AT.value to 50)

player.configure(Options(source = "http://clappr.io/highline.mp4", options = optionMap))
```

### Embed

Player is a `Fragment` and must be embedded in a container view of the activity in order to start playing:

``` java
val fragmentTransaction = fragmentManager.beginTransaction()
fragmentTransaction.add(R.id.player_container, player)
fragmentTransaction.commit()
```

### Error Handling

All errors are reported through the `Event.Error` event. This event includes an `ErrorInfo` in its `Bundle`.
``` java
player.on(Event.ERROR.value, Callback.wrap { bundle: Bundle? ->
            bundle?.getParcelable<ErrorInfo>(Event.ERROR.value)?.let {
                Logger.error("App","Error: ${it.code} ${it.message}", (it.extras?.getSerializable(ErrorInfoData.EXCEPTION.value) as? Exception))
            }
        })
```

Generic error codes (`UNKNOWN_ERROR` and `PLAYBACK_ERROR`) are also provided in [ErrorCode](doc/clappr/io.clappr.player.base/-error-code/index.md):

| Code | Description |
|---|---|
| ErrorCode.AUTHENTICATION | User authentication required. <br/><br/>  `ErrorInfo` extras `Bundle` may include the following information: <br/> *ErrorInfoData.SERVICE_ID* - (Integer) media service id <br/> *ErrorInfoData.AUTHORIZED_RESPONSE* - (String) authorization error JSON response |
| ErrorCode.DEVICE_UNAUTHORIZED | Device authorization required |
| ErrorCode.GEOBLOCK | Media is blocked for this region |
| ErrorCode.SIMULTANEOUS_ACCESS | Number of simultaneous media access exceeded |
| ErrorCode.VIDEO_NOT_FOUND | Media not found |
| ErrorCode.LOCATION_UNAVAILABLE | Media requires location information |
| ErrorCode.GEOFENCING | Media is not available for this location |

The data returned by the errors are described on [ErrorInfoData](doc/clappr/io.clappr.player.base/-error-info-data/index.md).


### Recommendations
#### Fullscreen

Application must handle all fullscreen transitions and behavior.

Player provides two events to indicate that the user has requested fullscreen transition:
``` java
player.on(Event.REQUEST_FULLSCREEN.value, Callback.wrap { enterFullscreen() })
player.on(Event.EXIT_FULLSCREEN.value, Callback.wrap { exitFullscreen() })
```

## [API Documentation](doc/clappr/index.md)

## [FAQ & Troubleshooting](doc/TROUBLESHOOTING.md)

## [Read this before open an issue](doc/BEFORE_OPEN_AN_ISSUE.md)

## [Contributors](https://github.com/clappr/clappr-android/graphs/contributors)

## [Contributing](doc/CONTRIBUTING.md)

## [License](LICENSE)

## Sponsor

[![image](https://cloud.githubusercontent.com/assets/244265/5900100/ef156258-a54b-11e4-9862-7e5851ed9b81.png)](http://globo.com)
