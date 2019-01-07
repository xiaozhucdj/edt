package com.yougy.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class CustomItemDecoration extends RecyclerView.ItemDecoration{

    private Builder mBuilder;
    private CustomItemDecoration (Builder builder) {
        mBuilder = builder;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mBuilder.isOffsets) {
            outRect.left = mBuilder.leftMargin;
            outRect.top = mBuilder.topMargin;
            outRect.right = mBuilder.rightMargin;
            outRect.bottom = mBuilder.bottomMargin;
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }


    public static class Builder {
        private int topMargin , leftMargin, rightMargin, bottomMargin;
        private boolean isOffsets;

        public Builder isOffsets (boolean isOffsets) {
            this.isOffsets = isOffsets;
            return this;
        }
        public Builder setItemMargin (int left , int top, int right, int bottom){
            this.leftMargin = left;
            this.topMargin = top;
            this.rightMargin = right;
            this.bottomMargin = bottom;
            return this;
        }

        public CustomItemDecoration build () {
            return new CustomItemDecoration(this);
        }
    }

}
