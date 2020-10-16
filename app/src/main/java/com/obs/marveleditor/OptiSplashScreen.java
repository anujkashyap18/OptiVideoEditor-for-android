package com.obs.marveleditor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class OptiSplashScreen extends AppCompatActivity {
    private HashMap _$_findViewCache;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.opti_splash_screen);
        this.initListener();
    }

    private void initListener() {
        Handler handler = new Handler();
        handler.postDelayed((Runnable) (new Runnable() {
            public final void run() {
                OptiSplashScreen.this.callActivityIntent();
            }
        }), 3000L);
    }

    private final void callActivityIntent() {
        Intent intentFlag = new Intent((Context) this, MainActivity.class);
        intentFlag.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intentFlag);
    }

    protected void onStop() {
        super.onStop();
        this.finish();
    }

    public View _$_findCachedViewById(int var1) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View) this._$_findViewCache.get(var1);
        if (var2 == null) {
            var2 = this.findViewById(var1);
            this._$_findViewCache.put(var1, var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }
}
