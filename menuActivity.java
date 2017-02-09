package com.boffinapes.linealis.prime;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

public class menuActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        ImageView playview = (ImageView) findViewById(R.id.playView);
        playview.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                Intent passer = new Intent(menuActivity.this, Game.class);
                startActivity(passer);     
            }
        });
        
        Button instruct = (Button) findViewById(R.id.instructButton);
        instruct.setOnClickListener(new OnClickListener(){
           public void onClick(View iv){
               Intent ipass = new Intent(menuActivity.this, instructActivity.class);
               startActivity(ipass);
           }
        });
        
        Button builder = (Button) findViewById(R.id.buildButton);
        builder.setOnClickListener(new OnClickListener(){
            public void onClick(View iv){
                Intent ipass = new Intent(menuActivity.this, PieceBuilder.class);
                startActivity(ipass);
            }
         });
        
        Button about = (Button) findViewById(R.id.aboutButton);
        about.setOnClickListener(new OnClickListener(){
           public void onClick(View av){
               Intent apass = new Intent(menuActivity.this, aboutActivity.class);
               startActivity(apass);
           }
        });
        
        
    }
}
