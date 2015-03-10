package com.globo.clappr

import com.globo.clappr.components.PlayerInfo
import groovy.transform.CompileStatic
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@CompileStatic
@RunWith(RobolectricTestRunner)
abstract class BaseTest {
    @Before void setUp() {
        PlayerInfo.setContext(Robolectric.application)
    }

    @After void tearDown() {
    }
}
