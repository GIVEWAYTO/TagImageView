package com.ken.tagimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * 作者: by KEN on 2018/7/14 17.
 * 邮箱: gr201655@163.com
 */

public class TagTextView extends View {


    private Paint ovalPaint = new Paint();

    private float mRadius = 0;   //外圆半径
    private float mInnerRadius = 0;       //内圆半径
    private RectF mCenterRect;
    private int lineLong = 0; //横线的长度
    private RectF mTextRoundRect;   //文字框内容高度
    private String textContent;    //文本内容
    private final int TextMaxNum = 16;  //文本最多显示16个文字
    private GestureDetectorCompat mGestureDetector;

    private float mStokeHeight;  //边框的高度
    private float mStokeWidth;   //边框的宽度
    private float mCircleY;   //中心圆的高度
    private float leftPadding;
    private float mTextX;     //文字起始x
    private float mTextY;    //文字baseline的高度
    private int mRoundX;

    private float mFirstDownX;
    private float mFirstDownY;

    private int leftBorder = 0;     //上边界  左边界
    private int rightBorder = 0;   //右边界
    private int bottomBorder = 0;  //底部边界

    private int circleX = 0;     //中心圆的x坐标
    private int circleY = 0;     //中心圆的y坐标

    private int parentWidth = 0;  //父级的宽度
    private int parentHeight = 0;  //父级的高度

    private float percentX = 0f;   //在父级的宽度占比
    private float percentY = 0f;   //在父级的高度占比

    private boolean isLeft = true;
    private TagInfoBean mTagInfoBean;
    private int mShadeColor;

    public static final int TAG_TEXT = 0;  //一般
    public static final int TAG_LOCATION = 1;  //地址
    public static final int TAG_BRAND = 2;  //品牌
    public static final int TAG_PRICE = 3; //价格

    private int notesTagType = 0;  //标签类型
    private Bitmap mBitmap;
    private float mTopPadding;
    private RectF mTypeIconRect;
    private int mIconWidth;

    private boolean isMoveXY = false;  // 用来记录是否被移动过  如果没有则不改变数据中的 xy百分比

    private RectF deleteRect;     //用来判断是否滑动到底部

    private PaintFlagsDrawFilter pfd;

    public TagInfoBean getTagInfoBean() {
        if (isMoveXY) {
            mTagInfoBean.setX(percentX);
            mTagInfoBean.setY(percentY);
        }
        return mTagInfoBean;
    }

    public TagTextView(Context context, TagInfoBean tagInfoBean) {
        this(context, null, 0);
        mTagInfoBean = tagInfoBean;
        initView(context);
    }

