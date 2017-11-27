package com.starunion.collapselib;

import android.view.View;

/**
 * Discription:
 *
 * @author sz
 * @date 17/11/27
 */

public class CollapseByWidthStragety implements CollapseStragety {
    @Override
    public int getAnchorPosition(int targetWidth, int anchorWidth, int totalCount,  int pos, View targetView) {
        return targetWidth - anchorWidth * pos - targetView.getWidth() - targetView.getLeft();
    }

    @Override
    public int getCollapseBtnPosition(int targetWidth, int anchorWidth, int totalCount, int margin, View targetView) {
        return targetWidth - targetView.getWidth() - targetView.getLeft();
    }
}
