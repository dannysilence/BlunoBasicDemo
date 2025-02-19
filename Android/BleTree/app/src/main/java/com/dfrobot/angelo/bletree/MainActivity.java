package com.dfrobot.angelo.bletree;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonUp;
	private Button buttonLeft;
	private Button buttonDown;
	private Button buttonRight;
	private Button buttonSerialSend;

	private ToggleButton buttonLogs;
	private ToggleButton buttonMode;
	private ToggleButton buttonSettings;
	private ToggleButton buttonPump;

	private CircularProgressIndicator humidityLevel;
	private TextView humidityLevelValue;
	private TextView humidityLevelLabel;

	private CircularProgressIndicator soilHumidityLevel;
	private TextView temperatureLevelValue;
	private TextView temperatureLevelLabel;

	private CircularProgressIndicator temperatureLevel;
	private TextView soilHumidityLevelValue;
	private TextView soilHumidityLevelLabel;

	private EditText serialSendText;
	private TextView serialReceivedText;

	private TextView mTextViewAngleLeft;
	private TextView mTextViewStrengthLeft;
	private TextView mTextViewCoordinateLeft;

	private ScrollView logsView;

	private TextView mTextViewAngleRight;
	private TextView mTextViewStrengthRight;
	private TextView mTextViewCoordinateRight;

	private byte _btnByteA = 0;
	private byte _btnByteB = 0;

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

		mTextViewAngleLeft = findViewById(R.id.textView_angle_left);
		mTextViewStrengthLeft = findViewById(R.id.textView_strength_left);
		mTextViewCoordinateLeft = findViewById(R.id.textView_coordinate_left);


		mTextViewAngleRight = findViewById(R.id.textView_angle_right);
		mTextViewStrengthRight = findViewById(R.id.textView_strength_right);
		mTextViewCoordinateRight = findViewById(R.id.textView_coordinate_right);

		onCreateProcess();														//onCreate Process by BlunoLibrary

		//new getSupportActionBar().
		//getActionBar().hide();

		serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

		serialReceivedText= findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
		serialSendText= findViewById(R.id.serialSendText);			//initial the EditText of the sending data

		buttonSerialSend = findViewById(R.id.buttonSerialSend);		//initial the button for sending the data
		buttonSerialSend.setOnClickListener(v -> {
			serialSend(serialSendText.getText().toString());				//send the data to the BLUNO
		});

		buttonScan = findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
		buttonScan.setOnClickListener(v -> {
			buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
		});

		buttonSettings = findViewById(R.id.buttonSettings);
		buttonSettings.setOnCheckedChangeListener((view, isChecked) -> {
			int x = isChecked
					? R.drawable.ic_settings_dark_grey_24dp
					: R.drawable.ic_settings_grey_24dp;

			buttonSettings.setBackgroundResource(x);
		});



		buttonMode = findViewById(R.id.buttonMode);
		buttonMode.setOnCheckedChangeListener((view, isChecked) -> {

			byte a = (byte)(isChecked ? 0x01 : 0x01);
			byte b = (byte)(isChecked ? 0x0B : 0x0B);
			sendButtonClick(a, b);

			int x = isChecked
					? R.drawable.ic_schedule_dark_grey_24dp
					: R.drawable.ic_schedule_grey_24dp;

			buttonMode.setBackgroundResource(x);
		});

		buttonLogs = findViewById(R.id.buttonLogs);
		buttonLogs.setOnCheckedChangeListener((view, isChecked) -> {
			int x = isChecked
					? R.drawable.ic_logs_dark_grey_24dp
					: R.drawable.ic_logs_grey_24dp;

			buttonLogs.setBackgroundResource(x);
			logsView.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
		});
