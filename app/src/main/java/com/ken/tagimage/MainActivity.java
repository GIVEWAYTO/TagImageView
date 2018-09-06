package com.ken.tagimage;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int imageH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        final TagImageView tag_view = findViewById(R.id.tag_content);
        tag_view.setClickTagListener(new TagImageView.ClickTagListener() {
            @Override
            public void click(TagInfoBean bean) {
                Toast.makeText(MainActivity.this, "标签被点击 == " + bean.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        //添加标签
        tag_view.setAddTagListener(new TagImageView.AddTagListener() {
            @Override
            public void addTag(String path, double rawX, double rawY) {
                final TagInfoBean bean = new TagInfoBean();
                bean.setCanMove(true);
                bean.setNotesTagId(652);
                bean.setNotesTagType(TagTextView.TAG_TEXT);
                //通过手机中的图片地址  或者  网络拉取的图片信息  获得图片宽高
                bean.setPicWidth(1010);
                bean.setPicHeight(1324);
                bean.setUrl("tag点的链接url");
                // 显示控件的显示 依照图片的本身的宽高比例进行动态设置
                bean.setWidth(Density.getScreenW(MainActivity.this));
                //标签在控件上的比例
                bean.setLeft(!(rawX > bean.getWidth()/ 2));
                bean.setX(rawX / bean.getWidth());
                bean.setY(rawY / imageH);
                bean.setHeight(imageH);
                ViewDialogFragment dialogFragment = new ViewDialogFragment();
                dialogFragment.setCallback(new ViewDialogFragment.Callback() {
                    @Override
                    public void onClick(String tabName) {
                        if (TextUtils.isEmpty(tabName))
                            tabName = "女孩";
                        bean.setName(tabName);
                        Log.e("zz", "onClick: "+bean.getName() + "  " + imageH );
                        tag_view.addTag(bean);
                    }
                });
                dialogFragment.show(getSupportFragmentManager());
            }
        });
        //删除标签
        tag_view.setDeleteTagListener(new TagImageView.DeleteTagListener() {
            @Override
            public void remove(String path, TagInfoBean bean) {
                Toast.makeText(MainActivity.this, "删除标签 == " + bean.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        //设置图片的路径
        tag_view.setPath("一般是本地图片地址,这里用的是资源图片");  //可用来标记这些标签属于哪张图片

        //添加初始标签
        List<TagInfoBean> tagInfoBeanList = new ArrayList<>();

        tagInfoBeanList.add(createTag1());
        tagInfoBeanList.add(createTag2());
        tagInfoBeanList.add(createTag3());

        //设置 图片的高度  可根据实际的图片高度比例  设置
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageH);
        findViewById(R.id.image).setLayoutParams(params);

        tag_view.setLayoutParams(params);
        tag_view.setTagList(tagInfoBeanList);
    }

    @NonNull
    private TagInfoBean createTag1() {
        TagInfoBean bean = new TagInfoBean();
        //该标签是否可以移动
        bean.setCanMove(false);
        bean.setLeft(false);
        bean.setName("一杯奶茶");
        bean.setNotesTagId(652);
        bean.setNotesTagType(TagTextView.TAG_BRAND);

        //通过手机中的图片地址  或者  网络拉取的图片信息  获得图片宽高
        bean.setPicWidth(1010);
        bean.setPicHeight(1324);
        bean.setUrl("tag点的链接url");
        // 显示控件的显示 依照图片的本身的宽高比例进行动态设置
        bean.setWidth(Density.getScreenW(this));
        imageH = 0;
        //项目中需求是只有1：1  和  3：4的比例   这个可根据实际修改   直接按图片比例也可以
        if (bean.getPicWidth() / bean.getPicHeight() > 0.85f) {
            imageH = (int) bean.getWidth();
        } else {
            imageH = (int) (bean.getWidth() * 4 / 3);
        }

        //标签原点在照片上的比例
        bean.setX(0.7513889);
        bean.setY(0.5864583);
        bean.setHeight(imageH);
        return bean;
    }

    @NonNull
    private TagInfoBean createTag2() {
        TagInfoBean bean = new TagInfoBean();
        bean.setCanMove(true);
        bean.setLeft(true);
        bean.setName("￥55 粉色衣服");
        bean.setNotesTagId(652);
        bean.setNotesTagType(TagTextView.TAG_PRICE);

        //通过手机中的图片地址  或者  网络拉取的图片信息  获得图片宽高
        bean.setPicWidth(1010);
        bean.setPicHeight(1324);
        bean.setUrl("tag点的链接url");
        // 显示控件的显示 依照图片的本身的宽高比例进行动态设置
        bean.setWidth(Density.getScreenW(this));
        imageH = 0;
        //项目中需求是只有1：1  和  3：4的比例   这个可根据实际修改   直接按图片比例也可以
        if (bean.getPicWidth() / bean.getPicHeight() > 0.85f) {
            imageH = (int) bean.getWidth();
        } else {
            imageH = (int) (bean.getWidth() * 4 / 3);
        }

        //标签原点在照片上的比例
        bean.setX(0.5625);
        bean.setY(0.81041664);
        bean.setHeight(imageH);
        return bean;
    }
    @NonNull
    private TagInfoBean createTag3() {
        TagInfoBean bean = new TagInfoBean();
        bean.setCanMove(true);
        bean.setLeft(true);
        bean.setName("大眼睛");
        bean.setNotesTagId(652);
        bean.setNotesTagType(TagTextView.TAG_BRAND);

        //通过手机中的图片地址  或者  网络拉取的图片信息  获得图片宽高
        bean.setPicWidth(1010);
        bean.setPicHeight(1324);
        bean.setUrl("tag点的链接url");
        // 显示控件的显示 依照图片的本身的宽高比例进行动态设置
        bean.setWidth(Density.getScreenW(this));
        imageH = 0;
        //计算图片的高度   因为项目中需求是图片只有1：1  和  3：4的比例   这个可根据实际修改   直接按图片比例也可以
        if (bean.getPicWidth() / bean.getPicHeight() > 0.85f) {
            imageH = (int) bean.getWidth();
        } else {
            imageH = (int) (bean.getWidth() * 4 / 3);
        }

        //标签原点在照片上的比例
        bean.setX(0.35833332);
        bean.setY(0.29583332);
        bean.setHeight(imageH);
        return bean;
    }
}
