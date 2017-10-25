package bg.devlabs.flyingfab

import android.animation.TimeInterpolator
import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator

@Suppress("unused")
/**
 * Created by Simona Stoyanova on 10/25/17.
 * simona@devlabs.bg
 * Handles AppBarStateChanges and animates the Floating action button
 * according to the position and the State of the AppBarLayout
 *
 * Can be created like FabAnimationHandler fabh = new FabAnimationHandler()
 * Should call setup(appBarLayout: AppBarLayout, fab: FloatingActionButton)
 * in order for the view to be animated
 *
 * @see AppBarStateChangeListener.AppBarState
 */

class FlyingFab {
    private val DEFAULT_TIME_INTERPOLATOR = AccelerateDecelerateInterpolator()
    private val DEFAULT_ANIMATION_DURATION = 800L
    private var interpolator: TimeInterpolator = DEFAULT_TIME_INTERPOLATOR
    private var animationDuration: Long = DEFAULT_ANIMATION_DURATION

    @Suppress("unused", "RedundantVisibilityModifier")
    public fun setup(appBarLayout: AppBarLayout, fab: FloatingActionButton) {
        val mListener = object : AppBarStateChangeListener() {

            override fun moveButtonAccordingToOffset(verticalOffset: Int) {
                val appBarHeight = appBarLayout.height
                val fabHeight = fab.height
                val translateNew = appBarHeight + verticalOffset - fabHeight / 2f
                animateToY(fab, translateNew, 0, interpolator)
            }

            override fun moveButtonTop() {
                val appBarHeight = appBarLayout.height
                val fabHeight = fab.height
                val translateNew = appBarHeight - fabHeight / 2f
                animateToY(fab, translateNew, animationDuration, interpolator)
            }

            override fun moveButtonDown() {
                val context = appBarLayout.context
                val heightPixels = context.resources.displayMetrics.heightPixels
                val sixteenDpToPixel = convertDpToPixel(16f, context)
                val translateNew = heightPixels - fab.height - sixteenDpToPixel
                animateToY(fab, translateNew, animationDuration, interpolator)
            }
        }
        appBarLayout.addOnOffsetChangedListener(mListener)
    }

    /**
     * Sets the interpolator for the underlying animator that animates the requested properties.
     * By default, the animator uses the [FlyingFab.DEFAULT_TIME_INTERPOLATOR] interpolator.
     * Calling this method will cause the declared interpolator to be used instead.
     *
     * @param interpolator The TimeInterpolator to be used for ensuing property animations. A value
     * of <code>null</code> will result in [FlyingFab.DEFAULT_TIME_INTERPOLATOR] interpolation.
     * @return This object, allowing calls to methods in this class to be chained.
     *
     * Known Indirect Subclasses of TimeInterpolator:
     * AccelerateDecelerateInterpolator,AccelerateInterpolator,
     * AnticipateInterpolator,AnticipateOvershootInterpolator,
     * BaseInterpolator,BounceInterpolator,CycleInterpolator,
     * DecelerateInterpolator,FastOutLinearInInterpolator,
     * FastOutSlowInInterpolator,Interpolator,LinearInterpolator,
     * LinearOutSlowInInterpolator,OvershootInterpolator,PathInterpolator
     */
    @Suppress("unused", "RedundantVisibilityModifier")
    public fun interpolator(interpolator: TimeInterpolator?): FlyingFab {
        this.interpolator = interpolator ?: DEFAULT_TIME_INTERPOLATOR
        return this
    }

    /**
     * Sets the duration of the animation in which the button is animated from top to bottom and reverse.
     * By default, the duration is [FlyingFab.DEFAULT_ANIMATION_DURATION].
     * Calling this method will cause the declared duration to be used instead.
     *
     * @param animationDuration The animation duration to be used for ensuing property animations. A value
     * of <code>null</code> will result in [FlyingFab.DEFAULT_ANIMATION_DURATION].
     * @return This object, allowing calls to methods in this class to be chained.
     *
     */
    @Suppress("unused", "RedundantVisibilityModifier")
    public fun animationDuration(animationDuration: Long?): FlyingFab {
        this.animationDuration = animationDuration ?: DEFAULT_ANIMATION_DURATION
        return this
    }

    private fun animateToY(fab: View, translateNew: Float, aDuration: Long, interpolator: TimeInterpolator) {
        fab.animation {
            y(translateNew)
            duration = aDuration
            if (duration > 0) {
                this.interpolator = interpolator
            }
        }
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private inline fun View.animation(func: ViewPropertyAnimator.() -> Unit) {
        this.animate().apply {
            func()
        }
    }
}