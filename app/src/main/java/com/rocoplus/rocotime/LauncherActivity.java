package com.rocoplus.rocotime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LauncherActivity extends RocoTimeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if (!isPermissionGranted())
            this.requestPermission();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isPermissionGranted()) return;
        Toast.makeText(this, "请先授予储存访问权限！", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isPermissionGranted())
            this.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0)
            if(!this.isPermissionGranted()) {
                Toast.makeText(this, "请先授予储存访问权限！", Toast.LENGTH_LONG).show();
                this.finish();
            } else this.start();
    }
    @Nullable
    private String getApplicationVersion() {
        try {
            return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void start() {
        new Thread(() -> {
            Looper.prepare();
            JSONObject json = new JSONObject();
            long millis = System.currentTimeMillis();
            try {
                json.put("hash", StringUtils.toHex(StringUtils.toMd5((millis + this.getApplicationVersion() + "#8boomV").getBytes(StandardCharsets.UTF_8))).substring(0, 8)).put("ver", this.getApplicationVersion()).put("time", millis);
                URL url = new URL("https://api.17luoke.cn/api/app/check");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                //Log.e("json", json.toString());
                byte[] bytes = json.toString().getBytes();
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //设置文件长度
                OutputStream out = connection.getOutputStream();
                out.write(bytes);
                out.close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                json = new JSONObject(reader.readLine());
                switch (json.getInt("retcode")) {
                    case 1:
                        Toast.makeText(this, "检测到已有可用新版本Version:" + json.getString("latestVersion") + "!", Toast.LENGTH_LONG).show();
                    case 0:
                        this.startActivity(new Intent(this, MainActivity.class));
                        this.finish();
                        break;
                    case -1:
                        Toast.makeText(this, "当前版本无法继续使用，请更新至Ver:" + json.getString("latestVersion") + "!", Toast.LENGTH_LONG).show();
                        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(json.getString("url"))));
                        this.exit();
                }
                reader.close();
            } catch (IOException | JSONException e) {
                Toast.makeText(this, "服务器验证失败，应用退出……", Toast.LENGTH_LONG).show();
                this.exit();
            }
            Looper.loop();
        }).start();
    }

    //是否授予权限
    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT < 30)
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return Environment.isExternalStorageManager();
    }
    //申请访问权限
    private void requestPermission() {
        if (Build.VERSION.SDK_INT < 30)
            if (this.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            else this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {}).launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", this.getPackageName(), null)));
        else if (!Environment.isExternalStorageManager())
            this.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {}).launch(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
    }
}