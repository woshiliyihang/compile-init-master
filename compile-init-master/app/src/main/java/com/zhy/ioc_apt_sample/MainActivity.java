package com.zhy.ioc_apt_sample;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.FindView;

public class MainActivity extends AppCompatActivity {

    @FindView(R.id.showFont)
    public TextView showFont;

    @FindView(R.id.myLayout)
    public RelativeLayout myLayout;

    @FindView(R.id.myClick)
    public Button myClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity$$ViewFindImp.findView(this);
        showFont.setText("测试文字");
        myLayout.setBackgroundResource(R.mipmap.ic_launcher);

    }

}
