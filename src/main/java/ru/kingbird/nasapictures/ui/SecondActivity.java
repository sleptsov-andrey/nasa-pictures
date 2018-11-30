package ru.kingbird.nasapictures.ui;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ru.kingbird.nasapictures.R;

public class SecondActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleGestureDetector;
    private ImageView imageView;
    private float mScaleFactor = 1.0f;
    private static final String EXTRA_IMAGE_SRC =
            "ru.kingbird.nasapictures.extra_img_src";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        imageView = findViewById(R.id.imageView);
        String imgSrc = getIntent().getStringExtra(EXTRA_IMAGE_SRC);
        Glide.with(this).load(imgSrc).into(imageView);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    public static Intent newIntent(Context packageContext, String imgSrc) {
        Intent intent = new Intent(packageContext, SecondActivity.class);
        intent.putExtra(EXTRA_IMAGE_SRC, imgSrc);
        return intent;
    }
}
