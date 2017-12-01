package com.starunion.collapselib;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Discription:
 * Created by sz on 17/11/26.
 */

public class CollapseAnimationUtil {

    /**
     * @param target
     * @param star     动画起始坐标
     * @param end      动画终止坐标
     * @param duration 持续时间
     * @return 创建一个从左到右的飞入动画
     * 礼物飞入动画
     */
    public static ObjectAnimator createFlyFromLtoR(final View target, float star, float end, int duration, TimeInterpolator interpolator) {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(target, "translationX",
                star, end);
        anim1.setInterpolator(interpolator);
        anim1.setDuration(duration);
        return anim1;
    }

    /**
     * @param target
     * @param startWidth    开始大小
     * @param endWidth      动画终止😄
     * @param duration   持续时间
     * @return 创建一个大笑
     */
    public static ValueAnimator changeLayoutWidth(final View target, int startWidth, int endWidth, int duration) {
        ValueAnimator animator = ValueAnimator.ofInt(startWidth, endWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                target.getLayoutParams().width = (Integer) animation.getAnimatedValue();
                target.requestLayout();
            }
        });
        animator.setDuration(duration);
        return animator;
    }
}
