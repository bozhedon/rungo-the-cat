package com.myrungo.rungo.onboarding;

import android.content.Intent;
import android.os.Bundle;
import com.github.paolorotolo.appintro.AppIntro2;
import com.myrungo.rungo.AppActivity;
import com.myrungo.rungo.R;

public class CustomIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

// Здесь указываем количество слайдов, например нам нужно 3
        addSlide(SampleSlide.newInstance(R.layout.intro_1)); //
        addSlide(SampleSlide.newInstance(R.layout.intro_2));
        addSlide(SampleSlide.newInstance(R.layout.intro_3));
        addSlide(SampleSlide.newInstance(R.layout.intro_4));

    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
    }


    @Override
    public void onNextPressed() {
        // Do something here
    }

    @Override
    public void onDonePressed() {
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something here
    }
}
