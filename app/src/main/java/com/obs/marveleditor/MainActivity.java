package com.obs.marveleditor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.obs.marveleditor.fragments.OptiMasterProcessorFragment;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize((Context) this);
        this.setContentView(R.layout.activity_main);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, (Fragment) (new OptiMasterProcessorFragment())).commit();
    }

}
