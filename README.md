[![Build Status](https://travis-ci.org/clappr/clappr-android.svg?branch=master)](https://travis-ci.org/clappr/clappr-android)
[![Coverage Status](https://coveralls.io/repos/clappr/clappr-android/badge.svg?branch=master)](https://coveralls.io/r/clappr/clappr-android?branch=master)
[![License](https://img.shields.io/badge/license-BSD--3--Clause-blue.svg)](https://img.shields.io/badge/license-BSD--3--Clause-blue.svg)

<div align=center>
<img src="https://cloud.githubusercontent.com/assets/244265/6373134/a845eb50-bce7-11e4-80f2-592ba29972ab.png"><br><br>
</div>

# Clappr for Android - Beta Version

:exclamation: **Clappr 0.7.0 Beta is finally available! If you know the Alpha version, now we have a media control that allows interaction with the player. Come and play..** :exclamation:

## Using the Player

### Dependencies

Clappr is a Kotlin library. If your app is too, be sure Kotlin version is `1.2.51` or bigger, and Android Support version is `27.1.1` or bigger.

The minimal API level supported is `16` `(4.1)`.

It is possible to incorporate clappr into your project in two ways:

#### Local Reference

After cloning Clappr project, add following lines to `dependencies` in `project/build.gradle`:
```
dependencies {
    ...
    implementation project(':clappr')
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.2.51"
    ...
}
```

#### Remote Reference

It is possible to reference any release from Clappr on JCenter. 
Add following lines to `repositories` sections in `build.gradle`:
```
buildscript {
    repositories {
        jcenter()
        ...
    }
    ...
}

allprojects {
    repositories {
        jcenter()
        ...
    }
}
```

Following, add following lines to `dependencies` section in `project/build.gradle`:
```
dependencies {
    ...
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.2.51"
    implementation "org.jetbrains.kotlin:kotlin-reflect:1.2.51"
    implementation "com.android.support:appcompat-v7:27.1.1"
    implementation "com.android.support:support-v4:27.1.1"
    implementation 'com.google.android.exoplayer:exoplayer:2.8.2'

    implementation "io.clappr.player:clappr:0.7.0"
    ...
}
```

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

[Options](doc/clappr/io.clappr.player.base/-options/index.md) class presents the main options (source and mime-type) followed by a hashmap of optional parameters. An `Options` is the only parameter to `Player` `configure` method. Player *optional* parameters are described by [ClapprOption class](doc/clappr/io.clappr.player.base/-clappr-option/index.md).

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

Generic error codes (`UNKNOWN_ERROR` and `PLAYBACK_ERROR`) are also provided in [ErrorCode class](doc/clappr/io.clappr.player.base/-error-code/index.md):

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
