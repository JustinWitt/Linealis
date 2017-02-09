package com.boffinapes.linealis.prime;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class PieceBuilder extends Activity{
    int[][][] newpiece = new int[3][4][3];
    public int[] piececolors = {Color.YELLOW, Color.GREEN, Color.BLUE};
    public int emptyspace = Color.rgb(0, 0, 0);
    int currentselection = 0;
    int squaresize;
    float downX;
    float downY;
    public String filename = "createdpieces.dat";
    
    
 @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //get squaresize from screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        
        if(display.getHeight() < display.getWidth()){
            squaresize = display.getHeight()/18;
        }
        else{
            squaresize = display.getWidth()/18;
        }
        for(int k = 0; k<3; k++){
            for(int i = 0; i<4; i++){
                for(int j=0; j<3; j++){
                    newpiece[k][i][j] = emptyspace;
                }
            }
        }


        BuildView bv = new BuildView(this);
        
     
        //gets basic finger down, finger up and puts those in global variables
          bv.setOnTouchListener(new OnTouchListener(){
              public boolean onTouch(View v, MotionEvent event) {
                  BuildView myview = (BuildView) v;
                  int action = event.getAction();
                  if(action == MotionEvent.ACTION_DOWN){
                      downY = event.getY();
                      downX = event.getX();
        
                  }
                  if(action == MotionEvent.ACTION_UP){
                      float upX = event.getX();
                      float upY = event.getY();
                      if(Math.abs(upX - downX) < 30 && Math.abs(upY-downY) < 30){
                          int part = onPart(downX,downY);
                          if(part != -1){
                              if(part<13){
                                  int x = (part-1)%3;
                                  int y = ((part - x) + 2) / 4;
                                  if(newpiece[currentselection][y][x] == emptyspace){
                                      newpiece[currentselection][y][x] = piececolors[currentselection];
                                  }
                                  else{
                                      newpiece[currentselection][y][x] = emptyspace;
                                  }
                                  myview.postInvalidate();
                              }
                              else if(part == 13){
                                  savepieces();
                                  myview.postInvalidate();
                              }
                              else if(part == 14){
                                  for(int y=0; y<4; y++){
                                      for(int x=0; x<3; x++){
                                          newpiece[currentselection][y][x] = emptyspace;
                                      }
                                  }
                                  myview.postInvalidate();
                              }
                              else if(part == 15){
                                  currentselection = 0;
                                  myview.postInvalidate();
                              }
                              else if(part == 16){
                                  currentselection = 1;
                                  myview.postInvalidate();
                              }
                              else if(part == 17){
                                  currentselection = 2;
                                  myview.postInvalidate();
                              }
                          }
                      }
                  }
                  return true;
              }
          });


        setContentView(bv);
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
 
 


public void savepieces(){
    newpiece[1][1][1] = Color.GRAY;
}

public int onPart(float x, float y){
    int row = -1;
    int col = -1;
    if(x>squaresize*2 && x<squaresize*5){
        col = 1;
    }
    else if(x>squaresize*5 && x<squaresize*8){
        col = 2;
    }
    else if(x>squaresize*8 && x<squaresize*11){
        col = 3;
    }
    if(y>squaresize && y<squaresize*4){
        row = 0;
    }
    else if(y>squaresize*4 && y<squaresize*7){
        row = 1;
    }
    else if(y>squaresize*7 && y<squaresize*10){
        row = 2;
    }
    else if(y>squaresize*10 && y<squaresize*13){
        row = 3;
    }
    if(row != -1 && col != -1){
        return row*3 + col; 
    }
    if(x<squaresize*6.5 && y>squaresize*14){
        return 13;
    }
    if(x>squaresize*6.5  && x< squaresize*13 && y>squaresize*14){
        return 14;
    }

    if(x>squaresize*13 && y<squaresize*6){
    return 15;
    }
    if(x>squaresize*13 && y>squaresize*6 && y<squaresize*12){
        return 16;
    }
    if(x>squaresize*13 && y>squaresize*12){
        return 17;
    }   
    return -1;
}



     
 private class BuildView extends View{
        public BuildView(Context context){
            super(context);
        }
        
        public void drawsave(Canvas canvas){
            Rect savearea = new Rect(0, (14*squaresize)+2, ((int) 6.5*squaresize)-2,(18*squaresize)-2);
            Bitmap rm = BitmapFactory.decodeResource(getResources(), R.drawable.savepiece);
            Paint surround = new Paint();
            surround.setColor(Color.WHITE);
            canvas.drawBitmap(rm, null, savearea, surround);
        }
        
        public void drawclear(Canvas canvas){
            Rect cleararea = new Rect(((int) 6.5*squaresize)+2,(14*squaresize)+2, (13*squaresize)-2,(18*squaresize)-2 );
            Bitmap rm = BitmapFactory.decodeResource(getResources(), R.drawable.clearpiece);
            Paint surround = new Paint();
            surround.setColor(Color.WHITE);
            canvas.drawBitmap(rm, null, cleararea, surround);
        }
        
        public void drawpiece(Canvas canvas){
            float startx = 2*squaresize;
            float starty = squaresize;
            RectF background = new RectF(startx,starty,startx+(squaresize*9),starty+(squaresize*12));
            Paint backcolor = new Paint();
            backcolor.setStyle(Style.FILL);
            backcolor.setColor(Color.WHITE);
            canvas.drawRect(background, backcolor);
            for(int x = 0; x<3; x++){
                for(int y=0; y<4; y++){                    
                    if(newpiece[currentselection][y][x]!=0){
                        Paint pcolor = new Paint();
                        pcolor.setColor(newpiece[currentselection][y][x]);
                        pcolor.setStyle(Style.FILL);
                        RectF piecerect = new RectF(startx+2,starty+2,startx+(squaresize * 3)-2,starty+(squaresize *3)-2);
                        canvas.drawRect(piecerect, pcolor);
                    }
                    starty = starty + (squaresize*3);
                }
                starty = squaresize;
                startx = startx + (squaresize*3);
            }
        }
        
        public void drawminis(Canvas canvas){
            for(int i = 0; i<3; i++){
                float startx = 14*squaresize;
                float starty = squaresize + (squaresize*i*6);
                RectF background = new RectF(startx,starty,startx+(squaresize*3),starty+(squaresize*4));
                Paint backcolor = new Paint();
                backcolor.setStyle(Style.FILL);
                backcolor.setColor(Color.WHITE);
                canvas.drawRect(background, backcolor);
                for(int x = 0; x<3; x++){
                    for(int y=0; y<4; y++){                    
                        if(newpiece[i][y][x]!=0){
                            Paint pcolor = new Paint();
                            pcolor.setColor(newpiece[i][y][x]);
                            pcolor.setStyle(Style.FILL);
                            RectF piecerect = new RectF(startx+2,starty+2,startx+(squaresize-2),starty+(squaresize-2));
                            canvas.drawRect(piecerect, pcolor);
                        }
                        starty = starty + squaresize;
                    }
                    starty = squaresize + (squaresize*i*6);
                    startx = startx + squaresize;
                }
            }    
        }

    public void drawlines(Canvas canvas){
        Paint linecolor = new Paint();
        linecolor.setStyle(Style.STROKE);
        linecolor.setColor(Color.WHITE);
        linecolor.setStrokeWidth(3);
        canvas.drawLine(0,18*squaresize, 18*squaresize, 18*squaresize,linecolor);
        canvas.drawLine(18*squaresize, 0, 18*squaresize, 18*squaresize,linecolor);
        canvas.drawLine(13*squaresize, 0, 13*squaresize, 18*squaresize,linecolor);
        canvas.drawLine(13*squaresize, 6*squaresize,18*squaresize,6*squaresize,linecolor);
        canvas.drawLine(13*squaresize, 12*squaresize,18*squaresize,12*squaresize,linecolor);
        canvas.drawLine(0, 14*squaresize, 13*squaresize,14*squaresize,linecolor);
        canvas.drawLine((int) 6.5*squaresize, 14*squaresize, (int) 6.5*squaresize,18*squaresize, linecolor);
        if(currentselection == 0){
            Rect sele0 = new Rect((squaresize*13)+3,3,(squaresize*18)-3,(squaresize*6)- 3);
            linecolor.setColor(Color.RED);
            canvas.drawRect(sele0,linecolor);
        }
        else if(currentselection == 1){
            Rect sele1 = new Rect((squaresize*13)+3,(squaresize*6)+3,(squaresize*18)-3,(squaresize*12)- 3);
            linecolor.setColor(Color.RED);
            canvas.drawRect(sele1,linecolor);
        }
        else if(currentselection == 2){
            Rect sele2 = new Rect((squaresize*13)+3,(squaresize*12)+3,(squaresize*18)-3,(squaresize*18)- 3);
            linecolor.setColor(Color.RED);
            canvas.drawRect(sele2,linecolor);
        }
    }
        
        @Override
        protected void onDraw(Canvas canvas){
            canvas.drawColor(Color.BLACK);
            drawlines(canvas);
            drawsave(canvas);
            drawclear(canvas);
            drawpiece(canvas);
            drawminis(canvas);
        }
    }



}