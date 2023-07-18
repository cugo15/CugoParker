package com.example.cugoparker.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.cugoparker.R;
import com.example.cugoparker.databinding.ActivitySplashScreenBinding;
import com.example.cugoparker.view.MapsActivity;

public class SplashScreen extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    private static int SPLASH_SCREEN = 2000;
    private Animation top;
    private Animation text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        text = AnimationUtils.loadAnimation(this,R.anim.text_animation);
        binding.car.setAnimation(top);
        binding.textApp.setAnimation(text);
        binding.textWelcome.setAnimation(text);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
                intent.putExtra("name","main");
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}