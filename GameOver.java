package com.boffinapes.linealis.prime;



import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class GameOver extends Activity {
    float width;
    int score;
    String filename = "HiScore";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            score = extras.getInt("cScore");
        }
        File file = getFileStreamPath(filename);
        if(file.exists() == false){
            quicksave(1);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        FinishedGame fg = new FinishedGame(this);
        fg.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                startOver();
            }
        });
        setContentView(fg);
    }
    
    public void startOver(){
        Intent passer = new Intent(GameOver.this, Game.class);
        startActivity(passer);
        this.finish();
    }
    
    public boolean quicksave(int whatever){
        try{
            FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut); 
            osw.write(Integer.toString(whatever) + '\n');
            osw.flush();
            osw.close();
        }
        catch (IOException x) {
            System.err.println(x);
            return false;
        }
        return true;
    }
    
    public String quickread(){
        String temp = "error";
        try{
            FileInputStream fIn = openFileInput(filename);
            BufferedInputStream bis = new BufferedInputStream(fIn);
            DataInputStream dis = new DataInputStream(bis);
            temp = dis.readLine();
            dis.close();
            bis.close();
            fIn.close();
        }
        catch (IOException x) {
            System.err.println(x);
            return temp;
        }
        return temp;
    }
    
    private class FinishedGame extends View{
        public FinishedGame(Context context){
            super(context);
        }
        
        public void gameOverDraw(Canvas c){
            /*RectF background = new RectF(0,0,width,(float)(width*.19375));
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
            c.drawBitmap(bm, null, background, new Paint());*/
            Paint txtcolor = new Paint();
            txtcolor.setColor(Color.WHITE);
            txtcolor.setStyle(Style.STROKE);
            txtcolor.setTextSize((float) (width/10));
            String bestscore = quickread();
            int oldscore = Integer.parseInt(bestscore);
            if(score>oldscore){
                quicksave(score);
                bestscore = Integer.toString(score);
            }
            String bscore = "Best Score: " + bestscore;
            String yourscore = "Your Score: " + Integer.toString(score);
            c.drawText(bscore,(float) (width/20), (float) (width*.1), txtcolor);
            c.drawText(yourscore,(float) (width/20), (float) (width*.3), txtcolor);
            Paint bkground = new Paint();
            bkground.setColor(Color.GRAY);
            bkground.setStyle(Style.FILL);
            RectF retrybk = new RectF((float) (width*.10),(float) (width*.40) ,(float)(width*.90),(float) ((width*.40)+(width*.1944)));
            Bitmap rm = BitmapFactory.decodeResource(getResources(), R.drawable.retryimg);
            c.drawBitmap(rm, null, retrybk, new Paint());
            
        }
             
        @Override
        protected void onDraw(Canvas canvas){
            canvas.drawColor(Color.BLACK);
            gameOverDraw(canvas);
        }
    }
}
