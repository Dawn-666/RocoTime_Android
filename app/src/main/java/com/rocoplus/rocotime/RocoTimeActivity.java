package com.rocoplus.rocotime;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RocoTimeActivity extends AppCompatActivity {
    private static final ArrayList<RocoTimeActivity> activities = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RocoTimeActivity.activities.add(this);
        if (this.getSupportActionBar() != null) this.getSupportActionBar().hide();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    @Override
    protected void onDestroy() {
        RocoTimeActivity.activities.remove(this);
        super.onDestroy();
    }
    //退出整个应用
    public void exit() {
        for (RocoTimeActivity activity : RocoTimeActivity.activities)
            activity.finish();
    }
}
