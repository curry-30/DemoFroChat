package com.phy.demofrovoice.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.jaeger.library.StatusBarUtil;
import com.phy.demofrovoice.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_chat)
    Button btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.colorPrimary), 0);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_chat)
    public void onClick() {
        Intent intent = new Intent(this,ChatActivity.class);
        startActivity(intent);
    }
}
