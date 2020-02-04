package com.dev.methk.arduinoandroid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Receiver extends AppCompatActivity {

    // Widgets
    public Button btnDis;
    //public TextView dataTW, dataTW2 ;
    public String address = null;

    // Bluetooth
    private ProgressDialog progress;
    private ArrayList<Sensor> sensors;
    private String[] names = {"Bomba 1", "Bomba 2", "Bomba Piloto", "Solenóide Bomba", "Sensor", "Sensor", "Sensor","Sensor"};
    private  ListView listSensors;
    ArrayAdapter adapter;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Thread workerThread;
    byte[] generalBuffer;
    int generalBufferPosition;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newInt = getIntent();
        address = newInt.getStringExtra(DeviceList.EXTRA_ADDRESS); // MAC address of the chosen device

        setContentView(R.layout.activity_receiver);

        // Initialize widgets
        btnDis = (Button)findViewById(R.id.BTN_disc);
        //dataTW = (TextView)findViewById(R.id.TW_data);
        //dataTW2 = (TextView)findViewById(R.id.TW_data2);

        new ConnectBT().execute(); // Connection class
        listSensors = (ListView)findViewById(R.id.listView);
        sensors = new ArrayList<Sensor>();
        adapter = new SensorAdapter(this, sensors);
        listSensors.setAdapter(adapter);


        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void generateSensors(int amount) {

        Sensor e;
        for (int i = 0; i< amount; i++) {
            e = new Sensor(names[i], "0","0", "0");
            sensors.add(e);
        }
        listSensors.setAdapter(adapter);
    }

    private void updateArray( int amount, String[] results )
    {
        double r, pBAR, pPSI, pKPA;
        for (int i = 0; i< amount; i++) {

            r = Double.parseDouble(results[i]);
            pBAR = r * (500.0/4.5);
            pBAR = Math.ceil( pBAR * 100)/100;

            pPSI = r * ( 7251.89/4.5);
            pPSI = Math.ceil( pPSI * 100)/100;

            pKPA = pBAR * 100;
            pKPA = Math.ceil( pKPA * 100)/100;

            //dataTW.setText( String.valueOf(Pressao_bar)  );


            Sensor e = new Sensor(names[i], String.valueOf(pBAR), String.valueOf(pPSI), String.valueOf(pKPA));
            sensors.set(i, e);
        }
        listSensors.setAdapter(adapter);
    }

    private String[] processReturn(String r ){
        return  r.split(";",-1);
    }


    private void showReturn( String r){

        String[] results = processReturn(r);

        if (sensors.size() < 1)
            generateSensors(results.length);

        updateArray(results.length, results);

    }



    // If go back disconnect
    @Override
    public void onBackPressed() {
        disconnect();
        finish();
        return;
    }

    // Disconnection
    private void disconnect() {
        if (btSocket != null) { // If bluetooth socket is taken then disconnect
            try {
                btSocket.close(); // Close bluetooth connection
            }
            catch (IOException e) {
                toast("Erro ao fechar conexão");
            }
        }
        finish();
    }

    private void toast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void beginListenForData() {
        final Handler handler = new Handler(); // Interacts between this thread and UI thread
        final byte delimiter = 35; // ASCII code for (#) end of transmission

        stopWorker = false;
        generalBufferPosition = 0;
        generalBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = btSocket.getInputStream().available(); // Received bytes by bluetooth module



                        if (bytesAvailable > 0) {
                          //  dataTW.setText("HM") ;
                            byte[] packet = new byte[bytesAvailable];
                            btSocket.getInputStream().read(packet);

                            for (int i=0; i<bytesAvailable; i++) {
                                byte b = packet[i];
                                if (b == delimiter) { // If found a # print on screen
                                    byte[] arrivedBytes = new byte[generalBufferPosition];
                                    System.arraycopy(generalBuffer, 0, arrivedBytes, 0, arrivedBytes.length);
                                    final String data = new String(arrivedBytes, "US-ASCII"); // Decode from bytes to string
                                    generalBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                         showReturn(data);
                                            //dataTW.setText(data); // Print on screen
                                        }
                                    });
                                }
                                else { // If there is no # add bytes to buffer
                                    generalBuffer[generalBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> { // UI thread

        private boolean connectionSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Receiver.this, "Conectando...", "Aguarde!"); // Connection loading dialog
        }

        @Override
        protected Void doInBackground(Void... devices) { // Connect with bluetooth socket

            try {
                if (btSocket == null || !isBTConnected) { // If socket is not taken or device not connected
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address); // Connect to the chosen MAC address
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); // This connection is not secure (mitm attacks)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery(); // Discovery process is heavy
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                connectionSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) { // After doInBackground
            super.onPostExecute(result);

            if (!connectionSuccess) {
                toast("Conexão falhou. Tente Novamente.");
                finish();
            }
            else {
                toast("Conectado");
                beginListenForData();
                isBTConnected = true;
            }
            progress.dismiss();
        }


    }

    public void printData(String data){

        /*
        //Pres_Bar = Pressao * (500/4.5);
        double Pressao_bar = Double.parseDouble(data);
        Pressao_bar = Pressao_bar * (500.0/4.5);
        Pressao_bar = Math.ceil( Pressao_bar * 100)/100;
        dataTW.setText( String.valueOf(Pressao_bar)  );


        double Pressao_psi = Double.parseDouble(data);
        Pressao_psi = Pressao_psi * ( 7251.89/4.5);
        Pressao_psi = Math.ceil( Pressao_psi * 100)/100;
        dataTW2.setText( String.valueOf(Pressao_psi)  );
        */

    }



}
