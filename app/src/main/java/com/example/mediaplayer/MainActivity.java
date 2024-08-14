package com.example.mediaplayer;

import static com.example.mediaplayer.HomeActivity.data;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity {

    // declare variables
    private int currentIndex;
    private String name;

    // declare widgets
    ImageView img;
    ProgressBar sound_progress;

    TextView time_passed, sound_length, soundName, soundUser;

    NeumorphImageButton play, forward, backward;
    ImageButton back;

    public static MediaPlayer mp;
    Intent intent;
    Handler handler;
    Runnable runnable;
    ObjectAnimator rotationAnimator;
    boolean firstTime;
    // *

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // idk
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // initialize Widgets;
        img = findViewById(R.id.blue_img);
        soundName = findViewById(R.id.sound_name_single);
        soundUser = findViewById(R.id.user_name_single);
        sound_progress = findViewById(R.id.sound_progress);
        time_passed = findViewById(R.id.time_passed);
        sound_length = findViewById(R.id.sound_length);
        play = findViewById(R.id.play_pause);
        forward = findViewById(R.id.after);
        backward = findViewById(R.id.before);
        back = findViewById(R.id.back);
        intent= getIntent();
        handler = new Handler();
        firstTime = true;

        // Crop Image to Circle
        Glide.with(this)
                .load(R.drawable.background)  // Replace with your actual image path
                .apply(new RequestOptions().circleCrop().override(750, 750))
                .into(img);

        // Create Animation
        rotationAnimator = createAnimation();
        // Create a Scheduled runnable to determine the progress value
        runnable = createRunnable();
        // Check if activity is created for the first time
        if (savedInstanceState == null) {
            // if yes initialize mediaPlayer
            currentIndex = intent.getIntExtra("soundID", 0);
            name = splitAndTrim(intent.getStringExtra("soundTitle"));
            initSound();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start Sound
        startSound();
        // back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // play / pause -> button
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp.getCurrentPosition() == 0 && !mp.isPlaying()){
                    initSound();
                    startSound();
                } else{
                    if(mp.isPlaying())
                        pauseSound();
                    else
                        startPausedSound();
                }


            }});
        // forward button
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.reset();
                currentIndex = (currentIndex + 1) % data.size();
                name = splitAndTrim(data.get(currentIndex).getName());
                initSound();
                startSound();
            }
        });
        // backward button
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.reset();
                if(currentIndex == 0){currentIndex = data.size()-1;} else {currentIndex-=1;}
                name = splitAndTrim(data.get(currentIndex).getName());
                initSound();
                startSound();
            }
        });

    }

    private void initSound(){
        mp = MediaPlayer.create(getBaseContext(), data.get(currentIndex).getSound());
        soundUser.setText(name);
        sound_progress.setMax(mp.getDuration());
        sound_length.setText(getFormattedTime(mp.getDuration()));
    }

    private void startSound(){
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                play.setPaddingRelative(0, 0, 0, 0);
                play.setImageResource(R.drawable.pause);
                rotationAnimator.start();
                handler.postDelayed(runnable, 100);
                mp.start();
            }
        });
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(runnable);
                rotationAnimator.pause();
                play.setPaddingRelative(30,20,0,20);
                play.setImageResource(R.drawable.play);
                sound_progress.setProgress(0);
                time_passed.setText(getFormattedTime(0));
                mp.reset();
            }
        });
    }

    private void startPausedSound(){
        play.setPaddingRelative(0, 0, 0, 0);
        play.setImageResource(R.drawable.pause);
        rotationAnimator.resume();
        handler.postDelayed(runnable, 100);
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                handler.removeCallbacks(runnable);
                rotationAnimator.pause();
                play.setPaddingRelative(30,20,0,20);
                play.setImageResource(R.drawable.play);
                sound_progress.setProgress(0);
                time_passed.setText(getFormattedTime(0));
                mp.reset();
            }
        });
    }

    private void pauseSound(){
        play.setPaddingRelative(30,20,0,20);
        play.setImageResource(R.drawable.play);
        rotationAnimator.pause();
        mp.pause();
        handler.removeCallbacks(runnable);
    }

    private ObjectAnimator createAnimation(){
        rotationAnimator = ObjectAnimator.ofFloat(img, "rotation", 0f, 360f);
        rotationAnimator.setDuration(10000); // Set the duration for one full rotation
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE); // Infinite rotation
        rotationAnimator.setInterpolator(new LinearInterpolator()); // Linear animation

        return rotationAnimator;
    }

    private Runnable createRunnable(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    int currentPosition = mp.getCurrentPosition();
                    sound_progress.setProgress(currentPosition);
                    time_passed.setText(getFormattedTime(currentPosition));
                }
                handler.postDelayed(this, 100); // Update every 100 milliseconds
            }
        };

        return runnable;
    }

    // convert millisecond into time format
    public String getFormattedTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;int minutes = (milliseconds / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // Crop DocumentID (SoundFilePath) to only Name
    public static String splitAndTrim(String text) {
        if (text.indexOf('.') != -1){
        return text.substring(text.lastIndexOf('/')+1, text.indexOf('.'));
        }
        else {
            return text.substring(text.lastIndexOf('/')+1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            rotationAnimator.resume();
            handler.postDelayed(runnable, 100);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            rotationAnimator.pause();
            handler.removeCallbacks(runnable);
        }
        firstTime = false;
    }
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }

    }
}


