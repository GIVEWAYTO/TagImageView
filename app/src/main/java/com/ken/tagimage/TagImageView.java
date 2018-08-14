package com.ken.tagimage;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: by KEN on 2018/7/14 17.
 * 邮箱: gr201655@163.com
 */

public class TagImageView extends FrameLayout {

    private RelativeLayout mContentLayout;
    private Paint mOvalPaint;
    private float topPadding;
    private int leftPading;
    private int mRadius;
    private int lineLong;
    private View mDelete_tags;
    private TagTextView.TagGestureListener tagClickListener;
    private boolean isClick = false;
    private String mPath;
    /**
     * 标签数据   最后只要重新设置每个标签当前的位置
     * 标签有可能被编辑 所以要获取最后的位置 即可
     * 如果标签是不可移动的属性 则不需要更新
     */
    private List<TagInfoBean> infoBeanList = new ArrayList<>();
    public TagImageView(Context context) {
        this(context, null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_tag, this, true);
        mDelete_tags = findViewById(R.id.delete_tags);
        mDelete_tags.setVisibility(View.GONE);

        mContentLayout = (RelativeLayout) findViewById(R.id.tagsGroup);
        mOvalPaint = new Paint();
        mOvalPaint.setTextSize(Density.sp2px(context, 14));
        mOvalPaint.setStrokeWidth(Density.dip2px(context, 1));
        mOvalPaint.setFilterBitmap(true);

        //滑动
        mContentLayout.setOnTouchListener(onTouchListener);

        //边框与文字顶部底部的距离
        topPadding = Density.dip2px(context, 3);
        leftPading = Density.dip2px(context, 6);
        mRadius = Density.dip2px(context, 4);
        //线长
        lineLong = Density.dip2px(context, 15);

        //标签手势监听
        tagClickListener = new TagTextView.TagGestureListener() {
            @Override
            public void onDown(View view, TagInfoBean bean) {
                if (mDelete_tags.getVisibility() == View.GONE && bean.isCanMove()) {
                    mDelete_tags.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onUp(View view, TagInfoBean bean) {
                if (mDelete_tags.getVisibility() == View.VISIBLE) {
                    mDelete_tags.setVisibility(View.GONE);
                }
            }

            @Override
            public void clickTag(View view, TagInfoBean bean) {
                if(mClickTagListener!=null)
                    mClickTagListener.click(bean);
            }

            @Override
            public void inDeleteRect(View view, TagInfoBean bean) {
                mContentLayout.removeView(view);
                infoBeanList.remove(bean);
                //移除的监听
                if (mDeleteTagListener != null) mDeleteTagListener.remove(mPath, bean);

            }

            @Override
            public void move(View view, TagInfoBean bean) {

            }
        };
    }


    float mLastX;
    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //记录当前点击的时间
                    mLastX = event.getX();
                    isClick = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClick) {
                        double rawX = event.getRawX();
                        double rawY = event.getRawY();

                        mAddTagListener.addTag(mPath, rawX, rawY);
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isClick && Math.abs(event.getX() - mLastX) > 50f) {
                        isClick = false;
                    }
                    return false;
            }

            return true;
        }
    };


    public void addTag(TagInfoBean infoBean) {
        infoBeanList.add(infoBean);
        createTags(infoBean);
    }

    public void setAddTagListener(AddTagListener addTagListener) {
        mAddTagListener = addTagListener;
    }

    public void setDeleteTagListener(DeleteTagListener deleteTagListener) {
        mDeleteTagListener = deleteTagListener;
    }

    /**
     * 添加标签
     */
    private AddTagListener mAddTagListener;
    public interface AddTagListener {
        void addTag(String path, double rawX, double rawY);
    }

    /**
     * 删除标签
     */
    private DeleteTagListener mDeleteTagListener;

    public interface DeleteTagListener {
        void remove(String path, TagInfoBean bean);
    }

    /**
     * 标签被点击
     */
    private ClickTagListener mClickTagListener;

    public void setClickTagListener(ClickTagListener mClickTagListener) {
        this.mClickTagListener = mClickTagListener;
    }

    public interface ClickTagListener {
        void click(TagInfoBean bean);
    }

    /**
     * 图片在本地的地址
     *
     * @param path
     */
    public void setPath(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }



    public void setTagList(List<TagInfoBean> list) {
        clearTags();
        int num = 0;//用于计数
        infoBeanList = list;
        for (TagInfoBean bean : list) {
            if (TextUtils.isEmpty(bean.getName())) {
                continue;
            }
            bean.setIndex(num++);
            createTags(bean);

        }
    }



    private void createTags(TagInfoBean bean) {
        //获取文本的宽高
        final Rect bounds = new Rect();
        String name = bean.getName();
        if (name.length() > 16) {
            name = name.substring(0, 16) + "...";
        }

        mOvalPaint.getTextBounds(name, 0, name.length(), bounds);
        //获得边框的宽高
        final float mStokeHeight = bounds.bottom - bounds.top + topPadding * 2;

        int left = 0;
        int top = 0;
        top = (int) (bean.getHeight() * bean.getY() - mStokeHeight / 2);
        if (bean.isLeft()) {
            left = (int) (bean.getWidth() * bean.getX() - mRadius);
        } else {
            int mStokeWidth = bounds.right - bounds.left + leftPading * 2 + (bounds.bottom - bounds.top);
            left = (int) (bean.getWidth() * bean.getX() - mStokeWidth - lineLong);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout
                .LayoutParams
                .WRAP_CONTENT);

        params.setMargins(left, top, 0, 0);
        final TagTextView child = new TagTextView(getContext(), bean);
        child.setTagGestureListener(tagClickListener);
        mContentLayout.addView(child, params);
    }


    private void clearTags() {
        mContentLayout.removeAllViews();
    }
}
