package com.starunion.collapselib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Discription:
 * Created by sz on 17/11/26.
 */

public class CollapseLayout extends ViewGroup {
    public static final String TAG = "CollapseLayout";
    public static final int SHOW_COLLAPSE_HIDE = 0;
    public static final int SHOW_COLLAPSE_SHOW = 1;
    public static final int SHOW_COLLAPSE_HIDE_EXTENDS_SHOW = 2;

    public static final int STYLE_SHOW_BY_ANCHOR_WIDTH = 0;
    public static final int STYLE_SHOW_BY_TARGET_WIDTH = 1;
    /**
     * 两个子控件之间的垂直间隙
     */
    protected float vertivalSpace;

    /**
     * 重叠宽度
     */
    protected float pileWidth;

    private float anchorWidth = -1;
    private int collapseRes;
    /**
     * 状态
     * 0：不展示
     * 1：展示
     * 2：收缩不展示 打开展示
     */
    private int showCollapseStatus;
    /**
     * 是否打开
     */
    boolean opened = false;
    /**
     * 收缩按钮的margin
     */
    private float collapseMargin;

    /**
     * 设置的折叠数
     */
    private int collapseCount;
    /**
     * 设置的总数
     */
    private int totalCount;
    /**
     * 真正child的数量
     */
    int childCount = 0;

    OnItemClickListener mItemClickListener;

    CollapseStragety mCollapseStragety;
    private int collapseStyle;