//
//		buttonUp = findViewById(R.id.buttonUp);
//		buttonUp.setOnClickListener(v -> {
//			sendButtonClick((byte)0x00, (byte)0x10);
//		});
//
//		buttonLeft = findViewById(R.id.buttonLeft);
//		buttonLeft.setOnClickListener(v -> {
//			sendButtonClick((byte)0x00, (byte)0x2B);
//		});
//
//		buttonRight = findViewById(R.id.buttonRight);
//		buttonRight.setOnClickListener(v -> {
//			sendButtonClick((byte)0x00, (byte)0x4B);
//		});
//
//		buttonDown = findViewById(R.id.buttonDown);
//		buttonDown.setOnClickListener(v -> {
//			sendButtonClick((byte)0x00, (byte)0x80);
//		});

		buttonPump = findViewById(R.id.buttonPump);
		buttonPump.setOnCheckedChangeListener((view, isChecked) -> {
			byte a = (byte)(isChecked ? 0x04 : 0x04);
			byte b = (byte)(isChecked ? 0x0B : 0x0B);
			sendButtonClick(a, b);

			int x = isChecked
					? R.drawable.ic_water_blue_24dp
					: R.drawable.ic_water_grey_24dp;

			buttonPump.setBackgroundResource(x);
//			logsView.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
		});

		humidityLevel = findViewById(R.id.humidityLevel);
		humidityLevelLabel = findViewById(R.id.humidityLevelLabel);
		humidityLevelValue = findViewById(R.id.humidityLevelValue);
		humidityLevel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		humidityLevelLabel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		humidityLevelValue.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		temperatureLevel = findViewById(R.id.temperatureLevel);
		temperatureLevelValue = findViewById(R.id.temperatureLevelValue);
		temperatureLevelLabel = findViewById(R.id.temperatureLevelLabel);
		temperatureLevel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		temperatureLevelValue.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		temperatureLevelLabel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		soilHumidityLevel = findViewById(R.id.soilHumidityLevel);
		soilHumidityLevelLabel = findViewById(R.id.soilHumidityLabel);
		soilHumidityLevelValue = findViewById(R.id.soilHumidityLevelValue);
		soilHumidityLevel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		soilHumidityLevelLabel.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		soilHumidityLevelValue.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		logsView = findViewById(R.id.logsView);
		logsView.setVisibility(buttonLogs.isChecked() && mConnected ? View.VISIBLE : View.INVISIBLE);

		tryAutoConnect();
	}

	private void sendButtonClick(byte valA, byte valB) {
		_btnByteA = (byte) (_btnByteA | valA);
		_btnByteB = (byte) (_btnByteB | valB);

		sendJoystickState();

		final Handler handler = new Handler();
		handler.postDelayed(() -> {
			_btnByteA = (byte) (((byte)(_btnByteA & valA) == valA) ? _btnByteA - valA : _btnByteA);
			_btnByteB = (byte) (((byte)(_btnByteB & valB) == valB) ? _btnByteB - valB : _btnByteB);

			sendJoystickState();
		}, 500);
	}

	private static byte _pid = 0;
	private void sendJoystickState() {
		final byte buf[] = new byte[20];	// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L9
		buf[0] = (byte)0xAA;		// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L10
		//buf[1] = (byte)0xBB;
		buf[1] = (byte)0x00;		// right joystick, vertical coordinates
		buf[2] = (byte)0x00;		// right joystick, horizontal coordinates
		buf[3] = (byte)0x00;		// left joystick, vertical coordinates
		buf[4] = (byte)0x00;		// left joystick, horizontal coordinates
		buf[5] = _pid++;			// pid of the message
		buf[6] = (byte)0x00;		// buttons bit 00-08
		buf[7] = (byte)0x00;		// buttons bit 08-16
		buf[8] = (byte)0x00;		// buttons bit 16-24
		buf[9] = (byte)0xBB;
		//buf[11] = (byte)0xAA;// https://github.com/dannysilence/a-robot/blob/main/BleVehicle.ino#L11

//		buf[1] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickRight.getNormalizedY())*0xFF/100))&0xFF);
//		buf[2] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickRight.getNormalizedX())*0xFF/100))&0xFF);
//		buf[3] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickLeft.getNormalizedY())*0xFF/100))&0xFF);
//		buf[4] = (byte)(Math.round(Math.floor(Math.abs(100-this.joystickLeft.getNormalizedX())*0xFF/100))&0xFF);

		buf[1] = Math.abs(buf[1] - 0x7F) <= 4 ? 0x7F : buf[1];
		buf[2] = Math.abs(buf[2] - 0x7F) <= 4 ? 0x7F : buf[2];
		buf[3] = Math.abs(buf[3] - 0x7F) <= 4 ? 0x7F : buf[3];
		buf[4] = Math.abs(buf[4] - 0x7F) <= 4 ? 0x7F : buf[4];

		buf[7] = _btnByteA;
		buf[8] = _btnByteB;

		if(mConnected) serialSend(buf);
		final Handler handler = new Handler();
		handler.postDelayed(() -> serialSend(buf), 30);
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
				//buttonScan.setText("Connected");
				buttonScan.setBackgroundResource(R.drawable.ic_bluetooth_blue_24dp);
				break;
			case isConnecting:
				buttonScan.setBackgroundResource(R.drawable.ic_bluetooth_green_24dp);
				break;
			case isToScan:
				buttonScan.setBackgroundResource(R.drawable.ic_bluetooth_dark_grey_24dp);
				break;
			case isScanning:
				//buttonScan.setText("Scanning");
				break;
			case isDisconnecting:


				break;
			default:
				break;
		}

