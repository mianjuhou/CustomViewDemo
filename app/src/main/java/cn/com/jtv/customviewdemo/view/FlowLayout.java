package cn.com.jtv.customviewdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangdean on 2016/3/4.
 */
public class FlowLayout extends ViewGroup {

    private int widthMode;
    private int heightMode;

    private boolean once = true;
    private int widthSize;
    private int heightSize;
    private int measuredWidth;
    private int measuredHeight;

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (once) {
            once = false;
            widthMode = MeasureSpec.getMode(widthMeasureSpec);
            heightMode = MeasureSpec.getMode(heightMeasureSpec);
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
            heightSize = MeasureSpec.getSize(heightMeasureSpec);

            measuredWidth = 0;
            measuredHeight = 0;
            if (MeasureSpec.AT_MOST == widthMode && MeasureSpec.AT_MOST == heightMode) {
                //宽度为最宽子View的宽度，高度为每个子View高度的叠加
                int maxWidth = 0;
                int allChildHeight = 0;
                int cCout = getChildCount();
                for (int i = 0; i < cCout; i++) {
                    View childView = getChildAt(i);
                    measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    //处理宽度
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    maxWidth = Math.max(maxWidth, childWidth);
                    //处理高度
                    int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    allChildHeight += childHeight;
                }
                measuredWidth = maxWidth;
                measuredHeight = allChildHeight;
            } else if (MeasureSpec.EXACTLY == widthMode && MeasureSpec.EXACTLY == heightMode) {
                measuredHeight = widthSize;
                measuredHeight = heightSize;
            } else if (MeasureSpec.AT_MOST == widthMode && MeasureSpec.EXACTLY == heightMode) {
                //宽度为最宽子View的宽度
                int maxWidth = 0;
                int cCout = getChildCount();
                for (int i = 0; i < cCout; i++) {
                    View childView = getChildAt(i);
                    measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    maxWidth = Math.max(maxWidth, childWidth);
                }
                measuredWidth = maxWidth;
                measuredHeight = heightSize;
            } else if (MeasureSpec.EXACTLY == widthMode && MeasureSpec.AT_MOST == heightMode) {
                //最常用的情况，宽度叠加超出父View宽度时换行，高度为行高的叠加，行高为行中最高子View的高度
                //计算行数，记录每行的结束位置
                List<Integer> lineEndIndex = new ArrayList<Integer>();
                int lineWidth = 0;
                int cCout = getChildCount();
                for (int i = 0; i < cCout; i++) {
                    View childView = getChildAt(i);
                    measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    //当累加行宽+当前子View的宽度>父View的宽度时换行
                    if (lineWidth + childWidth > widthSize) {
                        lineEndIndex.add(i - 1);
                        lineWidth = 0;
                    } else {//否则累加当前子View的宽度
                        lineWidth += childWidth;
                    }
                    if (i == cCout - 1) {
                        lineEndIndex.add(i);
                    }
                }

                int allLineHeight = 0;
                int preIndex = -1;
                for (int i = 0; i < lineEndIndex.size(); i++) {
                    int maxChildHeight = 0;
                    for (int j = preIndex + 1; j <= lineEndIndex.get(i); j++) {
                        //从每一行的起始位置索引到结束位置索引进行遍历
                        //目的得到行内最高控件高度，设为行高
                        View childView = getChildAt(j);
                        measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                        MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                        int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                        maxChildHeight = Math.max(maxChildHeight, childHeight);
                    }
                    allLineHeight += maxChildHeight;
                }
                measuredHeight = allLineHeight;
                measuredWidth = widthSize;
            }
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else {
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            if (MeasureSpec.AT_MOST == widthMode && MeasureSpec.AT_MOST == heightMode) {
                int lineStartHeight = 0;
                int cCount = getChildCount();
                for (int i = 0; i < cCount; i++) {
                    View childView = getChildAt(i);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    childView.getMeasuredHeight();

                    int lc = lp.leftMargin;
                    int tc = lineStartHeight + lp.topMargin;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    childView.layout(lc, tc, rc, bc);

                    lineStartHeight += lp.topMargin + lp.bottomMargin + childView.getMeasuredHeight();
                }
            } else if (MeasureSpec.EXACTLY == widthMode && MeasureSpec.EXACTLY == heightMode) {
                List<Integer> lineEndIndex = new ArrayList<Integer>();
                int lineWidth = 0;
                int cCout = getChildCount();
                for (int i = 0; i < cCout; i++) {
                    View childView = getChildAt(i);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    //当累加行宽+当前子View的宽度>父View的宽度时换行
                    if (lineWidth + childWidth > getMeasuredWidth()) {
                        lineEndIndex.add(i - 1);
                        lineWidth = 0;
                    } else {//否则累加当前子View的宽度
                        lineWidth += childWidth;
                    }
                    if (i == cCout) {
                        lineEndIndex.add(i);
                    }
                }
                //计算行高
                List<Integer> lineHeightList = new ArrayList<Integer>();
                int preIndex = -1;
                for (int i = 0; i < lineEndIndex.size(); i++) {
                    int maxChildHeight = 0;
                    for (int j = preIndex + 1; j <= lineEndIndex.get(i); j++) {
                        //从每一行的起始位置索引到结束位置索引进行遍历
                        //目的得到行内最高控件高度，设为行高
                        View childView = getChildAt(j);
                        MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                        int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                        maxChildHeight = Math.max(maxChildHeight, childHeight);
                    }
                    lineHeightList.add(maxChildHeight);
                }


                int lineStartHeight = 0;
                preIndex = -1;
                for (int i = 0; i < lineEndIndex.size(); i++) {
                    int lineStartWidth = 0;
                    for (int j = preIndex + 1; j <= lineEndIndex.get(i); j++) {
                        //从每一行的起始位置索引到结束位置索引进行遍历
                        //目的得到行内最高控件高度，设为行高
                        View childView = getChildAt(j);
                        MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

                        int lc = lineStartWidth + lp.leftMargin;
                        int tc = lineStartHeight + lp.topMargin;
                        int rc = lc + childView.getMeasuredWidth();
                        int bc = tc + childView.getMeasuredHeight();
                        childView.layout(lc, tc, rc, bc);

                        lineStartWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    }
                    lineStartHeight += lineHeightList.get(i);
                }
            } else if (MeasureSpec.AT_MOST == widthMode && MeasureSpec.EXACTLY == heightMode) {
                int lineStartHeight = 0;
                int cCount = getChildCount();
                for (int i = 0; i < cCount; i++) {
                    View childView = getChildAt(i);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    childView.getMeasuredHeight();

                    int lc = lp.leftMargin;
                    int tc = lineStartHeight + lp.topMargin;
                    int rc = lc + childView.getMeasuredWidth();
                    int bc = tc + childView.getMeasuredHeight();

                    childView.layout(lc, tc, rc, bc);

                    lineStartHeight = lp.topMargin + lp.bottomMargin + childView.getMeasuredHeight();
                }
            } else if (MeasureSpec.EXACTLY == widthMode && MeasureSpec.AT_MOST == heightMode) {
                List<Integer> lineEndIndex = new ArrayList<Integer>();
                int lineWidth = 0;
                int cCout = getChildCount();
                for (int i = 0; i < cCout; i++) {
                    View childView = getChildAt(i);
                    MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                    int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    //当累加行宽+当前子View的宽度>父View的宽度时换行
                    if (lineWidth + childWidth > getMeasuredWidth()) {
                        lineEndIndex.add(i - 1);
                        lineWidth = 0;
                    } else {//否则累加当前子View的宽度
                        lineWidth += childWidth;
                    }
                    if (i == cCout - 1) {
                        lineEndIndex.add(i);
                    }
                }
                //计算行高
                List<Integer> lineHeightList = new ArrayList<Integer>();
                int preIndex = -1;
                for (int i = 0; i < lineEndIndex.size(); i++) {
                    int maxChildHeight = 0;
                    for (int j = preIndex + 1; j <= lineEndIndex.get(i); j++) {
                        //从每一行的起始位置索引到结束位置索引进行遍历
                        //目的得到行内最高控件高度，设为行高
                        View childView = getChildAt(j);
                        MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                        int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                        maxChildHeight = Math.max(maxChildHeight, childHeight);
                    }
                    preIndex = lineEndIndex.get(i);
                    lineHeightList.add(maxChildHeight);
                }

                int lineStartHeight = 0;
                preIndex = -1;
                for (int i = 0; i < lineEndIndex.size(); i++) {
                    int lineStartWidth = 0;
                    for (int j = preIndex + 1; j <= lineEndIndex.get(i); j++) {
                        //从每一行的起始位置索引到结束位置索引进行遍历
                        //目的得到行内最高控件高度，设为行高
                        View childView = getChildAt(j);
                        MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

                        int lc = lineStartWidth + lp.leftMargin;
                        int tc = lineStartHeight + lp.topMargin;
                        int rc = lc + childView.getMeasuredWidth();
                        int bc = tc + childView.getMeasuredHeight();
                        childView.layout(lc, tc, rc, bc);

                        lineStartWidth += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    }
                    preIndex = lineEndIndex.get(i);
                    lineStartHeight += lineHeightList.get(i);
                }
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
