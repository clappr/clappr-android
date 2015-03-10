package com.globo.clappr.base

import android.content.Context
import android.view.View
import com.globo.clappr.BaseTest
import com.globo.clappr.components.PlayerInfo
import groovy.transform.CompileStatic
import org.junit.Before
import org.junit.Test
import org.robolectric.annotation.Config

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.assertThat

@CompileStatic
@Config(manifest = Config.NONE)
class UIObjectTest extends BaseTest {

    static class CustomView extends View {
        public CustomView(Context context) {
            super(context)
        }
    }

    CustomView customView

    @Before
    void setUp() {
        super.setUp()
        customView = new CustomView(PlayerInfo.context)
    }

    @Test
    void objectShouldPrioritizeCreatingViewThroughTheRenderMethod() {
        def testObj = new UIObject() {
            @Override
            UIObject render() {
                view = customView
                return this
            }
        }
        assertThat testObj.view, is(customView as View)
    }

    @Test
    void objectShouldCreateDefaultViewIfRenderDoesNotCreateIt() {
        def testObj = new UIObject()
        assertThat testObj.view, not(nullValue())
        assertThat testObj.view, isA(View)
    }

    @Test
    void objectShouldCreateViewWithCustomClassIfRenderDoesNotCreateIt() {
        def testObj = new UIObject() {
            @Override
            Class<CustomView> viewClass() {
                return CustomView
            }
        }
        assertThat testObj.view, is(instanceOf(CustomView))
    }
}
