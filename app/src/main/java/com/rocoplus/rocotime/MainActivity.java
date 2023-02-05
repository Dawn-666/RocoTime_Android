package com.rocoplus.rocotime;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends RocoTimeActivity {
    private WebView webView;
    private EditText edt_url;
    private TextView btn_ok;
    private LinearLayout layout_float;
    private static final String WEBSITE_ROCO = "https://17luoke.cn/h5/";
    //方法重写
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        //初始化本界面
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        //关联界面控件
        //成员属性
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.edt_url = this.findViewById(R.id.edt_url);
        this.webView = this.findViewById(R.id.webView);
        this.btn_ok = this.findViewById(R.id.btn_go);
        this.layout_float = this.findViewById(R.id.layout_float);
        this.layout_float.setVisibility(View.GONE);
        //加载界面配置
        WebSettings settings = this.webView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setGeolocationEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        this.webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            String name;
            if (contentDisposition.contains("filename=\""))
                name = (name = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10)).substring(0, name.indexOf("\""));
            else name = StringUtils.toHex(StringUtils.toMd5(url.getBytes()));
            String path = Environment.getExternalStorageDirectory().getPath() + "/Download/" + name;
            this.download(url, path);
        });
        this.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.e("should", url);
                if (url.startsWith("http://") || url.startsWith("https://"))
                    if (url.endsWith("RKT58FG542"))
                        MainActivity.this.download(url, Environment.getExternalStorageDirectory().getPath() + "/Download/" + StringUtils.toHex(StringUtils.toMd5(url.getBytes())) + ".png");
                    else view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                MainActivity.this.edt_url.setHint(url);
            }
        });
        this.webView.loadUrl(WEBSITE_ROCO);
        toolbar.setVisibility(View.GONE);
        //监听点击“前往”
        this.btn_ok.setOnClickListener(v -> this.webView.loadUrl(this.edt_url.getText().toString()));
        //监听文本改变
        this.edt_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                MainActivity.this.btn_ok.setEnabled(!s.toString().isEmpty());
            }
        });
        this.findViewById(R.id.btn_clean).setOnClickListener(v -> {
            Toast.makeText(this, "成功清理" + CacheUtils.getClearedByte(this) + "缓存。", Toast.LENGTH_LONG).show();
            this.webView.clearFormData();
            this.webView.clearHistory();
//            if (this.webView.getOriginalUrl() == null)
//                this.webView.loadUrl(WEBSITE_SPARE);
//            else if (this.webView.getOriginalUrl().startsWith(WEBSITE_ROCO) || this.webView.getOriginalUrl().startsWith(WEBSITE_SPARE))
//                this.webView.loadUrl(WEBSITE_SHADOW);
//            else this.webView.loadUrl(this.canAccessWebsite ? WEBSITE_ROCO : WEBSITE_SPARE);
        });
        this.findViewById(R.id.btn_home).setOnClickListener(v-> this.webView.loadUrl("https://17luoke.cn/h5/#/"));
        this.findViewById(R.id.btn_refresh).setOnClickListener(v -> this.webView.reload());
    }
    @Override
    public void onBackPressed() {
        if (this.webView.canGoBack())
            this.webView.goBack();
        else super.onBackPressed();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            this.layout_float.setVisibility(layout_float.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);//this.toolbar.setVisibility(this.toolbar.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        return super.onKeyDown(keyCode, event);
    }
    private void download(String url, String path) {
        new Thread(() -> {
            Looper.prepare();
            try {
                int length;
                byte[] bytes = new byte[1024];
                Toast.makeText(this, "正在下载，请稍候~", Toast.LENGTH_LONG).show();
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt");
                OutputStream out = new FileOutputStream(path);
                InputStream in = connection.getInputStream();
                while ((length = in.read(bytes)) != -1)
                    out.write(bytes, 0, length);
                out.close();
                in.close();
                connection.disconnect();
                Toast.makeText(this, "下载成功，文件已保存至\"" + path + "\"!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "下载失败，请重试!", Toast.LENGTH_LONG).show();
                Log.e("IOException", e.toString());
            }
            Looper.loop();
        }).start();
    }
}