package com.example.profilemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupImageViews();

        // Back button to return to MainActivity
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Load From Phone button
        Button loadFromPhoneButton = findViewById(R.id.loadFromPhoneButton);
        loadFromPhoneButton.setOnClickListener(v -> openGallery());

        // Initialize gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("imageUri", selectedImageUri.toString());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        }
                    }
                });
    }

    // Sets up click listeners for all avatar images
    private void setupImageViews() {
        int[] imageViewIds = {
                R.id.imageView1, R.id.imageView2, R.id.imageView3,
                R.id.imageView4, R.id.imageView5, R.id.imageView6
        };

        int[] avatarDrawables = {
                R.drawable.ic_logo_00,
                R.drawable.ic_logo_01,
                R.drawable.ic_logo_02,
                R.drawable.ic_logo_03,
                R.drawable.ic_logo_04,
                R.drawable.ic_logo_05
        };

        for (int i = 0; i < imageViewIds.length; i++) {
            ImageView imageView = findViewById(imageViewIds[i]);
            int avatarId = avatarDrawables[i];

            imageView.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("avatarId", avatarId);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }
    }

    // Opens the gallery to select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}

