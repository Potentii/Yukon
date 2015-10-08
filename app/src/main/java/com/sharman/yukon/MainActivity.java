package com.sharman.yukon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

<<<<<<< HEAD
import com.sharman.yukon.model.Exam;
=======
import com.sharman.yukon.view.activities.ExamCreateConfirmActivity;
import com.sharman.yukon.view.activities.GoogleConnectActivity;
>>>>>>> integration

public class MainActivity extends GoogleConnectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.addQuestionsBtn);
    }

    public void callActivityExam(View view) {

<<<<<<< HEAD
        Intent iCallActivityExam = new Intent(this, ExamCreateActivity.class);
        startActivity(iCallActivityExam);
        finish();
=======
        Button btn = (Button)findViewById(R.id.btVerProva);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Hello world", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ExamCreateConfirmActivity.class);
                startActivity(intent);
                finish();
            }
        });
>>>>>>> integration
    }
}