    public TagTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {
        if (mTagInfoBean == null) return;
        ovalPaint.setAntiAlias(true);
        ovalPaint.setStyle(Paint.Style.FILL);
        ovalPaint.setColor(Color.WHITE);
        ovalPaint.setStrokeWidth(Density.dip2px(context, 1));
        mTextRoundRect = new RectF();
        mShadeColor = Color.parseColor("#30ffffff");
        mRadius = Density.dip2px(context, 4);
        mInnerRadius = Density.dip2px(context, 2.5f);

        notesTagType = mTagInfoBean.getNotesTagType();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        switch (mTagInfoBean.getNotesTagType()) {
            case TAG_LOCATION:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tags_location, opts);
                break;
            case TAG_BRAND:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.brand, opts);
                break;
            case TAG_PRICE:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.price, opts);
                break;
            default:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tags_text, opts);
                break;
        }
        //防止bitmap 变模糊
        ovalPaint.setFilterBitmap(true);
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        ovalPaint.setTextSize(Density.dip2px(context, 14));
        lineLong = Density.dip2px(context, 15);
        textContent = mTagInfoBean.getName();
        if (TextUtils.isEmpty(textContent)) return;
        if (textContent.length() > TextMaxNum) {
            textContent = textContent.substring(0, TextMaxNum - 1) + "...";
        }
        mCanMove = mTagInfoBean.isCanMove();
        rightBorder = Density.getScreenW(context);


        //获取文本的宽高
        Rect bounds = new Rect();
        ovalPaint.getTextBounds(textContent, 0, textContent.length(), bounds);
        //边框与文字顶部底部的距离
        mTopPadding = Density.dip2px(context, 3);
        //边框与文字左右的距离
        leftPadding = Density.dip2px(context, 6);
        isLeft = mTagInfoBean.isLeft();

        //标签类别icon
        mTypeIconRect = new RectF();
        int iconHeight = bounds.bottom - bounds.top;
        mIconWidth = iconHeight;

        mTypeIconRect.set(lineLong + mRadius + leftPadding, mTopPadding, lineLong + mRadius + mIconWidth + leftPadding, mTopPadding + iconHeight);

        //获得边框的宽高
        mStokeHeight = bounds.bottom - bounds.top + mTopPadding * 2;
        mStokeWidth = bounds.right - bounds.left + leftPadding * 2 + mIconWidth;


        mTextRoundRect.set(lineLong + mRadius, 0, lineLong + mRadius + mStokeWidth, 0 + mStokeHeight);
        //边框圆角半径
        mRoundX = Density.sp2px(getContext(), 100);

        //圆心y
        mCircleY = mStokeHeight / 2;

        //文字起始x
        mTextX = lineLong + mRadius + leftPadding + mIconWidth;

        //文字y
        Paint.FontMetricsInt fontMetrics = ovalPaint.getFontMetricsInt();
        mTextY = (mTextRoundRect.top + mTextRoundRect.bottom - fontMetrics.bottom -
                fontMetrics.top) / 2;

        //整个的控件的位置
        mCenterRect = new RectF();
        mCenterRect.set(0, 0, mRadius + lineLong + mStokeWidth, mStokeHeight);

        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);

        parentWidth = rightBorder;
        parentHeight = (int) mTagInfoBean.getHeight();

        bottomBorder = parentHeight;

        //删除--控件的半径
        int delet_raduis = Density.dip2px(context, 15);
        float top = parentHeight - Density.dip2px(context, 80) - mStokeHeight / 2;

        deleteRect = new RectF(rightBorder / 2 - (lineLong + mStokeWidth + delet_raduis), top,
                rightBorder / 2 + delet_raduis, parentHeight - Density.dip2px(context, 50) - mStokeHeight / 2);

    }


    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius + lineLong + mStokeWidth), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mStokeHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        ovalPaint.setAntiAlias(true);
        //绘制外圆
        ovalPaint.setStyle(Paint.Style.FILL);

        ovalPaint.setColor(mShadeColor);
        if (isLeft) {
            canvas.drawCircle(mRadius, mCircleY , mRadius, ovalPaint);
            mTextRoundRect.set(lineLong + mRadius, 0, lineLong + mRadius + mStokeWidth,  mStokeHeight);
            //画边框遮罩
            canvas.drawRoundRect(mTextRoundRect, mRoundX, mRoundX, ovalPaint);
        } else {
            mTextRoundRect.set(0, 0, mStokeWidth,  mStokeHeight);

            canvas.drawCircle(mStokeWidth + lineLong, mCircleY , mRadius, ovalPaint);
            //画边框遮罩
            canvas.drawRoundRect(mTextRoundRect, mRoundX, mRoundX, ovalPaint);
        }
        canvas.setDrawFilter(pfd);
        //绘制内圆
        ovalPaint.setColor(Color.WHITE);
        if (isLeft) {
            canvas.drawCircle(mRadius, mCircleY , mInnerRadius, ovalPaint);

            //画线
            canvas.drawLine(mRadius,  mStokeHeight / 2, lineLong + mRadius,  mStokeHeight / 2, ovalPaint);

            mTypeIconRect.set(lineLong + mRadius + leftPadding, mTopPadding, lineLong + mRadius + mIconWidth + leftPadding, mTopPadding + mIconWidth);
            //画标签类型icon
            canvas.drawBitmap(mBitmap, null, mTypeIconRect, ovalPaint);

            //文字起始x
            mTextX = lineLong + mRadius + leftPadding + mIconWidth;

            //文字y
            Paint.FontMetricsInt fontMetrics = ovalPaint.getFontMetricsInt();
            mTextY =  (mTextRoundRect.top + mTextRoundRect.bottom - fontMetrics.bottom -
                    fontMetrics.top) / 2;
            canvas.drawText(textContent, mTextX, mTextY, ovalPaint);

            //画边框
            ovalPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(mTextRoundRect, mRoundX, mRoundX, ovalPaint);

        } else {
            canvas.drawCircle(mStokeWidth + lineLong, mCircleY , mInnerRadius, ovalPaint);

            mTypeIconRect.set(leftPadding, mTopPadding, mIconWidth + leftPadding, mTopPadding + mIconWidth);
            //画标签类型icon
            canvas.drawBitmap(mBitmap, null, mTypeIconRect, ovalPaint);

            //画线
            canvas.drawLine(mStokeWidth,  mStokeHeight / 2, mStokeWidth + lineLong,  mStokeHeight / 2, ovalPaint);

            //文字
            mTextX = leftPadding + mIconWidth;
            //文字y
            Paint.FontMetricsInt fontMetrics = ovalPaint.getFontMetricsInt();
            mTextY =  (mTextRoundRect.top + mTextRoundRect.bottom - fontMetrics.bottom -
                    fontMetrics.top) / 2;
            canvas.drawText(textContent, mTextX, mTextY, ovalPaint);

            //画边框
            ovalPaint.setStyle(Paint.Style.STROKE);

            canvas.drawRoundRect(mTextRoundRect, mRoundX, mRoundX, ovalPaint);
        }

        super.onDraw(canvas);
        meausePercent();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanMove && mTagGestureListener != null && event.getAction() == MotionEvent.ACTION_UP) {
            mTagGestureListener.onUp(this, mTagInfoBean);

            float positionX = event.getRawX() - mFirstDownX - tempX;
            float positionY = event.getRawY() - mFirstDownY - tempY;

            if (positionX < leftBorder) {
                positionX = leftBorder;
            } else {
                float viewWidth = rightBorder - mRadius - lineLong - mStokeWidth;
                if (positionX > viewWidth) positionX = viewWidth;
            }

            if (positionY < 0) {
                positionY = 0;
            } else {
                float viewHeight = bottomBorder - mStokeHeight;
                if (positionY > viewHeight) positionY = viewHeight;
            }

            //处于删除区域
            if (deleteRect.contains(positionX, positionY)) {
                mTagGestureListener.inDeleteRect(TagTextView.this, mTagInfoBean);
            }
        }
        return mGestureDetector.onTouchEvent(event);

    }

    public interface TagGestureListener {
        // 手指按下
        void onDown(View view, TagInfoBean bean);

        // 手指抬起
        void onUp(View view, TagInfoBean bean);

        // 点击了标签
        void clickTag(View view, TagInfoBean bean);

        // 在删除区域
        void inDeleteRect(View view, TagInfoBean bean);

        // 在滑动
        void move(View view, TagInfoBean bean);
    }

    //标签文字内容被点击
    private TagGestureListener mTagGestureListener;

    public void setTagGestureListener(TagGestureListener tagClickListener) {
        mTagGestureListener = tagClickListener;
    }

    private boolean mCanMove = true; //可以被移动

    private float tempX;
    private float tempY;
    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {


        @Override
        public boolean onDown(MotionEvent e) {

            if (mCanMove && mTagGestureListener != null)
                mTagGestureListener.onDown(TagTextView.this, mTagInfoBean);

            mFirstDownX = e.getX();
            mFirstDownY = e.getY();

            //记录父容器和手机屏幕左上角的x和y的值
            tempX = e.getRawX() - e.getX() - TagTextView.this.getX();
            tempY = e.getRawY() - e.getY() - TagTextView.this.getY();
            return mCenterRect.contains(e.getX(), e.getY()) || super.onDown(e);
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //标签被点击
            if (mCanMove) {
                //换方向
                isLeft = !isLeft;
                isMoveXY = true;
                //重新记录点的位置
                meausePercent();
                invalidate();
            } else if (mTextRoundRect.contains(e.getX(), e.getY())) {
                if (!mCanMove && mTagGestureListener != null) { // 不可编辑状态
                    mTagGestureListener.clickTag(TagTextView.this, mTagInfoBean);
                }
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            //可以滑动编辑
            if (mCanMove) {
                float positionX = e2.getRawX() - mFirstDownX - tempX;
                float positionY = e2.getRawY() - mFirstDownY - tempY;

                if (positionX < leftBorder) {
                    positionX = leftBorder;
                } else {
                    float viewWidth = rightBorder - mRadius - lineLong - mStokeWidth;
                    if (positionX > viewWidth) positionX = viewWidth;
                }

                if (positionY < 0) {
                    positionY = 0;
                } else {
                    float viewHeight = bottomBorder - mStokeHeight;
                    if (positionY > viewHeight) positionY = viewHeight;
                }

                TagTextView.this.setX(positionX);
                TagTextView.this.setY(positionY);
                if (mCanMove && mTagGestureListener != null) {
                    mTagGestureListener.move(TagTextView.this, mTagInfoBean);

                }
                isMoveXY = true;

                meausePercent();

                return true;
            } else {
                return false;
            }

        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //请求所有父控件及祖宗控件不要拦截事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 计算圆心的坐标占比
     */
    private void meausePercent() {
        if (parentWidth == 0 || parentHeight == 0) return;
        if (isLeft) {
            circleX = (int) (getX() + mRadius);
            circleY = (int) (getY() + mStokeHeight / 2);
        } else {
            circleX = (int) (getX() + mStokeWidth + lineLong);
            circleY = (int) (getY() + mStokeHeight / 2);
        }
        percentX = (((float) circleX) / parentWidth);
        percentY = (((float) circleY) / parentHeight);
//        Log.e("zz", "meausePercent: parentx  ===  "+ getX() );
//        Log.e("zz", "meausePercent: x == " + circleX + "  yy == " + circleY);
//        Log.e("zz", "meausePercent: percentX == " + percentX + "   percentY == " + percentY);
        Log.e("zz", "圆点相关数据");
        Log.e("zz", "圆点坐标 x == "+ circleX+"  , y == " + circleY );
        Log.e("zz", "圆点在图片上的坐标比例  x  == "+ percentX +"  , y == " + percentY );
        Log.e("zz", "圆点数据："+ getTagInfoBean().toString() );
    }


}
