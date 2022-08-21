package com.dfrobot.angelo.blunobasicdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import io.github.controlwear.virtual.joystick.android.JoystickView;
import java.util.List;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonSerialSend;
	private ToggleButton buttonMode;
	private ToggleButton buttonDebug;
	private ToggleButton buttonLight;
	private EditText serialSendText;
	private TextView serialReceivedText;

	private JoystickView joystickLeft;
	private TextView mTextViewAngleLeft;
	private TextView mTextViewStrengthLeft;
	private TextView mTextViewCoordinateLeft;

	private JoystickView joystickRight;
	private TextView mTextViewAngleRight;
	private TextView mTextViewStrengthRight;
	private TextView mTextViewCoordinateRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		request(1000, new OnPermissionsResult() {
			@Override
			public void OnSuccess() {
				Toast.makeText(MainActivity.this,"123456",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void OnFail(List<String> noPermissions) {
				Toast.makeText(MainActivity.this,"987654",Toast.LENGTH_SHORT).show();
			}
		});

		mTextViewAngleLeft = (TextView) findViewById(R.id.textView_angle_left);
		mTextViewStrengthLeft = (TextView) findViewById(R.id.textView_strength_left);
		mTextViewCoordinateLeft = findViewById(R.id.textView_coordinate_left);

		joystickLeft = (JoystickView) findViewById(R.id.joystickLeft);
		joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				mTextViewAngleLeft.setText(angle + "°");
				mTextViewStrengthLeft.setText(strength + "%");
				mTextViewCoordinateLeft.setText( String.format("0x%02X:0x%02X", Math.round(Math.floor(joystickLeft.getNormalizedX()*0xFF/100))&0xFF, Math.round(Math.floor(joystickLeft.getNormalizedY()*0xFF/100))&0xFF ));

				sendJoystickState();
			}
		});

		mTextViewAngleRight = (TextView) findViewById(R.id.textView_angle_right);
		mTextViewStrengthRight = (TextView) findViewById(R.id.textView_strength_right);
		mTextViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

		joystickRight = (JoystickView) findViewById(R.id.joystickRight);
		joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
			//@SuppressLint("DefaultLocale")
			@Override
			public void onMove(int angle, int strength) {
				mTextViewAngleRight.setText(angle + "°");
				mTextViewStrengthRight.setText(strength + "%");
				mTextViewCoordinateRight.setText( String.format("0x%02X:0x%02X", Math.round(Math.floor(joystickRight.getNormalizedX()*0xFF/100))&0xFF, Math.round(Math.floor(joystickRight.getNormalizedY()*0xFF/100))&0xFF ));

				sendJoystickState();
			}
		});

        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
        buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
			}
		});

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
			}
		});

		buttonLight = (ToggleButton) findViewById(R.id.buttonLight);
		buttonLight.setOnCheckedChangeListener((view,isChecked) -> {
				boolean enable = isChecked;
				sendJoystickState(enable, !enable);
				sendJoystickState(enable, !enable);
				sendJoystickState(enable, !enable);
				sendJoystickState(enable, !enable);
		});

		buttonDebug = (ToggleButton) findViewById(R.id.buttonDebug);
		buttonDebug.setOnCheckedChangeListener((view, isChecked) -> {
				byte a = (byte)(isChecked ? 0x01 : 0x01);
				byte b = (byte)(isChecked ? 0x10 : 0x20);
				sendJoystickState(false, false, a, b);
		});

		buttonMode = (ToggleButton) findViewById(R.id.buttonMode);
		buttonMode.setOnCheckedChangeListener((view, isChecked) -> {

			byte a = (byte)(isChecked ? 0x04 : 0x01);
			byte b = (byte)(isChecked ? 0x07 : 0x07);
			sendJoystickState(false, false, a, b);
		});
	}

	private static byte _pid = 0;
	private void sendJoystickState() { this.sendJoystickState(false, false, (byte)0x00, (byte)0x00);}
	private void sendJoystickState(boolean addLight, boolean subLight) { this.sendJoystickState(addLight, subLight, (byte)0x00, (byte)0x00); }
	private void sendJoystickState(boolean addLight, boolean subLight, byte a, byte b) {
		final byte buf[] = new byte[10];	// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L9
		buf[0] = (byte)0xAA;		// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L10
		buf[1] = (byte)0x00;		// right joystick, vertical coordinates
		buf[2] = (byte)0x00;		// right joystick, horizontal coordinates
		buf[3] = (byte)0x00;		// left joystick, vertical coordinates
		buf[4] = (byte)0x00;		// left joystick, horizontal coordinates
		buf[5] = _pid++;			// pid of the message
		buf[6] = (byte)0x00;		// buttons bit 00-08
		buf[7] = (byte)0x00;		// buttons bit 08-16
		buf[8] = (byte)0x00;		// buttons bit 16-24
		buf[9] = (byte)0xBB;		// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L11

		buf[1] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickRight.getNormalizedY())*0xFF/100))&0xFF);
		buf[2] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickRight.getNormalizedX())*0xFF/100))&0xFF);
		buf[3] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickLeft.getNormalizedY())*0xFF/100))&0xFF);
		buf[4] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickLeft.getNormalizedX())*0xFF/100))&0xFF);

		buf[1] = Math.abs(buf[1] - 0x7F) <= 4 ? 0x7F : buf[1];
		buf[2] = Math.abs(buf[2] - 0x7F) <= 4 ? 0x7F : buf[2];
		buf[3] = Math.abs(buf[3] - 0x7F) <= 4 ? 0x7F : buf[3];
		buf[4] = Math.abs(buf[4] - 0x7F) <= 4 ? 0x7F : buf[4];

		if(addLight) buf[7] = (byte)(buf[7]+0x40);
		if(subLight) buf[7] = (byte)(buf[7]+0x80);
		buf[7] = (a!=0) ? a : buf[7];
		buf[8] = (b!=0) ? b : buf[8];

		if(mConnected) serialSend(buf);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				serialSend(buf);
			}
		}, 30);
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}

		this.joystickLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.joystickRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		this.mTextViewAngleLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.mTextViewStrengthLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.mTextViewCoordinateLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		this.mTextViewAngleRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.mTextViewStrengthRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.mTextViewCoordinateRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		this.buttonDebug.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonLight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonMode.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonScan.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub
		serialReceivedText.append(theString);							//append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
	}

}