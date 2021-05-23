/***********************************************************************************
 * /org/dynamicsoft/MainActivity.java: The MainActivity for SaveAsMP3
 ***********************************************************************************
 * MIT License
 *
 * Copyright (c) 2019 Shouko Komi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **********************************************************************************/

package org.dynamicsoft.SaveAsMP3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private DownloadManager downloadManager;
    String MusicFileName;
    TextView defaultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultText = findViewById(R.id.defaultText);

        if (Build.VERSION.SDK_INT >= 23) {  //Android API 23 read and write permission
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        @SuppressLint("InflateParams") View dialogView = li.inflate(R.layout.enter_file_name_dialog_box, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Enter a file name");
        alertDialogBuilder.setView(dialogView);
        final EditText userInput = dialogView.findViewById(R.id.et_input);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (MusicFileName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter File Name", Toast.LENGTH_LONG).show();
                } else {
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
