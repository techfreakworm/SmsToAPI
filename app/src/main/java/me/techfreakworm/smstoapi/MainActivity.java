package me.techfreakworm.smstoapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, 2);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS}, 2);
        }


        final SMSManagerPlug smsManagerPlug = SMSManagerPlug.getInstance();

        final TextView textView = findViewById(R.id.sms_text);

        final EditText textEdit = findViewById(R.id.sms_sender_name);

        final Button getSMSButton = findViewById(R.id.get_sms_button);
        getSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sender = textEdit.getText().toString();
                textView.setText(smsManagerPlug.getSMSFromSender(sender, v));

//                    CallAPI sendSmsAPI = new CallAPI();
//                    sendSmsAPI.doInBackground("http://192.168.1.4:1000/trade/v0.1/stocktip","Hello");
            }
        });
    }


}
