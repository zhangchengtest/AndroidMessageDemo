package demo.msg;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends Activity {
	Button btnSend;
	TextView edtPhoneNo;
	TextView edtContent;
	MsgReceiver msgRev;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        //注册Receiver
		msgRev = new MsgReceiver();
		registerReceiver(msgRev, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
		
		// 发送短信
		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new Button.OnClickListener() {
			
			public void onClick(View v) {
				edtPhoneNo = (TextView) findViewById(R.id.edtPhoneNo);
				edtContent = (TextView) findViewById(R.id.edtContent);
				String phoneNo = edtPhoneNo.getText().toString();
				String message = edtContent.getText().toString();
				if (phoneNo.length() > 0 && message.length() > 0) {
					// 发送短信
					sendSMS(phoneNo, message);
				} else
					Toast.makeText(getBaseContext(),
							"Please enter both phone number and message.",
							Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        // 注销Receiver
    	unregisterReceiver(msgRev);
    }
	
    //接收来自Receiver的消息
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    	Log.v("MY_TAG","==================\n onNewIntent");
        Bundle bundle = intent.getExtras();
        String smsNumber = bundle.getString("phone");
        String smsBody = bundle.getString("content");
        
        Log.v("MY_TAG", "SMS Number is:\n    " + smsNumber);
        Log.v("MY_TAG", "SMS Body is:\n    " + smsBody);
        
        String str="";
        str = "Receive message from\n"+smsNumber+":\n"+smsBody;
        ((TextView) findViewById(R.id.textView1)).setText(str);
    }
    
	/*
	 * 短信发送
	 */
	private void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";

		// create the sentIntent parameter
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,0);

		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,deliverIntent, 0);

		// register the Broadcast Receivers
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(),
							"SMS sent success actions", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(),
							"SMS generic failure actions", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(),
							"SMS radio off failure actions", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(),
							"SMS null PDU failure actions", Toast.LENGTH_SHORT)
							.show();
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				Toast.makeText(getBaseContext(), "SMS delivered actions",
						Toast.LENGTH_SHORT).show();
			}
		}, new IntentFilter(DELIVERED_SMS_ACTION));

		// if message's length more than 70 ,
		// then call divideMessage to dive message into several part ,and call
		// sendTextMessage()
		// else direct call sendTextMessage()
		if (message.length() > 70) {
			ArrayList<String> msgs = sms.divideMessage(message);
			for (String msg : msgs) {
				sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
			}
		} else {
			sms.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
		}

	}
}