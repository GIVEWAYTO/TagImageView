package com.ken.tagimage;

/**
 * 作者: by KEN on 2018/7/14 17.
 * 邮箱: gr201655@163.com
 *
 * 由于项目为统一跟后台定义字段相同  有部分命名不准确
 */


public class TagInfoBean {
    private String name;  //标签内容
    private int notesTagType;    //标签type
    private String url;     //标签url

    private double x;  //圆心x的占比
    private double y;  //圆心y的占比

    private float width;  //控件宽度
    private float height;  //控件高度

    private float picWidth;  //图片的宽度
    private float picHeight; //图片的高度

    private int notesTagId; //标签id

    private boolean isLeft = true;  //圆点是否在左边

    private boolean isCanMove = true;  //标签是否可以移动

    private int mIndex;    //用来记录在编辑标签中的index 位置

    public void setCanMove(boolean canMove) {
        isCanMove = canMove;
    }

    public boolean isCanMove() {
        return isCanMove;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getNotesTagType() {
        return notesTagType;
    }

    public void setNotesTagType(int notesTagType) {
        this.notesTagType = notesTagType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public float getPicWidth() {
        return picWidth;
    }

    public void setPicWidth(float picWidth) {
        this.picWidth = picWidth;
    }

    public float getPicHeight() {
        return picHeight;
    }

    public void setPicHeight(float picHeight) {
        this.picHeight = picHeight;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getNotesTagId() {
        return notesTagId;
    }

    public void setNotesTagId(int notesTagId) {
        this.notesTagId = notesTagId;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    @Override
    public String toString() {
        return "TagInfoBean{" +
                "name='" + name + '\'' +
                ", notesTagType=" + notesTagType +
                ", url='" + url + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", picWidth=" + picWidth +
                ", picHeight=" + picHeight +
                ", notesTagId=" + notesTagId +
                ", isLeft=" + isLeft +
                ", isCanMove=" + isCanMove +
                ", mIndex=" + mIndex +
                '}';
    }
}
