package com.starunion.collapselib;

import android.view.View;

/**
 * Discription:
 *
 * @author sz
 * @date 17/11/27
 */

public interface CollapseStragety {

    /**
     * 获取AnchorWidth锚点位置
     * @param targetWidth   未加缩放图标的大小
     * @param anchorWidth
     * @param totalCount
     * @param pos           当前view的positon 0开始
     * @param targetView
     * @return
     */
    int getAnchorPosition(int targetWidth, int anchorWidth, int totalCount, int pos, View targetView);

    /**
     * 获取缩放图标距离
     * @param targetWidth   加了缩放图标的大小
     * @param anchorWidth
     * @param totalCount
     * @param margin        缩放按钮距离左边的距离
     * @param targetView
     * @return
     */
    int getCollapseBtnPosition(int targetWidth, int anchorWidth, int totalCount, int margin, View targetView);
}
