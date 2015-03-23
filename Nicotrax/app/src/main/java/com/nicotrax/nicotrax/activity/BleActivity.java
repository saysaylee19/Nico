package com.nicotrax.nicotrax.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.nicotrax.nicotrax.ble.*;
import com.nicotrax.nicotrax.R;

public class BleActivity extends ActionBarActivity {

    //From BLE Tutorial (http://toastdroid.com/2014/09/22/android-bluetooth-low-energy-tutorial/)
    static final int REQUEST_ENABLE_BT = 1; //request code for activity result

    //Variables to limit scanning
    //private static final long SCAN_PERIOD = 10000;
    //private Handler mHandler;

    private static BleActivity mThis;
    private BluetoothGatt mBtGatt;

    //private boolean mBtAdapterEnabled = false;
    private boolean mBleSupported = true;
    private boolean mScanning = false;
    private int mNumDevs = 0;
    //private int mConnIndex = NO_DEVICE;
    private List<BleDeviceInfo> mDeviceInfoList;
    private static BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBtAdapter = null;
    //private BluetoothDevice mBluetoothDevice = null;
    private BluetoothLeService mBluetoothLeService = null;
    private IntentFilter mFilter;
    private String[] mDeviceFilter = null;
    private List<Sensor> mEnabledSensors = new ArrayList<Sensor>();
    private static final int GATT_TIMEOUT = 250; // milliseconds

    private TextView connectionDialog;
    private TextView btleServicesCount;
    private TextView btleIrCharacteristic;

