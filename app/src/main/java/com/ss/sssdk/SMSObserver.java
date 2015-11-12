package com.ss.sssdk;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by ljw on 15/11/11.
 */
public class SMSObserver extends ContentObserver {
    private static final String TAG = "SMSObserver";
    public static final Uri CONTENT_URI = Uri.parse("content://sms");
    public static String FILTER_SMS_CODE = "####";
    public static final String FILTER_CEHCKPHONE = "#CHECK#";
    private static final String _ID = "id";
    private static final String TYPE = "type";
    private static final String THREAD_ID = "thread_id";
    private static final String ADDRESS = "address";
    private static final String PERSON_ID = "person";
    private static final String DATE = "date";
    private static final String READ = "read";
    private static final String BODY = "body";
    private static final String PROTOCOL = "protocol";
    private static final int MESSAGE_TYPE_ALL = 0;
    private static final int MESSAGE_TYPE_INBOX = 1;
    private static final int MESSAGE_TYPE_SENT = 2;
    private static final int MESSAGE_TYPE_DRAFT = 3;
    private static final int MESSAGE_TYPE_OUTBOX = 4;
    private static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
    private static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send later
    private static final int PROTOCOL_SMS = 0;//SMS_PROTO
    private static final int PROTOCOL_MMS = 1;//MMS_PROTO

    private static final String[] PROJECTION = new String[]  {
                    _ID,//0
                    TYPE,//1
                    ADDRESS,//2
                    BODY,//3
                    DATE,//4
                    THREAD_ID,//5
                    READ,//6
                    PROTOCOL//7
            };

    private static final String SELECTION = _ID + " > %s and " + TYPE + " = " + MESSAGE_TYPE_INBOX;
    private static final int COLUMN_INDEX_ID = 0;
    private static final int COLUMN_INDEX_TYPE = 1;
    private static final int COLUMN_INDEX_PHONE = 2;
    private static final int COLUMN_INDEX_BODY = 3;
    private static final int COLUMN_INDEX_PROTOCOL = 7;
    private static final int MAX_NUMS = 5;
    private static int MAX_ID = 0;
    private ContentResolver mResolver;
    private Handler mHandler;

    public SMSObserver(ContentResolver contentResolver, Handler handler)    {
        super(handler);
        this.mResolver = contentResolver;
        this.mHandler = handler;
    }


    @Override

    public void onChange(boolean selfChange)    {
        Log.i(TAG, "onChange : " + selfChange + "; " + MAX_ID + "; " + SELECTION);
        super.onChange(selfChange);
        Cursor cursor = mResolver.query(CONTENT_URI, PROJECTION,String.format(SELECTION, MAX_ID), null, "date desc");
        int id, type, protocol;
        String phone, body;
        Message message;
        MessageItem item;
        int iter = 0;
        while (cursor.moveToNext())     {
            id = cursor.getInt(COLUMN_INDEX_ID);
            type = cursor.getInt(COLUMN_INDEX_TYPE);
            phone = cursor.getString(COLUMN_INDEX_PHONE);
            body = cursor.getString(COLUMN_INDEX_BODY);
            protocol = cursor.getInt(COLUMN_INDEX_PROTOCOL);
            if (id > MAX_ID) MAX_ID = id;

            if (protocol == PROTOCOL_SMS && body != null && (body.startsWith(FILTER_SMS_CODE) || body.contains(FILTER_SMS_CODE) || body.startsWith(FILTER_CEHCKPHONE) ))   {
                item = new MessageItem();
                item.setId(id);
                item.setType(type);
                item.setPhone(phone);
                item.setBody(body);
                item.setProtocol(protocol);
                message = new Message();
                message.obj = item;
                mHandler.sendMessage(message);
            }
            if (iter > MAX_NUMS) break;
            iter++;
        }
    }
}
