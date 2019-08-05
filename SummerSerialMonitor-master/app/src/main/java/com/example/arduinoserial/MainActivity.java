package com.example.arduinoserial;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button conn, close, send, start, stop;
    private TextView txView;
    private Spinner baud;
    private CheckBox autoScroll;

    private Physicaloid mPhysicaloid;

    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;

    private double accX, accY, accZ, magX, magY, magZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        conn = (Button)findViewById(R.id.btnConnect);
        close = (Button)findViewById(R.id.btnClose);
        send = (Button)findViewById(R.id.btnSend);
        start = (Button)findViewById(R.id.btnStart);
        stop = (Button)findViewById(R.id.btnStop);

        txView = (TextView)findViewById(R.id.tvResult);

        baud = (Spinner)findViewById(R.id.spBaud);
        autoScroll = (CheckBox)findViewById(R.id.cbAutoscroll);

        mPhysicaloid = new Physicaloid(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer != null){
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(this, "Accelerometer is not supported", Toast.LENGTH_LONG).show();
        }

        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if(magnetometer != null){
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else{
            Toast.makeText(this, "Magnetometer is not supported", Toast.LENGTH_LONG).show();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void onClickOpen(View v) {
        String baudtext = baud.getSelectedItem().toString();
        switch (baudtext) {
            case "300 baud":
                mPhysicaloid.setBaudrate(300);
                break;
            case "1200 baud":
                mPhysicaloid.setBaudrate(1200);
                break;
            case "2400 baud":
                mPhysicaloid.setBaudrate(2400);
                break;
            case "4800 baud":
                mPhysicaloid.setBaudrate(4800);
                break;
            case "9600 baud":
                mPhysicaloid.setBaudrate(9600);
                break;
            case "19200 baud":
                mPhysicaloid.setBaudrate(19200);
                break;
            case "38400 baud":
                mPhysicaloid.setBaudrate(38400);
                break;
            case "576600 baud":
                mPhysicaloid.setBaudrate(576600);
                break;
            case "744880 baud":
                mPhysicaloid.setBaudrate(744880);
                break;
            case "115200 baud":
                mPhysicaloid.setBaudrate(115200);
                break;
            case "230400 baud":
                mPhysicaloid.setBaudrate(230400);
                break;
            case "250000 baud":
                mPhysicaloid.setBaudrate(250000);
                break;
            default:
                mPhysicaloid.setBaudrate(9600);
        }

        if(mPhysicaloid.open()) {
            setEnabledUi(true);

            mPhysicaloid.addReadListener(new ReadLisener() {
                @Override
                public void onRead(int size) {
                    byte[] buf = new byte[size];
                    mPhysicaloid.read(buf, size);
                    tvAppend(txView, Html.fromHtml("<font color=blue>" + new String(buf) + "</font>"));

                }
            });
        } else {
            Toast.makeText(this, "Cannot open", Toast.LENGTH_LONG).show();
        }
    }



    public void onClickClose(View v) {
        if(mPhysicaloid.close()) {
            mPhysicaloid.clearReadListener();
            setEnabledUi(false);
        }
    }

    private void setEnabledUi(boolean on) {
        if(on) {
            conn.setEnabled(false);
            baud.setEnabled(false);
            autoScroll.setEnabled(false);
            close.setEnabled(true);
            send.setEnabled(true);
        } else {
            conn.setEnabled(true);
            baud.setEnabled(true);
            autoScroll.setEnabled(true);
            close.setEnabled(false);
            send.setEnabled(false);
        }
    }

    Handler mHandler = new Handler();
    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accX = sensorEvent.values[0];
            accY = sensorEvent.values[1];
            accZ = sensorEvent.values[2];
            //Log.d("Accelerometer values", "X: " + accX + "   Y: " + accY + "   Z: " + accZ);
        }
        if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magX = sensorEvent.values[0];
            magY = sensorEvent.values[1];
            magZ = sensorEvent.values[2];
            //Log.d("Magnetometer values", "X: " + magX + "   Y: " + magY + "   Z: " + magZ);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