//		this.joystickLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.joystickRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

//		this.mTextViewAngleLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.mTextViewStrengthLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.mTextViewCoordinateLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//
//		this.mTextViewAngleRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.mTextViewStrengthRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.mTextViewCoordinateRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);

		this.buttonSettings.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonPump.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonMode.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.buttonLogs.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
		this.logsView.setVisibility(this.buttonLogs.isChecked() && mConnected ? View.VISIBLE : View.INVISIBLE);

		if(mConnected) {
			this.humidityLevel.show();
			this.temperatureLevel.show();
			this.soilHumidityLevel.show();

			humidityLevelLabel.setVisibility(View.VISIBLE);
			humidityLevelValue.setVisibility(View.VISIBLE);
			temperatureLevelLabel.setVisibility(View.VISIBLE);
			temperatureLevelValue.setVisibility(View.VISIBLE);
			soilHumidityLevelLabel.setVisibility(View.VISIBLE);
			soilHumidityLevelValue.setVisibility(View.VISIBLE);
		} else {
			this.humidityLevel.hide();
			this.temperatureLevel.hide();
			this.soilHumidityLevel.hide();

			humidityLevelLabel.setVisibility(View.INVISIBLE);
			humidityLevelValue.setVisibility(View.INVISIBLE);
			temperatureLevelLabel.setVisibility(View.INVISIBLE);
			temperatureLevelValue.setVisibility(View.INVISIBLE);
			soilHumidityLevelLabel.setVisibility(View.INVISIBLE);
			soilHumidityLevelValue.setVisibility(View.INVISIBLE);
		}

//		this.buttonUp.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.buttonDown.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.buttonLeft.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
//		this.buttonRight.setVisibility(mConnected ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
//		String start = String.copyValueOf(new char[] {0xAA});
//		String end = String.copyValueOf(new char[] {0xBB});
		//if(theString.startsWith(start) && theString.endsWith(end))
		//{
		serialReceivedText.append(theString);                            //append the text into the EditText
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		try {
			((ScrollView) serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);

			if(theString.contains("AH:")) {
				String p = theString.substring(theString.indexOf("AH:")+"AH:".length()).trim();
				p = p.substring(0, p.indexOf(","));

				int v = new Integer(p);
				humidityLevel.setProgress(v);
				humidityLevelValue.setText(v+"%");
			}

			if(theString.contains("SH:")) {
				String p = theString.substring(theString.indexOf("SH:")+"SH:".length()).trim();
				p = p.substring(0, p.indexOf(","));

				int v = new Integer(p) / 10;
				soilHumidityLevel.setProgress(v);
				soilHumidityLevelValue.setText(v+"%");
			}

			if(theString.contains("AT:")) {
				String p = theString.substring(theString.indexOf("AT:")+"AT:".length()).trim();
				p = p.substring(0, p.indexOf(","));

				int v = new Integer(p);
				temperatureLevel.setProgress(v);
				temperatureLevelValue.setText(v+"°");
			}
		}catch(Exception e) {}
		//}
	}

}