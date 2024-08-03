package com.example.mediaplayer;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    RecyclerView RV;
    public static ArrayList<MyData> data = new ArrayList<>();
    SoundListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        data.add(new MyData( "AbdelBaset Hamoda - I'm not me", R.raw.abdelbaset_hamoda_ana_m4_ana));
        data.add(new MyData( "3b3az - The Recipe", R.raw.ab3az_el_khalta));
        data.add(new MyData( "Wegz - The Luck", R.raw.wegz_el_bakht));
        data.add(new MyData( "Batistuta - Wooden Face", R.raw.batistuta_wesh_kha4ab));
        data.add(new MyData( "BigSam - Egyption Life", R.raw.bigsam_ma_bthoon));
        data.add(new MyData( "ElWaili - Slave and Demon", R.raw.el_waili_el_3bd_wel_4etan));
        data.add(new MyData( "Afroto - Cigarette", R.raw.afroto_segara));
        data.add(new MyData( "BigSam - Wasi", R.raw.bigsam_wasi));
        data.add(new MyData( "Maha Ftouny - Patience is good", R.raw.maha_ftouni_el_sabr_gamil));
        data.add(new MyData( "Unknown - Ya Wa3dy", R.raw.unknown_ya_wa3dy));
        data.add(new MyData( "Ahmed Kamel - Ya leel", R.raw.ahmed_kamel_ya_leel));



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        RV = findViewById(R.id.Sound_items_list);
        adapter = new SoundListAdapter();
        RV.setAdapter(adapter);
        adapter.setSoundList(data);
        RV.setLayoutManager(new LinearLayoutManager(this));

    }
}