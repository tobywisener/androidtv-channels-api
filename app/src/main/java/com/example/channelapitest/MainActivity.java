package com.example.channelapitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.ContentUris;
import android.content.ContentValues;
import android.media.tv.TvContract;
import androidx.tvprovider.media.tv.PreviewProgram;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {
    private static final String TAG = "TOBY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_browse_fragment, new MainFragment())
                    .commitNow();
        }

        publishTestPreviewProgram();
    }

    private void toast(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void createPreviewProgram(int channelId) {
        PreviewProgram previewProgram = new PreviewProgram.Builder()
                .setChannelId(channelId)
                .setType(TvContract.PreviewPrograms.TYPE_MOVIE)
                .setTitle("Program Title")
                .setDescription("Program Description")
                .setPosterArtUri(Uri.parse("http://example.com/poster_art.png"))
                // Set more attributes...
                .build();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Uri previewProgramUri = getContentResolver().insert(TvContract.PreviewPrograms.CONTENT_URI,
                    previewProgram.toContentValues());
        }
    }
    private void publishTestPreviewProgram() {
        // Create a channel first (if it doesn't exist)
        ContentValues channelValues = new ContentValues();
        channelValues.put(TvContract.Channels.COLUMN_DISPLAY_NAME, "Test Channel");
        channelValues.put(TvContract.Channels.COLUMN_INPUT_ID, getApplication().getPackageName());
        channelValues.put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_PREVIEW);
        Uri channelUri = getContentResolver().insert(TvContract.Channels.CONTENT_URI, channelValues);

        // Check if the channel was created successfully
        if (channelUri == null) {
            toast("Failed to create channel");
            return;
        }

        long channelId = ContentUris.parseId(channelUri);

        toast("Channel created, id="+channelId+", uri="+channelUri);

        // Now, create a Preview Program on this channel
        ContentValues programValues = new ContentValues();
        programValues.put(TvContract.PreviewPrograms.COLUMN_CHANNEL_ID, channelId);
        programValues.put(TvContract.PreviewPrograms.COLUMN_TITLE, "Test Program " + channelId);
        programValues.put(TvContract.PreviewPrograms.COLUMN_SHORT_DESCRIPTION, "This is a test program");
        programValues.put(TvContract.PreviewPrograms.COLUMN_POSTER_ART_URI, "android.resource://your.package.name/drawable/test_image"); // Replace with your image URI
        programValues.put(TvContract.PreviewPrograms.COLUMN_INTENT_URI, "intent://yourappdetails#Intent;scheme=yourapp;package=your.package.name;end");
        programValues.put(TvContract.PreviewPrograms.COLUMN_TYPE, TvContract.PreviewPrograms.TYPE_MOVIE);
        Uri programUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            programUri = getContentResolver().insert(TvContract.PreviewPrograms.CONTENT_URI, programValues);
        } else {
            Log.e(TAG, "Failed to create channel - SDK too low");
            Toast.makeText(this, "Failed to create channel - SDK too low", Toast.LENGTH_SHORT).show();
        }

        if (programUri != null) {
            long programId = ContentUris.parseId(programUri);
            toast("Preview Program created with id="+programId+", uri="+programUri);

            // Preview program created with ID: 41
            Toast.makeText(this, "Preview program created", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Failed to create preview program");
            Toast.makeText(this, "Failed to create preview program", Toast.LENGTH_SHORT).show();
        }
    }

}