package me.techfreakworm.smstoapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.time.LocalDateTime;

public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

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

                    if (msg_from.equals("Mayank")) {

                        JSONObject jsonBody = new JSONObject();
                        jsonBody.put("Body", msgBody);
                        jsonBody.put("Time", LocalDateTime.now().toString());


                        sendPostRequest("http://192.168.1.4:1000/trade/v0.1/stocktip", jsonBody, context);
                    }

                    Log.d("Message", new String(msg_from + ":" + msgBody));
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public void sendPostRequest(String url, JSONObject data, Context context) {
        RequestQueue mRequestQueue;
        StringRequest stringRequest;

        mRequestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Success:", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        mRequestQueue.add(stringRequest);
    }
}