    private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        connectionDialog = (TextView)findViewById(R.id.btle_connection_dialog);
        btleServicesCount = (TextView)findViewById(R.id.btle_services_count);
        btleIrCharacteristic = (TextView)findViewById(R.id.btle_ir_characteristic);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG)
                    .show();
            mBleSupported = false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to BluetoothAdapter through BluetoothManager.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
        }

        // Register the BroadcastReceiver  --  Used in TI Sample App to communicate w/ service
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        mFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_READ);
        mFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        registerReceiver(mReceiver, mFilter);

        /* From BLE Tutorial - if bluetooth is not on, open a dialog for user to activate it*/
        if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);  //the returned result needs to be caught in an onActivityResult() method
        }

        mThis = this;  //Unsure as to why the TI Example uses this
        startBluetoothLeService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ble, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        // Log.e(TAG,"onDestroy");
        super.onDestroy();
        if (mBluetoothLeService != null) {
            if (mScanning)
                scanLeDevice(false);
            unregisterReceiver(mReceiver);
            unbindService(mServiceConnection);
            mBluetoothLeService.close();
            mBluetoothLeService = null;
        }

        mBtAdapter = null;

        //Need to understand the purpose and effect of Clearing Cache

        // Clear cache
        /*File cache = getCacheDir();

        String path = cache.getPath();
        try {
            Runtime.getRuntime().exec(String.format("rm -rf %s", path));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }

    void setError(String txt) {
        Toast.makeText(this, "Turning BT adapter off and on again may fix Android BLE stack problems", Toast.LENGTH_LONG).show();
    }

    boolean checkDeviceFilter(String deviceName) {
        if (deviceName == null)
            return false;

        int n = 0;
        Resources res = getResources();
        mDeviceFilter = res.getStringArray(R.array.device_filter);

        if(mDeviceFilter != null) {
            n = mDeviceFilter.length;
        }

        if (n > 0) {
            boolean found = false;
            for (int i = 0; i < n && !found; i++) {
                found = deviceName.equals(mDeviceFilter[i]);
            }
            return found;
        } else
            // Allow all devices if the device filter is empty
            return true;
    }

    private boolean deviceInfoExists(String address) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(address)) {
                return true;
            }
        }
        return false;
    }

    private BleDeviceInfo createDeviceInfo(BluetoothDevice device, int rssi) {
        BleDeviceInfo deviceInfo = new BleDeviceInfo(device, rssi);

        return deviceInfo;
    }

    private void addDevice(BleDeviceInfo device) {
        mNumDevs++;
        mDeviceInfoList.add(device);

        /* Do I want to import the mScanView UI flow? */

        /*
        mScanView.notifyDataSetChanged();
        if (mNumDevs > 1)
            mScanView.setStatus(mNumDevs + " devices");
        else
            mScanView.setStatus("1 device");
        */
    }

    private BleDeviceInfo findDeviceInfo(BluetoothDevice device) {
        for (int i = 0; i < mDeviceInfoList.size(); i++) {
            if (mDeviceInfoList.get(i).getBluetoothDevice().getAddress()
                    .equals(device.getAddress())) {
                return mDeviceInfoList.get(i);
            }
        }
        return null;
    }

    private void startBluetoothLeService() {
        boolean f;

        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        //startService(bindIntent);  !! Uncomment this to keep the service running in the background !!
        f = bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!f) {
            Toast.makeText(this, "Bind to BluetoothLeService failed", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startScan() {
        // Start device discovery
        if (mBleSupported) {
            mNumDevs = 0;
            //mDeviceInfoList.clear();
            //mScanView.notifyDataSetChanged();
            scanLeDevice(true);
            //mScanView.updateGui(mScanning);
            if (!mScanning) {
                setError("Device discovery start failed");
                //setBusy(false);
            }
        } else {
            setError("BLE not supported on this device");
        }

    }

    private void stopScan() {
        mScanning = false;
        //mScanView.updateGui(false);
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            /*mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(leScanCallback);

                }
            }, SCAN_PERIOD);*/

            mScanning = true;
            mBtAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(leScanCallback);
        }
    }

    private void updateSensorList() {
        mEnabledSensors.clear();

        for (int i = 0; i < Sensor.SENSOR_LIST.length; i++) {
            Sensor sensor = Sensor.SENSOR_LIST[i];
            mEnabledSensors.add(sensor);
        }
    }

    private void enableSensors(boolean f) {
        Log.i("tag", "Enabling Sensors. ~~~~~~~~~~~~~~~~");

        final boolean enable = f;

        for (Sensor sensor : mEnabledSensors) {
            UUID servUuid = sensor.getService();
            UUID confUuid = sensor.getConfig();

            // Skip keys
            if (confUuid == null)
                break;

            /*if (!mIsSensorTag2) {
                // Barometer calibration
                if (confUuid.equals(SensorTagGatt.UUID_BAR_CONF) && enable) {
                    calibrateBarometer();
                }
            }*/

            BluetoothGattService serv = mBtGatt.getService(servUuid);
            if (serv != null) {
                BluetoothGattCharacteristic charac = serv.getCharacteristic(confUuid);
                byte value = enable ? sensor.getEnableSensorCode()
                        : Sensor.DISABLE_SENSOR_CODE;
                if (mBluetoothLeService.writeCharacteristic(charac, value)) {
                    mBluetoothLeService.waitIdle(GATT_TIMEOUT);
                } else {
                    setError("Sensor config failed: " + serv.getUuid().toString());
                    break;
                }
            }
        }
    }

    private void enableNotifications(boolean f) {
        Log.i("tag", "Enabling Notifications. ~~~~~~~~~~~~~~~~");
        final boolean enable = f;

        for (Sensor sensor : mEnabledSensors) {
            UUID servUuid = sensor.getService();
            UUID dataUuid = sensor.getData();
            BluetoothGattService serv = mBtGatt.getService(servUuid);
            if (serv != null) {
                BluetoothGattCharacteristic charac = serv.getCharacteristic(dataUuid);

                if (mBluetoothLeService.setCharacteristicNotification(charac, enable)) {
                    mBluetoothLeService.waitIdle(GATT_TIMEOUT);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    setError("Sensor notification failed: " + serv.getUuid().toString());
                    break;
                }
            }
        }
    }

    private void enableDataCollection(boolean enable) {
        //setBusy(true);
        enableSensors(enable);
        enableNotifications(enable);
        //setBusy(false);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            // your implementation here
            Log.i("tag", "onLeScan callback triggered.");

            if(checkDeviceFilter(device.getName())){

                runOnUiThread(new Runnable() {
                    public void run() {
                        connectionDialog.setText("TI Sensortag Found!");
                    }
                });

                Log.i("tag", "**********************************************");
                Log.i("tag", "Found a SensorTag!");
                Log.i("tag", "**********************************************");

                Log.i("tag", device.getName());
                Log.i("tag", device.getAddress());

                mScanning = false;
                stopScan();

                if(mBluetoothLeService != null) {
                    Log.i("tag", "Connecting to bluetooth device through service." );

                    if(mBluetoothLeService.connect(device.getAddress())){
                        mBtGatt = BluetoothLeService.getBtGatt();
                    }
                }
            }

            /* This manages a device list & updates a ScanView fragment */
            /*runOnUiThread(new Runnable() {
                public void run() {
                    // Filter devices
                    if (checkDeviceFilter(device.getName())) {
                        if (!deviceInfoExists(device.getAddress())) {
                            // New device
                            BleDeviceInfo deviceInfo = createDeviceInfo(device, rssi);
                            addDevice(deviceInfo);
                        } else {
                            // Already in list, update RSSI info
                            BleDeviceInfo deviceInfo = findDeviceInfo(device);
                            deviceInfo.updateRssi(rssi);
                            //mScanView.notifyDataSetChanged();
                        }
                    }
                }*

            });*/
        }
    };

    // Code to manage Service life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(mThis, "Unable to initialize BluetoothLeService", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            final int n = mBluetoothLeService.numConnectedDevices();
            if (n > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mThis.setError("Multiple connections!");
                    }
                });
            } else {
                startScan();
                // Log.i(TAG, "BluetoothLeService connected");
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            // Log.i(TAG, "BluetoothLeService disconnected");
        }
    };

    // ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Broadcasted actions from Bluetooth adapter and BluetoothLeService
    //
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Intent fIntent = intent;
            final String action = intent.getAction();

            Log.i("tag","---- Received broadcast from BluetoothLeService ----");

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth adapter state change
                switch (mBtAdapter.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        //mConnIndex = NO_DEVICE;
                        //startBluetoothLeService();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, R.string.app_closing, Toast.LENGTH_LONG)
                                .show();
                        finish();
                        break;
                    default:
                        // Log.w(TAG, "Action STATE CHANGED not processed ");
                        break;
                }
                //updateGuiState();

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // GATT connect
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //setBusy(false);
                    //startDeviceActivity();

                    connectionDialog.setText("Connected to BTLE Device: " + intent.getStringExtra("com.example.ti.ble.common.EXTRA_ADDRESS"));
                    mBluetoothLeService.getBtGatt().discoverServices();
                    /*runOnUiThread(new Runnable() {
                        public void run() {
                            connectionDialog.setText("Connected to BTLE Device: " + fIntent.getStringExtra("com.example.ti.ble.common.EXTRA_UUID"));
                        }
                    });*/

                } else {
                    setError("Connect failed. Status: " + status);
                }

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                // GATT disconnect
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                //stopDeviceActivity();
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //setBusy(false);
                    //mScanView.setStatus(mBluetoothDevice.getName() + " disconnected",
                            //STATUS_DURATION);
                    btleIrCharacteristic.setText(" ");
                    btleServicesCount.setText(" ");
                    connectionDialog.setText("Disconnected.");

                } else {
                    setError("Disconnect failed. Status: " + status);
                }
                //mConnIndex = NO_DEVICE;
                mBluetoothLeService.close();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                btleServicesCount.setText("Services Discovered!");
                btleServicesCount.setText("Services Discovered!  " + mBluetoothLeService.getNumServices() + " available.");

                List<BluetoothGattService> myServices = mBluetoothLeService.getSupportedGattServices();
                for(BluetoothGattService s : myServices) {
                    Log.i("tag", "Service: " + s.getUuid());
                    List<BluetoothGattCharacteristic> myCharacteristics = s.getCharacteristics();
                    for(BluetoothGattCharacteristic c : myCharacteristics) {
                        Log.i("tag,", "Characteristic: " + c.getValue());
                    }
                }

                updateSensorList();
                enableDataCollection(true);  //Activate sensors, enable notifications
                btleIrCharacteristic.setText("Enabling IR Sensor ane enabling notifications . . .");

            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                //btleServicesCount.setText("New Data from: " + intent.getStringExtra(BluetoothLeService.EXTRA_UUID) + " " + intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //onCharacteristicChanged(uuidStr, value);

                Point3D v;
                String msg;

                if (uuidStr.equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
                    v = Sensor.IR_TEMPERATURE.convert(value);
                    msg = decimal.format(v.x) + "\n";
                    //mAmbValue.setText(msg);
                    msg = decimal.format(v.y) + "\n";
                    //mObjValue.setText(msg);

                    btleIrCharacteristic.setText("New IR Data: " + msg);
                }

                //Call onCharacteristicChanged here

            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // Data written
                Log.i("tag", "Data written ~~~~~~~~~~~~~");
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //onCharacteristicWrite(uuidStr, status);

            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                // Data read
                Log.i("tag", "Data read ~~~~~~~~~~~~~");

                //String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                //onCharacteristicsRead(uuidStr, value, status);

            }else {
                // Log.w(TAG,"Unknown action: " + action);
            }

        }
    };

}
