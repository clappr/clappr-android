package io.clappr.player.shadows


import com.google.android.exoplayer2.ui.SubtitleView
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowView

@Implements(SubtitleView::class)
 class SubtitleViewShadow : ShadowView() {

 @Implementation
 fun setUserDefaultStyle() {
 }

 @Implementation
 fun setUserDefaultTextSize(){
 }

}