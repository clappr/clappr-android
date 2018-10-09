## FAQ & Troubleshooting

### How can I disable a plugin?

Let's say you want to disable the plugin `PlayButton`.

``` java
// after creating a Player instance, and before loading a video
Loader.registeredPlugins.remove(PlayButton.name)
```
