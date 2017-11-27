package com.starunion.collapselib;

import android.view.View;

/**
 * Discription:
 * Created by sz on 17/11/27.
 */

public class CollapseByGapStragety implements CollapseStragety {
    @Override
    public int getAnchorPosition(int targetWidth, int anchorWidth,  int totalCount, int pos, View targetView) {
        int resultWidth = anchorWidth * (totalCount-pos-1);
        return resultWidth;
    }

    @Override
    public int getCollapseBtnPosition(int targetWidth, int anchorWidth, int totalCount, int margin, View targetView) {
        return anchorWidth * (totalCount-1) + targetView.getWidth() + margin;
    }
}
