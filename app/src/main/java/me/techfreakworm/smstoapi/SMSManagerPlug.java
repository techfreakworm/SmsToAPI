package me.techfreakworm.smstoapi;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

public class SMSManagerPlug {
    private static SMSManagerPlug instance = new SMSManagerPlug();

    private SMSManagerPlug() {

    }

    public static SMSManagerPlug getInstance() {
        return instance;
    }

    public String getSMSFromSender(String senderName, View view) {
        StringBuilder smsBuilder = new StringBuilder();
        HashMap<String, String> sms = new HashMap<>();
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = view.getContext().getContentResolver().query(uri, projection, "address='" + senderName + "'", null, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                sdf.setTimeZone(tz);
                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    String strDate = sdf.format(new Date(longDate));

                    int int_Type = cur.getInt(index_Type);

                    sms.put("From", strAddress);
                    sms.put("Body", strbody);
                    sms.put("datetime", strDate);
//                    smsBuilder.append("[ ");
//                    smsBuilder.append(strAddress + ", ");
//                    smsBuilder.append(intPerson + ", ");
//                    smsBuilder.append(strbody + ", ");
//                    smsBuilder.append(strDate + ", ");
//                    smsBuilder.append(int_Type);
//                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    //cur = null;
                }
            } else {
                sms.put("Error", "No Result");
//                smsBuilder.append("no result!");
            } // end if
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
        // connect to sms app and return latest sms from sender
//        return smsBuilder.toString();
        return new JSONObject(sms).toString();
    }


}
