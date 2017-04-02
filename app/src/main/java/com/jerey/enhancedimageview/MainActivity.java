package com.jerey.enhancedimageview;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jerey.imageview.EnhancedImageView;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int[] mImages = new int[]{R.drawable.about_bg,R.drawable.bg_java, R.drawable.my_desk};
    private ImageView[] mImageViews = new ImageView[mImages.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        mViewPager = (ViewPager) findViewById(R.id.my_vp);

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                EnhancedImageView imageView = new EnhancedImageView(getApplicationContext());
                imageView.setImageResource(mImages[position]);
                container.addView(imageView);
                mImageViews[position] = imageView;
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViews[position]);
            }

            @Override
            public int getCount() {
                return mImages.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
    }
}
