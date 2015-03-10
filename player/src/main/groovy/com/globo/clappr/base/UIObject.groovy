package com.globo.clappr.base

import android.view.View
import android.view.ViewManager
import com.globo.clappr.components.PlayerInfo
import groovy.transform.CompileStatic

@CompileStatic
class UIObject extends BaseObject {

    View view

    public UIObject(Map options = [:]) {
        super(options)
        ensureView()
    }

    public Class viewClass() {
        return View.class
    }

    public render() {
        return this
    }

    public remove() {
        (view.getParent() as ViewManager)?.removeView(view)
        return this
    }

    private ensureView() {
        this.render()
        if (!view) {
            view = viewClass().newInstance(PlayerInfo.context) as View
        }
    }
}
