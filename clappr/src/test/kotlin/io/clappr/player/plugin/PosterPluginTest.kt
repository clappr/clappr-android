package io.clappr.player.plugin

import io.clappr.player.BuildConfig
import io.clappr.player.Player
import io.clappr.player.base.*
import io.clappr.player.components.Container
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class PosterPluginTest {

    @Before
    fun setUp() {
        Player.initialize(ShadowApplication.getInstance().applicationContext)

        Loader.registerPlugin(PosterPlugin::class)
    }

    @After
    fun tearDown() {
        Loader.clearPlugins()
    }

    @Test
    fun shouldUpdateImageUrlWhenUpdateOptionsIsTriggered() {
        val container = Container(Loader(), Options())
        val posterPlugin = container.plugins.filterIsInstance(PosterPlugin::class.java).first()
        val expectedImageUrl = "image_url"

        val option = Options()
        option.put(ClapprOption.POSTER.value, expectedImageUrl)
        container.options = option

        assertEquals(expectedImageUrl, posterPlugin.posterImageUrl)
    }
}