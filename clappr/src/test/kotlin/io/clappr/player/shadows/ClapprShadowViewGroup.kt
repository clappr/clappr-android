package io.clappr.player.shadows

import android.view.ViewGroup
import org.robolectric.annotation.Implements

@Implements(ViewGroup::class)
open class ClapprShadowViewGroup : ClapprShadowView()