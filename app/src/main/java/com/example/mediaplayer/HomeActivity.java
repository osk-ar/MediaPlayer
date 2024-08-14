package com.example.mediaplayer;

import static com.example.mediaplayer.MainActivity.splitAndTrim;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphButton;

public class HomeActivity extends AppCompatActivity {
    public static ArrayList<SoundsClass> data = new ArrayList<>();
    private ActivityResultLauncher<Intent> directoryPickerLauncher;
    RecyclerView RV;
    SoundListAdapter adapter;
    FloatingActionButton add_btn;
    boolean perm_denied = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_home);
        RV = findViewById(R.id.Sound_items_list);
        adapter = new SoundListAdapter();
        RV.setAdapter(adapter);
        adapter.setSoundList(data);
        RV.setLayoutManager(new LinearLayoutManager(this));
        add_btn = findViewById(R.id.add_btn);



        directoryPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri directoryUri = data.getData();
                            getFilesFromDirectory(directoryUri);
                        }
                    }
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getExternalStoragePermission();
            }
        });

    }

    private void getExternalStoragePermission(){
        // check if the permission granted or not
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            // Request Permission
            if(!perm_denied){
            Toast.makeText(this, "I'm Requesting Permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);}
            else {
                Toast.makeText(this, "Please give permission from settings", Toast.LENGTH_SHORT).show();
            }
        }

        else{ // permission granted
            Toast.makeText(this, "I Have Permission", Toast.LENGTH_SHORT).show();
            openDirectoryChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1 :
                if(grantResults.length>0 &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } break;
            default:
                if(grantResults.length>0 &&
                        (grantResults[0] != PackageManager.PERMISSION_GRANTED)){
                    perm_denied = true;
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Please allow it from settings", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openDirectoryChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        directoryPickerLauncher.launch(intent);
    }

    private void getFilesFromDirectory(Uri directoryUri) {
        ContentResolver contentResolver = getContentResolver();

        int oldDataSize = data.size();
        int counter = 0;

        Uri treeUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                directoryUri,
                DocumentsContract.getTreeDocumentId(directoryUri)
        );

        Cursor cursor = contentResolver.query(treeUri, null, null, null, null);
        if (cursor != null) {

            while (cursor.moveToNext()) {
                String documentId = cursor.getString(
                        cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                );
                Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(directoryUri, documentId);

                // Check if the file is a sound file by inspecting its MIME type
                String fileType = contentResolver.getType(fileUri);
                if (fileType != null && fileType.startsWith("audio/")) {
                    data.add(new SoundsClass(splitAndTrim(documentId), fileUri));
                    counter++;

                }
            }
            cursor.close();
        }
        // Check to see if you added any new items
        if (data.size() == oldDataSize) {
            Toast.makeText(this, "No sound files found in the selected directory.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Added "+ counter +" new sounds!", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
    }

}