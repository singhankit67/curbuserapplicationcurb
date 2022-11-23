package com.curb.curbuserapplication

import android.animation.ValueAnimator
import android.os.CountDownTimer
import android.view.animation.LinearInterpolator

object AnimationUtils {

    fun polyLineAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 1500
//        valueAnimator.repeatMode.until(5)
        valueAnimator.repeatCount = 40
        return valueAnimator

    }
//
//    fun cabAnimator(): ValueAnimator {
//        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
//        valueAnimator.duration = 3000
//        valueAnimator.interpolator = LinearInterpolator()
//        return valueAnimator
//    }

}