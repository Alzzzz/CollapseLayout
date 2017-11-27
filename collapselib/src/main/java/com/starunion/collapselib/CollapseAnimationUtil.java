package com.starunion.collapselib;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.view.View;

/**
 * Discription:
 * Created by sz on 17/11/26.
 */

public class CollapseAnimationUtil {

    /**
     * @param target
     * @param star         动画起始坐标
     * @param end          动画终止坐标
     * @param duration     持续时间
     * @return
     * 创建一个从左到右的飞入动画
     * 礼物飞入动画
     */
    public  static ObjectAnimator createFlyFromLtoR(final View target, float star, float end, int duration, TimeInterpolator interpolator) {
        //1.个人信息先飞出来
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(target, "translationX",
                star, end);
        anim1.setInterpolator(interpolator);
        anim1.setDuration(duration);
        return  anim1;
    }
}
