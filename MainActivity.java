package com.boffinapes.linealis.prime;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView mainview = (ImageView) findViewById(R.id.imageView1);
        mainview.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent passer = new Intent(MainActivity.this, menuActivity.class);
                startActivity(passer);
                MainActivity.this.finish();     
            }
        });
    }
}
