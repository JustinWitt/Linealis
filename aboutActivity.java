package com.boffinapes.linealis.prime;




import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class aboutActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructionslayout);
        TextView t = (TextView) findViewById(R.id.instructionsView);
        String text = "Created by Boffin Ape\n\nwww.boffinape.com";
        t.setText(text);
    }
}
