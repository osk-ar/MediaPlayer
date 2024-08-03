package com.example.mediaplayer;

import static com.example.mediaplayer.HomeActivity.data;

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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class MainActivity extends AppCompatActivity {

    // declare variables
    private int currentIndex;
    private String[] names;

    // declare widgets
    ImageView img;
    ProgressBar sound_progress;

    TextView time_passed, sound_length, soundName, soundUser;

    NeumorphImageButton play, forward, backward;
    ImageButton back;

    public static MediaPlayer mp;
    Handler handler;
    Runnable runnable;
    RotateAnimation rotate;
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
        // *


        // initialize Widgets;
        img = findViewById(R.id.blue_img);
        sound_progress = findViewById(R.id.sound_progress);

        time_passed = findViewById(R.id.time_passed);
        sound_length = findViewById(R.id.sound_length);
        soundName = findViewById(R.id.sound_name_single);
        soundUser = findViewById(R.id.user_name_single);

        play = findViewById(R.id.play_pause);
        forward = findViewById(R.id.after);
        backward = findViewById(R.id.before);
        back = findViewById(R.id.back);

        Intent intent = getIntent();
        if (savedInstanceState == null) { // Check if activity is created for the first time
            currentIndex = intent.getIntExtra("soundID", 0);
            names = splitAndTrim(intent.getStringExtra("soundTitle"));
            mp = MediaPlayer.create(this, data.get(currentIndex).getSound());
            soundName.setText(names[1]);
            soundUser.setText(names[0]);
        }

        handler = new Handler();
        firstTime = true;
        // *


        // Create Animation
        rotate = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(10000); // Adjust duration as needed
        rotate.setRepeatCount(Animation.INFINITE); // Repeat indefinitely
        rotate.setInterpolator(new LinearInterpolator()); // For smooth rotation

        // Crop Image to Circle
        Glide.with(this)
                .load(R.drawable.background)  // Replace with your actual image path
                .apply(new RequestOptions().circleCrop().override(750, 750))
                .into(img);

        // Create a Scheduled runnable to determine the progress value
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
        // ***
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mp == null) { // Recreate MediaPlayer if it was released
            mp = MediaPlayer.create(this, data.get(currentIndex).getSound());
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                sound_progress.setMax(mp.getDuration());
                sound_length.setText(getFormattedTime(mp.getDuration()));
            }
        });

        // Clickable functionality

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mp.isPlaying()){
                    play.setPaddingRelative(30,20,0,20);
                    play.setImageResource(R.drawable.play);
                    img.clearAnimation();
                    mp.pause();
                    handler.removeCallbacks(runnable);
                }
                else {
                    sound_progress.setMax(mp.getDuration());
                    sound_length.setText(getFormattedTime(mp.getDuration()));
                    play.setPaddingRelative(30, 20, 35, 20);
                    play.setImageResource(R.drawable.pause);
                    img.startAnimation(rotate);
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            handler.removeCallbacks(runnable);
                            img.clearAnimation();
                            play.setPaddingRelative(30,20,0,20);
                            play.setImageResource(R.drawable.play);
                            sound_progress.setProgress(0);
                        }
                    });
                    handler.postDelayed(runnable, 100);

                }
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setPaddingRelative(30,20,35,20);
                play.setImageResource(R.drawable.pause);
                img.startAnimation(rotate);

                mp.reset();
                currentIndex = (currentIndex + 1) % data.size();
                names = splitAndTrim(data.get(currentIndex).getName());
                soundName.setText(names[1]);
                soundUser.setText(names[0]);
                mp = MediaPlayer.create(MainActivity.this,data.get(currentIndex).getSound());
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        sound_progress.setMax(mp.getDuration());
                        sound_length.setText(getFormattedTime(mp.getDuration()));
                    }
                });
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handler.removeCallbacks(runnable);
                        img.clearAnimation();
                        play.setPaddingRelative(30,20,0,20);
                        play.setImageResource(R.drawable.play);
                        sound_progress.setProgress(0);
                    }
                });
                handler.postDelayed(runnable, 100);
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setPaddingRelative(30,20,35,20);
                play.setImageResource(R.drawable.pause);
                img.startAnimation(rotate);

                mp.reset();
                if(currentIndex == 0){currentIndex = data.size()-1;} else {currentIndex-=1;}
                names = splitAndTrim(data.get(currentIndex).getName());
                soundName.setText(names[1]);
                soundUser.setText(names[0]);
                mp = MediaPlayer.create(MainActivity.this,data.get(currentIndex).getSound());

                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        sound_progress.setMax(mp.getDuration());
                        sound_length.setText(getFormattedTime(mp.getDuration()));
                    }
                });

                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        handler.removeCallbacks(runnable);
                        img.clearAnimation();
                        play.setPaddingRelative(30,20,0,20);
                        play.setImageResource(R.drawable.play);
                        sound_progress.setProgress(0);
                    }
                });

                handler.postDelayed(runnable, 100);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (!firstTime) {
            img.startAnimation(rotate);
            handler.postDelayed(runnable, 100);
        }
    }

    protected void onPause() {
        super.onPause();
        if (mp != null) {
            img.clearAnimation();
            handler.removeCallbacks(runnable);
        }
        firstTime = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // empty memory
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }

    }

    // convert millisecond into time format
    public String getFormattedTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;int minutes = (milliseconds / (1000 * 60)) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public String[] splitAndTrim(@NonNull String text) {
        String[] parts = text.split("-");
        for (int i =0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

}


