package me.fiveinarow.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.fiveinarow.R;
import me.fiveinarow.adapter.DeviceAdapter;
import me.fiveinarow.common.P;
import me.fiveinarow.service.BlueToochClientService;
import me.fiveinarow.service.BlueToochServerService;
import me.fiveinarow.widget.LoadingDialog;

/**
 * Created by caowenhua on 2015/11/29.
 */
public class MenuActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private Button btn_create;
    private Button btn_join;
    private Button btn_quit;
    private LoadingDialog loadingDialog;
    private List<String> list;
    private ListView lv_result;
    private DeviceAdapter adapter;

    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_join = (Button) findViewById(R.id.btn_join);
        btn_quit = (Button) findViewById(R.id.btn_quit);
        lv_result = (ListView) findViewById(R.id.lv_result);

        list = new ArrayList<>();
        adapter = new DeviceAdapter(list, this);
        lv_result.setAdapter(adapter);

        btn_create.setOnClickListener(this);
        btn_join.setOnClickListener(this);
        btn_quit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                Intent server = new Intent(MenuActivity.this, BlueToochServerService.class);
                startService(server);
                loadingDialog = new LoadingDialog(MenuActivity.this, "正在等待玩家加入..");
                break;
            case R.id.btn_join:
                Intent client = new Intent(MenuActivity.this, BlueToochClientService.class);
                startService(client);
                loadingDialog = new LoadingDialog(MenuActivity.this, "正在查找设备..");
                break;
            case R.id.btn_quit:
                System.exit(0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(foundDeviceReceiver, foundFilter);
        IntentFilter conFilter = new IntentFilter("connection");
        registerReceiver(connectionReceiver, conFilter);
        IntentFilter serFilter = new IntentFilter("connectionServer");
        registerReceiver(connectionServerReceiver, serFilter);

        wl.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(foundDeviceReceiver);
        unregisterReceiver(connectionReceiver);
        unregisterReceiver(connectionServerReceiver);

        wl.release();
    }

    private BroadcastReceiver foundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                loadingDialog.dismiss();
                showResult();
                // 从Intent中获取设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 将设备名称和地址放入array adapter，以便在ListView中显示
                Log.e("foundDeviceReceiver", "search->" + device.getName() + "  --  " + device.getAddress());
                String s = device.getName() + "\n" + device.getAddress();
                if(!list.contains(s)){
                    list.add(device.getName() + "\n" + device.getAddress());
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    private BroadcastReceiver connectionServerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("connectionServer", false)){
                Intent i = new Intent(MenuActivity.this, PieceActivity.class);
                i.putExtra("isBlack", true);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("connection", false)){
                Intent i = new Intent(MenuActivity.this, PieceActivity.class);
                i.putExtra("isBlack", false);
                startActivity(i);
                finish();
            }
            else {
                Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void hideResult(){
        lv_result.setVisibility(View.GONE);
        lv_result.setOnItemClickListener(null);
        btn_quit.setOnClickListener(this);
        btn_join.setOnClickListener(this);
        btn_create.setOnClickListener(this);
    }

    private void showResult(){
        lv_result.setVisibility(View.VISIBLE);
        lv_result.setOnItemClickListener(this);
        btn_quit.setOnClickListener(null);
        btn_join.setOnClickListener(null);
        btn_create.setOnClickListener(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MenuActivity.this, BlueToochClientService.class);
        intent.putExtra(P.OP, P.CONNECT_SERVER);
        intent.putExtra(P.CONNECT_ADDRESS, list.get(position).substring(list.get(position).indexOf("\n")+ 1));
        Log.e("connect address", list.get(position));
        Log.e("connect address", list.get(position).substring(list.get(position).indexOf("\n") + 1));
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if(lv_result.isShown()){
            hideResult();
        }
        else{
            super.onBackPressed();
        }

    }
}
