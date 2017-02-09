package com.boffinapes.linealis.prime;



import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class instructActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructionslayout);
        TextView t = (TextView) findViewById(R.id.instructionsView);
        String text = "To Slide a Piece:\nTouch piece and slide piece to the right\n\nTo Flip a Piece:\nTap the piece\n\nTo Cycle Pieces:\nSlide finger up and down\n\nTo Pause:\nSlide finger from right to left across screen, tap to resume\n\nTo Retry:\nTap Screen\n";
        t.setText(text);
    }
}
