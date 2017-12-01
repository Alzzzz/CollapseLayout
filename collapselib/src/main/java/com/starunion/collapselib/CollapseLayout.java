package com.starunion.collapselib;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;


/**
 * 重叠头像 布局
 *
 * @author sz
 */
public class CollapseLayout extends ViewGroup {

    /**
     * 重叠宽度
     */
    protected float pileWidth;
    protected float collapseMargin = 0;
    private int collapseCount = 5;
    /**
     * 是否已经开启
     */
    private boolean opened = false;

    /**
     * 默认的大小
     */
    int initWidth = 0;
    /**
     * 拉长的大小
     * =间隔*（总数-1）+ 各个childe的宽
     */
    int targetWidth = 0;

    int hideItemWidth = 0;

    public CollapseLayout(Context context) {
        this(context, null, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapseLayout);
        pileWidth = ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_pileWidth, dp2px(10));
        collapseMargin =  ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_collapse_margin, dp2px(10));
        collapseCount = ta.getInteger(R.styleable.CollapseLayout_CollapseLayout_collapse_count, 5);
        ta.recycle();

    }

    /**
     * 是否开启
     * @return
     */
    public boolean isOpened(){
        return opened;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //AT_MOST
        int width = 0;
        int height = 0;
        int rawWidth = 0;//当前行总宽度
        int rawHeight = 0;// 当前行高

        int rowIndex = 0;//当前行位置
        int tempTargetWidth = 0;
        int tempHideWidth = 0;

        //获取需要拉伸后的宽度
        tempTargetWidth = (int) collapseMargin*(getChildCount()-1);
        for (int i = getChildCount()-1; i >-1; i--) { // 往右重叠
            View child = getChildAt(i);
            if(child.getVisibility() == GONE){
                if(i == 0){
                    // 往右重叠
                    height += rawHeight;
                    width = Math.max(width, rawWidth);
                }
                continue;
            }

            //这里调用measureChildWithMargins 而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth()  + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (getChildCount() - i <= collapseCount){
                rawWidth += childWidth;
                if(rowIndex > 0){
                    rawWidth -= pileWidth;
                }
            } else {
                tempHideWidth += child.getMeasuredWidth() - pileWidth;
            }


            rawHeight = Math.max(rawHeight, childHeight);

            if(i == 0){
                // 往右重叠
                width = Math.max(rawWidth, width);
                height += rawHeight;
            }

            rowIndex++;
            tempTargetWidth = tempTargetWidth+child.getMeasuredWidth();
        }
        if (initWidth == 0){
            initWidth = widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight();
        }

        setMeasuredDimension(
                widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight(),
                heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : height + getPaddingTop() + getPaddingBottom()
        );

        if (targetWidth < tempTargetWidth){
            targetWidth = tempTargetWidth;
        }

        if (hideItemWidth < tempHideWidth){
            hideItemWidth = tempHideWidth;
        }

        Log.d("CollapseLayout", "onMeasure targetWidth = "+targetWidth);
        if (getMeasuredWidth() == initWidth){
//            opened = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = r - l;
        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rowMaxHeight = 0;
        int rowIndex = 0;//当前行位置
        View childView;
        int childCount = getChildCount();
        if (childCount > collapseCount){
            childCount = collapseCount;
        }

        int distributableWidth = getMeasuredWidth() - initWidth;
        if (initWidth < getMeasuredWidth()){
            distributableWidth -= hideItemWidth;
        }

        for( int w = getChildCount()-1, count = getChildCount(); w > -1 ; w-- ){ // 往右重叠

            childView = getChildAt(w);
            childView.setVisibility(VISIBLE);
            if (distributableWidth <= 0){
                distributableWidth = 0;
                if (w < getChildCount() - childCount) {
                    childView.setVisibility(GONE);
                }
            }
            if(childView.getVisibility() == GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            // 如果加上当前子View的宽度后超过了ViewGroup的宽度，就换行
            int occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin;

            int left = leftOffset + lp.leftMargin;
            int top = topOffset + lp.topMargin;
            int right = leftOffset+ lp.leftMargin + childView.getMeasuredWidth();
            int bottom =  topOffset + lp.topMargin + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);

            // 横向偏移
            leftOffset += occupyWidth;
            // 试图更新本行最高View的高度
            int occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if(rowIndex != count - 1){

                leftOffset = (int) (leftOffset - pileWidth + (distributableWidth>0? distributableWidth:0)/(getChildCount()-1));
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight);
            rowIndex++;
        }
    }

    public void open(){
        if (!opened){
            Log.d("CollapseLayout", "onMeasure targetWidth = "+targetWidth);
            Animator animator = CollapseAnimationUtil.changeLayoutWidth(this, initWidth, targetWidth, 200);
            animator.start();
            opened = true;
        }
    }

    public void close(){
        if (opened){
            Log.d("CollapseLayout", "onMeasure targetWidth = "+targetWidth);
            Animator animator = CollapseAnimationUtil.changeLayoutWidth(this, targetWidth, initWidth, 200);
            animator.start();
            opened = false;
        }
    }
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
