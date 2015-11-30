package me.fiveinarow.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.fiveinarow.R;
import me.fiveinarow.adapter.DeviceAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                Intent client = new Intent(MenuActivity.this, BlueToochServerService.class);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(foundDeviceReceiver);
    }

    private BroadcastReceiver foundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                showResult();
                // 从Intent中获取设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 将设备名称和地址放入array adapter，以便在ListView中显示
                Log.e("foundDeviceReceiver", "search->" + device.getName() + "  --  " + device.getAddress());
                list.add(device.getName() + "  --  " + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void showResult(){
        lv_result.setVisibility(View.VISIBLE);
        lv_result.setOnItemClickListener(this);
        btn_quit.setOnClickListener(null);
        btn_join.setOnClickListener(null);
        btn_create.setOnClickListener(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