    private ImageView collapseBtn;
    public CollapseLayout(Context context) {
        this(context, null, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapseLayout);
        vertivalSpace = ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_vertivalSpace, dp2px(4));
        pileWidth = ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_pileWidth, dp2px(10));
        showCollapseStatus = ta.getInt(R.styleable.CollapseLayout_CollapseLayout_collapse_show, SHOW_COLLAPSE_HIDE_EXTENDS_SHOW);
        collapseMargin = ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_collapse_margin, dp2px(4));
        collapseStyle = ta.getInteger(R.styleable.CollapseLayout_CollapseLayout_collapse_style, STYLE_SHOW_BY_ANCHOR_WIDTH);
        anchorWidth = ta.getDimension(R.styleable.CollapseLayout_CollapseLayout_anchor_width, -1);
        collapseCount = ta.getInteger(R.styleable.CollapseLayout_CollapseLayout_collapse_count, 3);
        totalCount = ta.getInteger(R.styleable.CollapseLayout_CollapseLayout_total_count, 5);
        ta.recycle();

        if (collapseStyle == STYLE_SHOW_BY_ANCHOR_WIDTH){
            mCollapseStragety = new CollapseByGapStragety();
        } else {
            mCollapseStragety = new CollapseByWidthStragety();
        }

        initViews();
    }

    /**
     * listener
     *
     * @param listener
     */
    public void setItemClickListener(OnItemClickListener listener){
        mItemClickListener = listener;
    }

    private void initViews() {
        //如果需要展示
        if (showCollapseStatus != 0){
            collapseBtn = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.btn_collapse, this ,false);
            collapseBtn.setImageResource(R.mipmap.ic_launcher_round);
            addView(collapseBtn);
            if (showCollapseStatus == 2){
                collapseBtn.setVisibility(INVISIBLE);
            }
            collapseBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onCollapseClick(view);
                }
            });
        }
    }

    public void setCollapseRes(int res){
        if (collapseBtn != null){
            collapseBtn.setImageResource(res);
        }
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
        int count = getChildCount();
        for (int i = count - 1; i > -1; i--) { // 往右重叠
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                if (i == 0) {   // 往右重叠
                    //最后一个child
                    height += rawHeight;
                    width = Math.max(width, rawWidth);
                }
                continue;
            }

            //这里调用measureChildWithMargins 而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            rawWidth += childWidth;
            if (rowIndex > 0) {
                rawWidth -= pileWidth;
            }
            rawHeight = Math.max(rawHeight, childHeight);

            if (i == 0) {      // 往右重叠
                width = Math.max(rawWidth, width);
                height += rawHeight;
            }

            rowIndex++;
        }

        setMeasuredDimension(
                widthSpecMode == MeasureSpec.EXACTLY ? widthSpecSize : width + getPaddingLeft() + getPaddingRight(),
                heightSpecMode == MeasureSpec.EXACTLY ? heightSpecSize : height + getPaddingTop() + getPaddingBottom()
        );
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = r - l;
        int leftOffset = getPaddingLeft();
        int topOffset = getPaddingTop();
        int rowMaxHeight = 0;
        //当前行位置
        int rowIndex = 0;
        View childView;
        if (showCollapseStatus > 0){
            childCount =  getChildCount() > totalCount+1 ? totalCount+1:getChildCount();
        } else {
            childCount = getChildCount() > totalCount ? totalCount: getChildCount();
        }
        for (int w = childCount - 1, count = getChildCount(); w > -1; w--) { // 往右重叠
            childView = getChildAt(w);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            // 如果加上当前子View的宽度后超过了ViewGroup的宽度，就换行
            int occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin;

            int left = leftOffset + lp.leftMargin;
            int top = topOffset + lp.topMargin;
            int right = leftOffset + lp.leftMargin + childView.getMeasuredWidth();
            int bottom = topOffset + lp.topMargin + childView.getMeasuredHeight();
            childView.layout(left, top, right, bottom);

            // 横向偏移
            leftOffset += occupyWidth;
            // 试图更新本行最高View的高度
            int occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin;
            if (rowIndex != count - 1) {
                leftOffset -= pileWidth;
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight);
            rowIndex++;
            if (rowIndex > collapseCount){
                childView.setVisibility(INVISIBLE);
            }
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

    /**
     * 打开
     */
    public void open(){
        if (!opened){
            toggle();
        }
    }

    /**
     * 关闭
     */
    public void close(){
        if (opened){
            toggle();
        }
    }

    /**
     * 是否展开
     *
     * @return 展开状态
     */
    public boolean isOpened(){
        return opened;
    }

    /**
     * 展开
     */
    public void toggle() {
        toggle(getWidth(), anchorWidth==-1?getDefaultAnchor(getWidth()): (int) anchorWidth);
    }

    /**
     * 展开
     *
     * @param targetWidth 伸缩到的宽度
     */
    public void toggle(int targetWidth) {
        toggle(targetWidth, anchorWidth==-1?getDefaultAnchor(getWidth()): (int) anchorWidth);
    }

    /**
     * 展开
     *
     * @param targetWidth 伸缩到的宽度
     * @param anchor      锚点位置
     */
    public synchronized void toggle(int targetWidth, int anchor) {
        int startPos = 0;
        int endPos = 0;
        int totalCount = childCount;
        //需要缩放按钮
        if (showCollapseStatus > 0){
            totalCount = totalCount - 1;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == null){
                return;
            }
            //如果当前按钮是默认的缩放按钮
            if (view == collapseBtn){
                int translateX = mCollapseStragety.getCollapseBtnPosition(targetWidth, anchor, totalCount, (int) collapseMargin, view);
                if (opened){
                    startPos = translateX;
                    endPos = 0;
                    if (showCollapseStatus != 1){
                        collapseBtn.setVisibility(INVISIBLE);
                    }
                } else {
                    startPos = 0;
                    endPos = translateX;
                    if (showCollapseStatus > 0){
                        collapseBtn.setVisibility(VISIBLE);
                    }
                }
                //动画平移到最左边距离一个view大小的地方
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationX", startPos, endPos);
                anim.setDuration(200);
                anim.start();
            } else {
                showOrhideView(i, view);
                int resultPos = mCollapseStragety.getAnchorPosition((int) (targetWidth-collapseBtn.getWidth()-collapseMargin), anchor, totalCount, i-1, view);
                if (opened){
                    startPos = resultPos;
                    endPos = 0;
                } else {
                    startPos = 0;
                    endPos = resultPos;
                }
                ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationX", startPos, endPos);
                anim.setDuration(200);
                anim.start();
            }
        }
        opened = !opened;
    }

    /**
     * 显示或隐藏
     */
    private void showOrhideView(int pos, View view) {
        int tempPos = pos;
        int totalCount = childCount;
        if (showCollapseStatus > 0){
            tempPos -= 1;
            totalCount -= 1;
        }
        if (opened){
            if (totalCount - tempPos > collapseCount){
                view.setVisibility(INVISIBLE);
            }
        } else {
            if (tempPos < totalCount){
                view.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 获取默认距离
     */
    private int getDefaultAnchor(int targetWidth) {
        int anchor = 0;
        //如果有默认的收缩的图标
        if (showCollapseStatus > 0){
            if (childCount > 1 && getChildAt(childCount - 2) != null){
                //（需要缩放的宽度 - 默认图标的的宽度 - 默认图标距离需要缩放按钮的宽度）/ 元素个数
                anchor = (int) ((targetWidth - getChildAt(childCount-1).getWidth() - collapseMargin - collapseBtn.getWidth()) * 1.0 / (childCount - 2));
            }
        } else {
            if (childCount > 0 && getChildAt(childCount - 1) != null){
                anchor = (int) ((targetWidth - getChildAt(childCount-1).getWidth()) * 1.0 / (childCount - 1));
            }
        }

        return anchor;
    }

    public interface OnItemClickListener{
        /**
         * 点击收缩按钮
         * @param view
         */
        void onCollapseClick(View view);
    }
}
