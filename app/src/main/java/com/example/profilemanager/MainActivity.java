package com.example.profilemanager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private ActivityResultLauncher<Intent> avatarLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoImageView = findViewById(R.id.avatarImage);

        // Register the ActivityResultLauncher to handle the result from ProfileActivity
        avatarLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.hasExtra("avatarId")) {
                                int avatarId = data.getIntExtra("avatarId", 0);
                                if (avatarId != 0) {
                                    logoImageView.setImageResource(avatarId);
                                }
                            } else if (data.hasExtra("imageUri")) {
                                String imageUriString = data.getStringExtra("imageUri");
                                if (imageUriString != null) {
                                    Uri imageUri = Uri.parse(imageUriString);
                                    logoImageView.setImageURI(imageUri);
                                }
                            }
                        }
                    }
                }
        );

        // Set click listener for the avatar image
        logoImageView.setOnClickListener(v -> onSetAvatarButton(v));

        // Set click listener for the Open Maps button
        findViewById(R.id.openMapButton).setOnClickListener(this::OnOpenInGoogleMaps);

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        File file = new File(currentPhotoPath);
                        if (file.exists()) {
                            logoImageView.setImageURI(Uri.fromFile(file));
                        }
                    }
                });

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            logoImageView.setImageURI(selectedImageUri);
                        }
                    }
                });
    }

    // Opens Google Maps with the address provided in the EditText
    public void OnOpenInGoogleMaps(View view) {
        EditText teamAddress = findViewById(R.id.teamAddressField);
        String address = teamAddress.getText().toString();
        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    // Opens the ProfileActivity to select a new avatar
    public void onSetAvatarButton(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        avatarLauncher.launch(intent);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.profilemanager.fileprovider", // Update this package name
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}
