package fi.jamk.lineardistancemeasurement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Physicaloid mPhysicaloid;

    private final String TAG = "XY status";

    private double accX, accY, accZ, gyroX, gyroY, gyroZ, magX, magY, magZ, linAccX, linAccY, linAccZ;

    private double distance;

    private double timeStart, timeStartSek, timeStop, time;

    private SensorManager sensorManager;
    Sensor accelerometer, gyroscope, magneto, linearAcc;

    TextView status, dist, tvAccX, tvAccY, tvAccZ, tvLinAccX, tvLinAccY, tvLinAccZ, tvGyrX, tvGyrY, tvGyrZ, tvMagX, tvMagY, tvMagZ, tvStartTime, tvEndTime, tvTimeDifference, tvXY;
    Button btnStart, btnStop;

    private boolean scanRunning;

    private CountDownTimer countDownTimer;

    private double[] xInitialVelocity;
    private double[] xFinalVelocity;
    private double[] yInitialVelocity;
    private double[] yFinalVelocity;

    int i = 0;

    private double x, y, v0x, v0y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanRunning = true;

        status = (TextView) findViewById(R.id.tvStatus);
        dist = (TextView) findViewById(R.id.tvDistance);
        tvAccX = (TextView) findViewById(R.id.tvAccX);
        tvAccY = (TextView) findViewById(R.id.tvAccY);
        tvAccZ = (TextView) findViewById(R.id.tvAccZ);
        tvLinAccX = (TextView) findViewById(R.id.tvLinAccX);
        tvLinAccY = (TextView) findViewById(R.id.tvLinAccY);
        tvLinAccZ = (TextView) findViewById(R.id.tvLinAccZ);
        tvGyrX = (TextView) findViewById(R.id.tvGyrX);
        tvGyrY = (TextView) findViewById(R.id.tvGyrY);
        tvGyrZ = (TextView) findViewById(R.id.tvGyrZ);
        tvMagX = (TextView) findViewById(R.id.tvMagX);
        tvMagY = (TextView) findViewById(R.id.tvMagY);
        tvMagZ = (TextView) findViewById(R.id.tvMagZ);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvTimeDifference = (TextView) findViewById(R.id.tvTimeDifference);
        tvXY = (TextView) findViewById(R.id.tvXY);

        btnStart = (Button) findViewById(R.id.btnStart);
        //btnStop = (Button) findViewById(R.id.btnStop);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Accelerometer is registered");
        } else {
            status.setText("Accelerometer is not supported");
            Log.i(TAG, "Acceleromater is not supported");
        }

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Gyroscope is registered");
        } else {
            status.append("Gyroscope is not supported");
            Log.i(TAG, "Gyroscope is not supported");
        }

        magneto = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (magneto != null) {
            sensorManager.registerListener(this, magneto, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Magnetometer is registered");
        } else {
            status.append("\nMagnetometer is not supported");
            Log.i(TAG, "Magnetometer is not supported");
        }

        linearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(linearAcc != null){
            sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_NORMAL);
            Log.i(TAG, "Linear acceleration is registered");
        } else{
            status.append("\nLinear acceleration is not supported");
            Log.i(TAG, "Linear acceleration is not supported");
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeStart = System.currentTimeMillis();
                timeStartSek = timeStart / 1000;

                x = 0;
                y = 0;
                v0x = 0;
                v0y = 0;

                time = 0;

                if(scanRunning){
                    stopScan();
                } else{
                    startScan();
                }

                tvStartTime.setText("Start time: " + String.format("%.5f", timeStart) + "ms   " + String.format("%.5f", timeStartSek) + "s");
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

            tvAccX.setText("AccX: " + String.valueOf(accX));
            tvAccY.setText("AccY: " + String.valueOf(accY));
            tvAccZ.setText("AccZ: " + String.valueOf(accZ));

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gyroX = sensorEvent.values[0];
            gyroY = sensorEvent.values[1];
            gyroZ = sensorEvent.values[2];

            tvGyrX.setText("GyroX: " + String.valueOf(gyroX));
            tvGyrY.setText("GyroY: " + String.valueOf(gyroY));
            tvGyrZ.setText("GyroZ: " + String.valueOf(gyroZ));
        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magX = sensorEvent.values[0];
            magY = sensorEvent.values[1];
            magZ = sensorEvent.values[2];

            tvMagX.setText("MagX: " + String.valueOf(magX));
            tvMagY.setText("MagY: " + String.valueOf(magY));
            tvMagZ.setText("MagZ: " + String.valueOf(magZ));
        } else {
            linAccX = sensorEvent.values[0];
            linAccY = sensorEvent.values[1];
            linAccZ = sensorEvent.values[2];

            tvLinAccX.setText("LinX: " + String.valueOf(linAccX));
            tvLinAccY.setText("LinY: " + String.valueOf(linAccY));
            tvLinAccZ.setText("LinZ: " + String.valueOf(linAccZ));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void stopScan(){
        countDownTimer = new CountDownTimer(6000, 100) {
            @Override
            public void onTick(long l) {

                timeStop = System.currentTimeMillis();
                double timeMil = timeStop - timeStart;
                double timeSec = timeMil / 1000;

                tvEndTime.setText("End time: " + String.format("%.5f", timeStop/1000));
                tvTimeDifference.setText("Interval: " + String.format("%.5f", timeSec));



                x += v0x * timeSec + (linAccX * timeSec * time / 2);
                y += v0y * timeSec + (linAccY * timeSec * time / 2);



                Log.i(TAG, "Reading (X,Y): (" + String.format("%.5f", (x*1000)) + "," + String.format("%.5f", (y*1000)) + "); " +
                        "Time: " + String.format("%.5f", time) + " s; ax: " + String.format("%.5f", linAccX) + "m/s^2; ay: " + String.format("%.5f", linAccY) + "m/s^2; Vx: " +
                        String.format("%.5f", v0x) + " m/s; Vy: " + String.format("%.5f", v0y) + " m/s");

                //String text1 = "<font color='red'>X: </font><font color='black'>" + String.format("%.5f", (s1*1000))+" mm, </font><font color='red'>Y: </font><font color='black'>" + String.format("%.5f", (s2*1000)) + " mm</font>";
                //String text2 = "\n<font color='red'>X: </font><font color='black'>" + String.format("%.5f", (s1*1000))+" mm, </font><font color='red'>Y: </font><font color='black'>" + String.format("%.5f", (s2*1000)) + " mm</font>";




                if(TextUtils.isEmpty(tvXY.getText())){
                    tvXY.setText("X: " + String.format("%.5f", (x*1000))+" mm, Y: " + String.format("%.5f", (y*1000)) + " mm");
                }else{
                    tvXY.append("\nX: " + String.format("%.5f", (x*1000))+" mm, Y: " + String.format("%.5f", (y*1000)) + " mm");
                }

                tvXY.setMovementMethod(new ScrollingMovementMethod());

                time = 0.1;
                v0x += linAccX * time;
                v0y += linAccY * time;
            }

            @Override
            public void onFinish() {
                scanRunning = false;
                btnStart.setText("start");
                btnStart.setBackgroundColor(Color.GREEN);
            }
        }.start();

        btnStart.setText("STOP");
        btnStart.setBackgroundColor(Color.RED);
        scanRunning = true;
    }

    private void startScan(){
        countDownTimer.cancel();
        scanRunning = false;
        btnStart.setText("start");
        btnStart.setBackgroundColor(Color.GREEN);
    }
}
