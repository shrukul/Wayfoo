package com.wayfoo.wayfoo;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by shrukul on 12/5/16.
 */
public class Intro extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlides();
    }

    private void addSlides() {
        addSlide(new SimpleSlide.Builder()
                .title("Wayfoo 1")
                .description("Write a Description")
                .image(R.drawable.one)
                .background(R.color.color_material_metaphor)
                .backgroundDark(R.color.color_dark_material_metaphor)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Wayfoo 2")
                .description("Write a Description")
                .image(R.drawable.one)
                .background(R.color.color_material_shadow)
                .backgroundDark(R.color.color_material_shadow)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Wayfoo 3")
                .description("Write a Description")
                .image(R.drawable.one)
                .background(R.color.color_material_motion)
                .backgroundDark(R.color.color_dark_material_motion)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Wayfoo 4")
                .description("Write a Description")
                .image(R.drawable.one)
                .background(R.color.color_material_bold)
                .backgroundDark(R.color.color_dark_material_bold)
                .permissions(new String[]{android.Manifest.permission.SEND_SMS,android.Manifest.permission.GET_ACCOUNTS})
                .build());
    }

}
