package org.dynamicsoft.SaveAsMP3;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DownloadManager downloadManager;
    String MusicFileName;
    TextView defaultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultText = (TextView) findViewById(R.id.defaultText);

        if (Build.VERSION.SDK_INT >= 23) {  //Android API 23 read and write permission
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View dialogView = li.inflate(R.layout.enter_file_name_dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Enter a file name");
        alertDialogBuilder.setView(dialogView);
        final EditText userInput = (EditText) dialogView.findViewById(R.id.et_input);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(MusicFileName==""){
                    Toast.makeText(getApplicationContext(), "Enter File Name", Toast.LENGTH_LONG).show();
                }
                else {
                    MusicFileName = userInput.getText().toString() + ".mp3";
                    downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Intent receivedIntent = getIntent();
                    String SharedURL = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);

                    //DOWNLOAD LOGIC

                    Uri Download_Uri = Uri.parse("http://convertmp3.io/fetch/?video=" + SharedURL);
                    DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(true);
                    request.setTitle("Downloading " + MusicFileName);
                    request.setDescription("Downloading " + MusicFileName);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, MusicFileName);
                    long refid;
                    refid = downloadManager.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_LONG).show();
                    defaultText.setText("Tips: You can check download status in notifications.\nFiles are always saved to /sdcard/Music/\nIf app does not work, feel free to send hate to contact@convertmp3.io.");
                }

            }
        }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                        finishAndRemoveTask();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}