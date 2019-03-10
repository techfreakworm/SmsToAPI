package me.techfreakworm.smstoapi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

public class SmsListener extends BroadcastReceiver {

    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;
    private SharedPreferences preferences;
    private static Activity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {



        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = null;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    String msgBody = null;
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                    }

                    if (msg_from.equals("BTSTRIND")) {

                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("Body", msgBody);
                        jsonBody.put("Time", LocalDateTime.now().toString());


                        sendPostRequest("https://api.techfreakworm.me/trade/v0.1/stocktip", jsonBody, context);

                    }

                    Log.d("Message", msg_from + ":" + msgBody);
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public void sendPostRequest(String url, JSONObject data, Context context) {


        mRequestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, url,
                (response) ->
                {
                    Log.d("Success:", response);
                    TextView view = (TextView)mainActivity.findViewById(R.id.sms_text);
                    view.setText(view.getText()+"\n"+response);
                },
                error ->
                        Log.d("error", error.toString())){

                @Override
                public byte[] getBody() {
                return data.toString().getBytes();
            }

                @Override
                public String getBodyContentType() {
                return "application/json";
            }

        };

        mRequestQueue.add(stringRequest);
    }

    public static void setContext(Activity activity){
        mainActivity = activity;
    }
}