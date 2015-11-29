package me.fiveinarow.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class BlueToochServerService extends Service {

    private UUID bluetooth_uuid = UUID.fromString("D7437E5C-E841-723C-74DF-4158924F6B26");
    private BluetoothAdapter adapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.setName("gameServer");
        adapter.enable();
        while(!adapter.isEnabled()){

        }
        new AcceptThread().start();
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mServerSocket;
        private BluetoothSocket socket = null;
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = adapter.listenUsingRfcommWithServiceRecord("FiveInARow", bluetooth_uuid);
            } catch (IOException e) { }
            mServerSocket = tmp;
        }
        public void run() {
            socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    //manageConnectedSocket(socket);
                    String s = socket.getRemoteDevice().getName() + "/n" + socket.getRemoteDevice().getAddress();
                    break;
                }
            }
        }
        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) { }
        }

        class outputThread extends Thread{

            @Override
            public void run() {
                super.run();
                try {
                    OutputStream os = socket.getOutputStream();
                    os.write("0".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
