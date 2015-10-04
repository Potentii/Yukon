package com.sharman.yukon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;

public class MainActivity extends GoogleConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.btVerProva);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Ola Mundo", Toast.LENGTH_SHORT).show();

                DriveFileOffline driveFileOffline = new DriveFileOffline(googleApiClient);
                driveFileOffline.upload(
                        Drive.DriveApi.getRootFolder(googleApiClient),
                        "Yukon Test File",
                        "Yukon Test Content",
                        EMimeType.JSON.getMimeType(),
                        new UploadCallback() {
                            @Override
                            public void onComplete(DriveId driveId) {
                                Toast.makeText(getApplicationContext(), "File uploaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(String errorMessage) {
                                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}
