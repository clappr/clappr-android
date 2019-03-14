[clappr](../../index.md) / [io.clappr.player.playback](../index.md) / [ExoPlayerPlayback](index.md) / [volume](./volume.md)

# volume

`open var volume: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)`?`

Overrides [Playback.volume](../../io.clappr.player.components/-playback/volume.md)

Playback volume. Its not the device volume.
If the playback has this capability. You can set the volume from 0.0f to 1.0f.
Where 0.0f is muted and 1.0f is the playback maximum volume.
PS.: If you set a volume less than 0.0f we'll set the volume to 0.0f
PS.: If you set a volume greater than 1.0f we'll set the volume to 1.0f

