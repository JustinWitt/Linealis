package com.boffinapes.linealis.prime;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;


public class Game extends Activity{
    float downX = -1;
    float downY = -1;
    float upX = -1;
    float upY = -1;
    float moveX = -1;
    float moveY = -1;
    long timeDown = 0;
    float totalmoveX = 0;
    float totalmoveY = 0;
    float originaldownY = -1;
    float originaldownX = -1;
    int pieceon = -1;
    Random randone = new Random();
    int numberofpieces = 6;
    int score = 0;
    boolean paused = false;
    ArrayList<int[][]> piecearray;
    ArrayList<int[][]> pieceshapes;
    PieceCorners[] pieces = new PieceCorners[4];
    int[] piecesmoving = new int[3];
    int lastpiecemoving = -1;
    boolean gameover = false;
    int endoffield = 12;
    int shrinkdelay = 100000;
    int pausedelay = 0;
    long lastshrink;
    int linescompleted = 0;
    int linegoal = 3;
    long pauselastshrink = 0;
    String filename = "HiScore";
    long timegameover;
    int counter = 0;
    boolean[] connectors = new boolean[12];
    boolean[] replaceit = new boolean[3];
    int loopspeed;

    //for canvas purposes
    private float startfieldx = 30;
    private float startfieldy = 30;
    private int[] piececolors = {Color.YELLOW, Color.GREEN, Color.BLUE};
    private float scoreheight = 18;
    private float squaresize = 20;
    public int[][] board = new int[12][12];
    public int emptyspace = Color.rgb(0,0,0);
    private GameView gv;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    //for multiple masks idea
    int[][] stillmask = new int[12][12];
    int[][] chunkmask = new int[12][12];
    int[] temptop = new int[3];
    int[] templeft = new int[3];
    ArrayList<Integer> piecesorder = new ArrayList<Integer>();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        //hide notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //get squaresize from screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        
        if(display.getHeight() < display.getWidth()){
            squaresize = display.getHeight()/14;
        }
        else{
            squaresize = display.getWidth()/14;
        }
        scoreheight = squaresize - 2;
        startfieldx = (display.getWidth()-(squaresize*13))/2;
        startfieldy = 0;
       
        for(int i = 0; i<3; i++){
            replaceit[i] = false;
        }
        
        for(int i = 0; i<12; i++){
            for(int j = 0; j<12; j++){
                board[i][j] = Color.rgb(0,0,0);
            }
            connectors[i] = false;
        }
        
        //get pieces from file        
        piecearray = getPieces(this);
        pieceshapes = copyArray();
        
        loopspeed = 150;
        lastshrink = System.currentTimeMillis();
        
        
       

        
        if(savedInstanceState == null){
            int[][] piece0 = piecearray.get(1);//(randone.nextInt(numberofpieces));
            int[][] piece1 = piecearray.get(1);//(randone.nextInt(numberofpieces));
            int[][] piece2 = piecearray.get(1);//(randone.nextInt(numberofpieces));
            putpieceonboard(piece0,0,0);
            putpieceonboard(piece1,4,1);
            putpieceonboard(piece2,8,2);
            piecesmoving[0] = 5;
            piecesmoving[1] = 5;
            piecesmoving[2] = 5;
            
        }//end of first create
        else{            
            board[0] = savedInstanceState.getIntArray("board0");
            board[1] = savedInstanceState.getIntArray("board1");
            board[2] = savedInstanceState.getIntArray("board2");
            board[3] = savedInstanceState.getIntArray("board3");
            board[4] = savedInstanceState.getIntArray("board4");
            board[5] = savedInstanceState.getIntArray("board5");
            board[6] = savedInstanceState.getIntArray("board6");
            board[7] = savedInstanceState.getIntArray("board7");
            board[8] = savedInstanceState.getIntArray("board8");
            board[9] = savedInstanceState.getIntArray("board9");
            board[10] = savedInstanceState.getIntArray("board10");
            board[11] = savedInstanceState.getIntArray("board11");
            stillmask[0] = savedInstanceState.getIntArray("still0");
            stillmask[1] = savedInstanceState.getIntArray("still1");
            stillmask[2] = savedInstanceState.getIntArray("still2");
            stillmask[3] = savedInstanceState.getIntArray("still3");
            stillmask[4] = savedInstanceState.getIntArray("still4");
            stillmask[5] = savedInstanceState.getIntArray("still5");
            stillmask[6] = savedInstanceState.getIntArray("still6");
            stillmask[7] = savedInstanceState.getIntArray("still7");
            stillmask[8] = savedInstanceState.getIntArray("still8");
            stillmask[9] = savedInstanceState.getIntArray("still9");
            stillmask[10] = savedInstanceState.getIntArray("still10");
            stillmask[11] = savedInstanceState.getIntArray("still11");
            chunkmask[0] = savedInstanceState.getIntArray("chunk0");
            chunkmask[1] = savedInstanceState.getIntArray("chunk1");
            chunkmask[2] = savedInstanceState.getIntArray("chunk2");
            chunkmask[3] = savedInstanceState.getIntArray("chunk3");
            chunkmask[4] = savedInstanceState.getIntArray("chunk4");
            chunkmask[5] = savedInstanceState.getIntArray("chunk5");
            chunkmask[6] = savedInstanceState.getIntArray("chunk6");
            chunkmask[7] = savedInstanceState.getIntArray("chunk7");
            chunkmask[8] = savedInstanceState.getIntArray("chunk8");
            chunkmask[9] = savedInstanceState.getIntArray("chunk9");
            chunkmask[10] = savedInstanceState.getIntArray("chunk10");
            chunkmask[11] = savedInstanceState.getIntArray("chunk11");
            temptop = savedInstanceState.getIntArray("temptop");
            templeft = savedInstanceState.getIntArray("templeft");
            piecesmoving = savedInstanceState.getIntArray("piecesmoving");
            connectors = savedInstanceState.getBooleanArray("connectors");
            lastpiecemoving = savedInstanceState.getInt("lastpiecemoving");
            score = savedInstanceState.getInt("score");
            paused = savedInstanceState.getBoolean("paused");
            endoffield = savedInstanceState.getInt("endoffield");
            shrinkdelay = savedInstanceState.getInt("shrinkdelay");
            lastshrink = savedInstanceState.getLong("lastshrink");
            linescompleted = savedInstanceState.getInt("linescompleted");
            linegoal = savedInstanceState.getInt("linegoal");
            pauselastshrink = savedInstanceState.getLong("pauselastshrink");
            replaceit = savedInstanceState.getBooleanArray("replaceit");
            loopspeed = savedInstanceState.getInt("loopspeed");
            int[] places = savedInstanceState.getIntArray("getfrompieces");
            pieces[0] = null;
            pieces[1] = null;
            pieces[2] = null;
            pieces[0] = new PieceCorners(places[0], places[1], places[2], fromsaved(savedInstanceState.getIntArray("piece0fill")));
            pieces[1] = new PieceCorners(places[3], places[4], places[5], fromsaved(savedInstanceState.getIntArray("piece1fill")));
            pieces[2] = new PieceCorners(places[6], places[7], places[6], fromsaved(savedInstanceState.getIntArray("piece2fill")));
        }
        
        gv = new GameView(this);
        
     
        //gets basic finger down, finger up and puts those in global variables
          gv.setOnTouchListener(new OnTouchListener(){
              public boolean onTouch(View v, MotionEvent event) {
                  GameView myview = (GameView) v;
                  int action = event.getAction();
                  boolean piecemoved = false;
                  if(gameover == false){
                      if(action == MotionEvent.ACTION_DOWN){
                          downY = event.getY();
                          downX = event.getX();
                          originaldownY = downY;
                          originaldownX = downX;
                          if(paused == true){
                              paused = false;
                              lastshrink = System.currentTimeMillis() - pauselastshrink;                    
                          }
                      }
                      if(action == MotionEvent.ACTION_MOVE){
                          moveX = event.getX();
                          moveY = event.getY();
                          if(moveX-downX > 1.5*squaresize && paused == false){
                              pieceon = onPiece(downY,downX);
                              downY = moveY;
                              downX = moveX;
                              if(pieceon != -1){
                                  piecesmoving[pieceon] = 6;
                                  lastpiecemoving = pieceon;
                                  piecesorder.add(pieceon);
                              }
                              piecemoved = true;
                          }
                          
                          if(Math.abs(moveY-downY) > squaresize && paused == false){
                              piecemoved = true;
                              if(moveY-downY > 0){
                                  for(int i = 0; i<3; i++){
                                      if(lastpiecemoving == i){
                                          if(piecesmoving[i] == 9){
                                              piecesmoving[i] = 6;
                                          }
                                          else{
                                              piecesmoving[i] = 3;
                                          }
                                      }
                                      else{
                                          if(piecesmoving[i] != 6){
                                              piecesmoving[i] = 2;
                                          }
                                      }
                                  }
                                  downY = moveY;
                                  downX = moveX;
                              }
                              else{
                                   for(int i = 0; i<3; i++){
                                      if(lastpiecemoving == i){
                                          if(piecesmoving[i] == 3){
                                              piecesmoving[i] = 6;
                                          }
                                          else{
                                              piecesmoving[i] = 9;
                                          }
                                      }
                                      else{
                                          if(piecesmoving[i] != 6){
                                              piecesmoving[i] = 8;
                                          }
                                      }
                                  }
                                  downY = moveY;
                                  downX = moveX;
                              }
                          }
                      }
                      if(action == MotionEvent.ACTION_UP){
                          timeDown = event.getDownTime();
                          long timeUp = event.getEventTime();
                          if(timeUp - timeDown<150 && piecemoved == false && paused == false){
                              pieceon = onPiece(downY, downX);
                              if(pieceon != -1){
                                  pieces[pieceon].flipme();
                                  myview.postInvalidate();
                              }
                          }
                          piecemoved = false;
                      }
                  }//gameover == false
                  return true;
              }
          });

        testLoop(gv);
        setContentView(gv);
    }



    public void testLoop(GameView v){
        final GameView inview = v;
        Runnable mover = new Runnable(){public void run(){
            if(paused == false){
                //move chunks
                movechunks();
                //check to see if chunks need to be changed from moving to still
                checkchunks();
                //move pieces
                movepieces();
                ///id cols to remove
                boolean linesgone = false;
                ArrayList<Integer> toremove = colcheck();
                while(toremove.isEmpty() == false){
                    linesgone = true;
                    linescompleted++;
                    int removeit = toremove.remove(0);
                    for(int y = 0; y<12; y++){
                        board[y][removeit] = emptyspace;
                        stillmask[y][removeit] = 0;
                    }
                    for(int x = removeit; x>2; x--){
                        connectors[x] = false;
                    }
                }
                
                //move chunks from stillmask to chunkmask
                //idchunks(leftof);
                
                if(linesgone == true){
                    stilltochunk();
                }
                shrinkField(); //This is going to be a pain with masks
                if(linescompleted >= linegoal){
                    nextLevel();
                }
                
                
            }
            inview.postInvalidate();
        }};
        scheduler.scheduleAtFixedRate(mover, 0, loopspeed, MILLISECONDS);
    }
    
    
    
    
    public void checkpieces(){
        boolean replaceit[] = new boolean[3];
        
        for(int i=0; i<3; i++){
            replaceit[i] = false;
            //need to figure 
            if(pieces[i].left == endoffield-3){
                int topp = pieces[i].top;
                int leftt = pieces[i].left;
                for(int y=0; y<4; y++){
                    for(int x = 0; x<3; x++){
                        if(pieces[i].filled[y][x] != 0){
                            stillmask[(topp+y+12)%12][leftt+x] = 5;
                            replaceit[i] = true;
                        }
                    }
                }
            }
        }
    }
    
   
    
    public void checkchunks(){
        for(int y= 0; y<12; y++){
            for(int x=0; x<endoffield; x++){
                if(x==endoffield-1){
                    if(chunkmask[y][x] != 0){
                        chunktostill(y,x);
                    }
                }
                else{
                    if(stillmask[y][x+1] != 0){
                        chunktostill(y,x);
                    }
                }
            }
        }
    }
    
    //given a chunk point will remove chunk and put to still 
    public void chunktostill(int cy, int cx){
        ArrayList<Point> nextto = new ArrayList<Point>();
        nextto.add(new Point(cx,cy));
        //make everything connected still        
        while(!nextto.isEmpty()){
            Point temp = nextto.remove(0);
            if(chunkmask[temp.y][temp.x] != 0){
                stillmask[temp.y][temp.x] = 5;
                chunkmask[temp.y][temp.x] = 0;
                if(temp.x != 0){
                    if(chunkmask[temp.y][temp.x-1] != 0){
                        nextto.add(new Point(temp.x-1,temp.y));
                    }
                }//!0
                
                if(temp.x != 11){
                    if(chunkmask[temp.y][temp.x+1] != 0){
                        nextto.add(new Point(temp.x+1,temp.y));
                    }
                }//!=11
                if(temp.y != 11){
                    if(chunkmask[temp.y+1][temp.x] != 0){
                        nextto.add(new Point(temp.x,temp.y+1));
                    }
                }
                if(temp.y != 0){
                    if(chunkmask[temp.y-1][temp.x] != 5){
                        nextto.add(new Point(temp.x,temp.y-1));
                    }
                }
            }
        }//!empty
    }
    
    public void idchunks(ArrayList<Integer> lcol){
        while(lcol.isEmpty() == false){
            int removed = lcol.remove(0);
            for(int y=0; y<12; y++){
                for(int x = removed; x>2; x--){
                    if(stillmask[y][x] != 0){
                        chunkmask[y][x] = 6;
                        stillmask[y][x] = 0;
                    }
                }
            }
        }
    }
    
    //goes through and leaves anything connected to the bottom in still as still
    public void stilltochunk(){
        for(int y = 0; y<12; y++){
            for(int x = 0; x<12; x++){
                if(stillmask[y][x] != 0){
                    stillmask[y][x] = 6;
                }
            }
        }
        ArrayList<Point> nextto = new ArrayList<Point>();
        for(int y = 0; y<12; y++){
            if(stillmask[y][11] != 0){
                nextto.add(new Point(11,y));
            }
        }
        
        while(!nextto.isEmpty()){
            Point temp = nextto.remove(0);
            if(stillmask[temp.y][temp.x] == 6){
                stillmask[temp.y][temp.x] = 5;
                
                if(temp.x != 0){
                    if(stillmask[temp.y][temp.x-1] != 5){
                        nextto.add(new Point(temp.x-1,temp.y));
                    }
                }//!0
                if(temp.x != 11){
                    if(stillmask[temp.y][temp.x+1] != 5){
                        nextto.add(new Point(temp.x+1,temp.y));
                    }
                }//!=11
                if(temp.y != 11){
                    if(stillmask[temp.y+1][temp.x] != 5){
                        nextto.add(new Point(temp.x,temp.y+1));
                    }
                }
                if(temp.y != 0){
                    if(stillmask[temp.y-1][temp.x] != 5){
                        nextto.add(new Point(temp.x,temp.y-1));
                    }
                } 
            }
            
        }//!empty
        
        for(int y = 0; y<12; y++){
            for(int x = 0; x<12; x++){
                if(stillmask[y][x] == 6){
                    chunkmask[y][x] = 6;
                    stillmask[y][x] = 0;
                       
                }
                if(stillmask[y][x] == 5){
                    board[y][x] = Color.GRAY;
                }
            }
        }
    }
    
    public void movechunks(){
        for(int x=endoffield-1; x>=0; x--){
            for(int y=11; y>=0; y--){
                if(chunkmask[y][x] == 6){
                    board[y][x+1] = board[y][x];
                    chunkmask[y][x+1] = chunkmask[y][x]; 
                    board[y][x] = emptyspace;
                    chunkmask[y][x] = 0;
                }
            }
        }
    }
    
    public void movepieces(){
        //all right, here is what we do
        //first we get rid of all the pieces from the board
        //next we find what order we want to move the pieces
        //now we go through each piece in order and try to place them
        int[] secondlayer = new int[3];
        for(int i=0; i<3; i++){
            secondlayer[i] = piecesmoving[i];
            temptop[i] = pieces[i].top;
            templeft[i] = pieces[i].left;
        }
        int itworks = 0;
        int notwork = 0;
        while(itworks < 3 && notwork < 100){
            for(int i = 0; i<3; i++){
                removefromboard(i);
            }
            Integer[] order = insertionsort(secondlayer);
            itworks = 0;
            
            
            for(int i=0; i<3; i++){
                int ordernum = order[i];
                if(piececheck(ordernum, secondlayer[ordernum])){
                    placepiece(ordernum,secondlayer[ordernum]);
                    itworks++;
                }
                else{//all order[i] were order[2] previously
                    if(piecesmoving[order[i]] == 9 && secondlayer[order[i]] == 9){
                        secondlayer[order[i]] = 6;
                    }
                    else if(piecesmoving[order[i]] == 9 && secondlayer[order[i]] == 6){
                        secondlayer[order[i]] = 8;
                    }
                    else if(piecesmoving[order[i]] == 9 && secondlayer[order[i]] == 8){
                        secondlayer[order[i]] = 5;
                    }
                    else if(piecesmoving[order[i]] == 3 && secondlayer[order[i]] == 3){
                        secondlayer[order[i]] = 6;
                    }
                    else if(piecesmoving[order[i]] == 3 && secondlayer[order[i]] == 6){
                        secondlayer[order[i]] = 2;
                    }
                    else if(piecesmoving[order[i]] == 3 && secondlayer[order[i]] == 2){
                        secondlayer[order[i]] = 5;
                    }
                    else if(piecesmoving[order[i]] == 6 && secondlayer[order[i]] == 6){
                        secondlayer[order[i]] = 5;
                    }
                    else if(piecesmoving[order[i]] == 2 && secondlayer[order[i]] == 2){
                        secondlayer[order[i]] = 5;
                    }
                    else if(piecesmoving[order[i]] == 8 && secondlayer[order[i]] == 8){
                        secondlayer[order[i]] = 5;
                    }
                    else{
                        secondlayer[order[i]] = 5;
                    }
                }
            }
            
            notwork++;
            
        }
        if(notwork > 50){
            score = 1500;
        }
        setpieces(secondlayer);
        replacepieces();
        
    }
    
    public void setpieces(int[] secondlayer){
        for(int i = 0; i<3; i++){
            pieces[i].top = temptop[i];
            pieces[i].left = templeft[i];
            if(secondlayer[i] == 2 || secondlayer[i] == 8){
                if(piecesmoving[i] == 9 || piecesmoving[i] == 3){
                    int ttop = pieces[i].top;
                    int tleft = pieces[i].left;
                    for(int y=0; y<4; y++){
                        for(int x = 0; x<3; x++){
                            if(pieces[i].filled[y][x] != 0){
                                stillmask[(ttop+y+12)%12][tleft+x] = 5;
                            }
                        }
                    }
                    piecesmoving[i] = 5;
                    replaceit[i] = true;
                }
                else{
                    piecesmoving[i] = 5;
                }
            }
            else if(secondlayer[i] == 9 || secondlayer[i] == 3){
                piecesmoving[i] = 6;
            }
            else if(secondlayer[i] == 6){
                piecesmoving[i] = 6;
            }
            else{
                if(piecesmoving[i] == 9 || piecesmoving[i] == 6 || piecesmoving[i] == 3){
                    int ttop = pieces[i].top;
                    int tleft = pieces[i].left;
                    for(int y=0; y<4; y++){
                        for(int x = 0; x<3; x++){
                            if(pieces[i].filled[y][x] != 0){
                                stillmask[(ttop+y+12)%12][tleft+x] = 5;
                            }
                        }
                    }
                    replaceit[i] = true;
                }
                piecesmoving[i] = 5; 
            }

        }
    }
    
    public Integer[] insertionsort(int[] toorder){
        ArrayList<Integer> finalorder = new ArrayList<Integer>();
        finalorder.add(0);
        if(lessequal(toorder[0], toorder[1])){
            finalorder.add(1);
        }
        else{
            finalorder.add(0,1);
        }
        if(lessequal(toorder[finalorder.get(0)],toorder[2])){
            if(lessequal(toorder[finalorder.get(1)],toorder[2])){
                finalorder.add(2);
            }
            else{
                finalorder.add(1,2);
            }
        }
        else{
            finalorder.add(0,2);
        }
        Integer[] returner = finalorder.toArray(new Integer[0]);
        return returner;
    }
     
     
    //returns true if pivot is less than equal to compare
    public boolean lessequal(int pivot, int compare){
        int[] order = {5,2,8,6,3,9};
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i = 0; i<order.length; i++){
            ids.add(order[i]);
        }
        int pivid = ids.indexOf(pivot);
        int comid = ids.indexOf(compare);
        if(pivid <= comid){
            return true;
        }
        return false;
    }
    
    public void placepiece(int i, int dir){
        int xoffset = 0;
        int yoffset = 0;
        if(dir == 6){
            score++;
            xoffset = 1;
        }
        if(dir== 9){
            score++;
            yoffset = -1;
            xoffset = 1;
        }
        if(dir == 3){
            score++;
            yoffset = 1;
            xoffset = 1;
        }
        if(dir == 2){
            yoffset = 1;
        }
        if(dir == 8){
            yoffset = -1;
        }
        int ttop = pieces[i].top;
        int tleft = pieces[i].left;
        int topp = (ttop+yoffset+12)%12;
        int leftt = (tleft + xoffset + 12) % 12;
        temptop[i] = topp;
        templeft[i] = leftt;
        for(int y=0; y<4; y++){
            for(int x = 0; x<3; x++){
                if(pieces[i].filled[y][x] != 0){
                    board[(topp+y+12)%12][leftt+x] = piececolors[i];
                }
            }
        }
    }   
    
    @Override
    protected void onPause(){
        super.onPause();
        paused = true;
        gv.postInvalidate();
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        paused = true;
        gv.postInvalidate();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(gameover == false){
            savedInstanceState.putIntArray("board0", board[0]);
            savedInstanceState.putIntArray("board1", board[1]);
            savedInstanceState.putIntArray("board2", board[2]);
            savedInstanceState.putIntArray("board3", board[3]);
            savedInstanceState.putIntArray("board4", board[4]);
            savedInstanceState.putIntArray("board5", board[5]);
            savedInstanceState.putIntArray("board6", board[6]);
            savedInstanceState.putIntArray("board7", board[7]);
            savedInstanceState.putIntArray("board8", board[8]);
            savedInstanceState.putIntArray("board9", board[9]);
            savedInstanceState.putIntArray("board10", board[10]);
            savedInstanceState.putIntArray("board11", board[11]);
            savedInstanceState.putIntArray("still0", stillmask[0]);
            savedInstanceState.putIntArray("still1", stillmask[1]);
            savedInstanceState.putIntArray("still2", stillmask[2]);
            savedInstanceState.putIntArray("still3", stillmask[3]);
            savedInstanceState.putIntArray("still4", stillmask[4]);
            savedInstanceState.putIntArray("still5", stillmask[5]);
            savedInstanceState.putIntArray("still6", stillmask[6]);
            savedInstanceState.putIntArray("still7", stillmask[7]);
            savedInstanceState.putIntArray("still8", stillmask[8]);
            savedInstanceState.putIntArray("still9", stillmask[9]);
            savedInstanceState.putIntArray("still10", stillmask[10]);
            savedInstanceState.putIntArray("still11", stillmask[11]);
            savedInstanceState.putIntArray("chunk0", chunkmask[0]);
            savedInstanceState.putIntArray("chunk1", chunkmask[1]);
            savedInstanceState.putIntArray("chunk2", chunkmask[2]);
            savedInstanceState.putIntArray("chunk3", chunkmask[3]);
            savedInstanceState.putIntArray("chunk4", chunkmask[4]);
            savedInstanceState.putIntArray("chunk5", chunkmask[5]);
            savedInstanceState.putIntArray("chunk6", chunkmask[6]);
            savedInstanceState.putIntArray("chunk7", chunkmask[7]);
            savedInstanceState.putIntArray("chunk8", chunkmask[8]);
            savedInstanceState.putIntArray("chunk9", chunkmask[9]);
            savedInstanceState.putIntArray("chunk10", chunkmask[10]);
            savedInstanceState.putIntArray("chunk11", chunkmask[11]);
            savedInstanceState.putIntArray("temptop", temptop);
            savedInstanceState.putIntArray("templeft",  templeft);
            savedInstanceState.putIntArray("piecesmoving", piecesmoving);
            savedInstanceState.putBooleanArray("connectors", connectors);
            savedInstanceState.putInt("lastpiecemoving", lastpiecemoving);
            savedInstanceState.putInt("score", score);
            savedInstanceState.putBoolean("paused", paused);
            savedInstanceState.putInt("endoffield", endoffield);
            savedInstanceState.putInt("shrinkdelay", shrinkdelay);
            savedInstanceState.putLong("lastshrink", lastshrink);
            savedInstanceState.putInt("linescompleted", linescompleted);
            savedInstanceState.putInt("linegoal", linegoal);
            savedInstanceState.putLong("pauselastshrink", pauselastshrink);
            savedInstanceState.putBooleanArray("replaceit", replaceit);
            savedInstanceState.putInt("loopspeed", loopspeed);
            savedInstanceState.putIntArray("piece0fill", pieces[0].filltosaver());
            savedInstanceState.putIntArray("piece1fill", pieces[1].filltosaver());
            savedInstanceState.putIntArray("piece2fill", pieces[2].filltosaver());
            savedInstanceState.putIntArray("getfrompieces", getfrompieces());
        }
        else{
            savedInstanceState.clear();
        }
    }
   
    public int[][] fromsaved(int[] sarray){
        int[][] filled = new int[4][3];
        for(int y = 0; y<4; y++){
            for(int x = 0; x<3; x++){
                filled[y][x] = sarray[(y*3)+x];
            }
        }
        return filled;
    }
            
    public void finishGame(){
        gameover = true;
        score = 0;
        /*
        Intent passer = new Intent(Game.this, GameOver.class);
        passer.putExtra("cScore", score);
        startActivity(passer);
        this.finish();
        */
    }
    
    private ArrayList<int[][]> copyArray(){
        ArrayList<int[][]> copied = new ArrayList<int[][]>();
        for(int i = 0; i<piecearray.size(); i++){
            copied.add(piecearray.get(i));
        }
        return copied;
    }
    
    
    public int[][] nextpiece(){
        int[][] temp = new int[4][3];
        if(pieceshapes.isEmpty() == true){
            pieceshapes = copyArray();
        }
        temp = pieceshapes.remove(randone.nextInt(pieceshapes.size()));
        return temp;
    }
    
           
    public void nextLevel(){
        linegoal = linegoal + 2;
        linescompleted = 0;
        lastshrink = System.currentTimeMillis();
        for(int y = 0; y<12; y++){
            for(int x = 0; x<12; x++){
                board[y][x] = emptyspace;
                stillmask[y][x] = 0;
                chunkmask[y][x] = 0;
            }
        }
        int[][] piece0 = nextpiece();
        int[][] piece1 = nextpiece();
        int[][] piece2 = nextpiece();
        putpieceonboard(piece0,0,0);
        putpieceonboard(piece1,4,1);
        putpieceonboard(piece2,8,2);
        for(int y = 0; y<12; y++){
            if(randone.nextBoolean()){
                 board[y][11] = Color.RED;
             }
         }
        downX = -1;
        downY = -1;
        upX = -1;
        upY = -1;
        moveX = -1;
        moveY = -1;
        timeDown = 0;
        totalmoveX = 0;
        totalmoveY = 0;
        originaldownY = -1;
        originaldownX = -1;
        pieceon = -1;
        endoffield = 12;
        paused = false;
        shrinkdelay = shrinkdelay - 10000;     
    }
      
    
    public void shrinkField(){
        long timenow = System.currentTimeMillis();
        if(timenow-lastshrink > shrinkdelay){
            if(paused == false){
                int emptycolumn = emptycol();
                if(emptycolumn != -1){ 
                    lastshrink = timenow;
                    for(int x=emptycolumn; x<11; x++){
                        for(int y = 0; y<12; y++){
                            board[y][x] = board[y][x+1];
                            stillmask[y][x] = stillmask[y][x+1];
                        }
                    }
                    for(int y=0; y<12; y++){
                        board[y][11] = Color.RED;
                        stillmask[y][11] = 5;
                    }
                    endoffield--;
                }
                else{
                    finishGame();
                }
            }//paused
        }
    }
    
    public int emptycol(){
        for(int x=3; x<12; x++){
            boolean totallyempty = true;
            for(int y = 0; y<12; y++){
                if(board[y][x] != emptyspace){
                    totallyempty = false;
                }
            }
            if(totallyempty == true){
                return x;
            }
        }
        return -1;
    }

    
    /*
    public void fillconnect(int piecenum){
        if(pieces[piecenum].top > 8){
            int topp = pieces[piecenum].top;
            int leftt = pieces[piecenum].left;
            for(int y = 0; y<3; y++){
                if(topp+y == 11){
                    for(int x=0; x<3; x++){
                        if(pieces[piecenum].filled[y][x] != 0 && pieces[piecenum].filled[y+1][x] != 0){
                            connectors[leftt+x] = true;
                        }
                    }
                }
            }
        }
    }
*/
   


    public boolean piececheck(int i, int dir){
        int xoffset = 0;
        int yoffset = 0;
        if(dir == 6){
            xoffset = 1;
        }
        if(dir== 9){
        yoffset = -1;
            xoffset = 1;
        }
        if(dir == 3){
            yoffset = 1;
            xoffset = 1;
        }
        if(dir == 2){
            yoffset = 1;
        }
        if(dir == 8){
            yoffset = -1;
        }
        
        boolean empty = true;
        if(pieces[i].left + 2 == 11 && xoffset == 1){
            return false;
        }
        int topp = pieces[i].top;
        int leftt = pieces[i].left;
        for(int y=0; y<4; y++){
            for(int x = 0; x<3; x++){
                if(pieces[i].filled[y][x] != 0){
                    if(board[(topp+y+yoffset+12)%12][leftt+x+xoffset] != emptyspace){
                        empty = false;
                    }
                }
            }
        }
        return empty; 
    }
   
   

    
    
    public void removefromboard(int i){
        for(int y =0; y<4; y++){
            for(int x= 0; x<3; x++){
                if(pieces[i].filled[y][x] != 0){
                    board[(temptop[i]+y+12)%12][templeft[i]+x] = emptyspace;
                }
            }
        }
    }
     
    

    
    public int blankforreplace(){
        int rowstart = -1;
        for(int i = 0; i<12; i++){
            boolean empty = true;
            for(int y=i; y<i+4; y++){
                for(int x=0;x<3; x++){
                    if(board[y%12][x] != emptyspace){
                        empty = false;
                    }
                }
            }
            if(empty == true){
                rowstart = i;
                return rowstart;
            }
        }
        return rowstart;
    }

    
    public void replacepieces(){
        
        for(int i = 0; i< 3; i++){
            if(replaceit[i]){
                if(piecesorder.isEmpty() == false){
                    piecesorder.remove(0);
                }
                if(lastpiecemoving == i){
                    lastpiecemoving = -1;
                }
                int[][] temp = piecearray.get(2);//nextpiece();
                int rowstart = blankforreplace();
                if(rowstart != -1){
                    putpieceonboard(temp,rowstart,i);
                    piecesmoving[i] = 5;
                    replaceit[i] = false;
                }
                else{
                    ArrayList<Integer> fullcols = colcheck();
                    if(fullcols.size() >= linegoal-linescompleted){
                        nextLevel();
                    }
                    else{
                        finishGame();
                    }
                }
            }
        }
    }
    
        
    
    private int onPiece(float touchY, float touchX){
        //if the touch is on the pieces to the left
        if((touchX > startfieldx) && (touchX < (startfieldx + (squaresize*12)))){

            if((touchY > startfieldy+scoreheight) && (touchY < (startfieldy + scoreheight + (squaresize*12)))){
                //convert touch x y to board x 
                touchY = touchY-startfieldy-scoreheight;
                touchX = touchX-startfieldx;
                //touch yx are now zeroed to the field
                //divide by squaresize to get board yx
                
                int boardY = (int) (touchY/squaresize);
                int boardX = (int) (touchX/squaresize);
                for(int i = 0; i<3; i++){
                    //we want to know if boardY is between the top and the bottom of the piece
                    if(pieces[i].top > 8){
                        if(boardY >= pieces[i].top || boardY <= (pieces[i].top+4)%12){
                            if(boardX >= pieces[i].left && boardX <= pieces[i].left+3){
                                return i;
                            }
                        }
                    }
                    else{
                        if(boardY >= pieces[i].top && boardY <= (pieces[i].top+4)){
                            if(boardX >= pieces[i].left && boardX <= pieces[i].left+3){
                                return i;
                            }
                        }
                    }
                }
            }
            return -1;
        }
        return -1;
    }
    
    public ArrayList<Integer> colcheck(){
        ArrayList<Integer> cols = new ArrayList<Integer>();
        for(int x=3; x<endoffield; x++){
            boolean full = true;
            for(int y = 0; y<12; y++){
                if(stillmask[y][x] == 0){
                    full = false;
                }
            }
            if(full == true){
                cols.add(x);
            }
        }
        return cols;
    }
       
    
    public void putpieceonboard(int[][] piece, int rowstart, int piecenum){
        pieces[piecenum] = null;
        pieces[piecenum] = new PieceCorners(rowstart, 0, 0, piece);
        for(int y = 0; y<4; y++){
            for(int x=0; x<3; x++){
                if(piece[y][x] != 0){
                    board[(rowstart+y)%12][x] = piececolors[piecenum];
                }   
            }
        }
    }

    
    private ArrayList<int[][]> getPieces(Context ctx){
        //each piece is [y][x]
        ArrayList<int[][]> pieces = new ArrayList<int[][]>();
        try{
            InputStream inputStream = ctx.getResources().openRawResource(R.raw.pieces);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader bufferedreader = new BufferedReader(inputreader);
            int i = 0;
            int j = 0;
            String sline;
            int[][] temp = new int[4][3];
            while (((sline = bufferedreader.readLine()) != null) && i<numberofpieces) {
                int line = Integer.parseInt(sline);
                if(line == 1){
                    temp[j][2] = Color.RED;
                }
                if(line == 10){
                   temp[j][1] = Color.RED;
                }
                if(line == 11){
                   temp[j][1] = Color.RED;
                   temp[j][2] = Color.RED;
                }
                if(line == 111){
                   temp[j][0] = Color.RED;
                   temp[j][1] = Color.RED;
                   temp[j][2] = Color.RED;
                }
                if(line == 110){
                    temp[j][0] = Color.RED;
                    temp[j][1] = Color.RED;
                }
                if(line == 100){
                    temp[j][0] = Color.RED; 
                }
                if(j==3) { 
                    i++;
                    pieces.add(temp);
                    int[][] temp2 = new int[4][3];
                    temp = temp2;
                }
                j = (j+1) % 4;                
                }
                bufferedreader.close();
              } catch (IOException e1) {
                System.out.println("not good");
            }
        return pieces;
        }
    
    public int[] getfrompieces(){
        int[] temp = new int[9];
        temp[0] = pieces[0].top;
        temp[1] = pieces[0].left;
        temp[2] = pieces[0].flip;
        temp[3] = pieces[1].top;
        temp[4] = pieces[1].left;
        temp[5] = pieces[1].flip;
        temp[6] = pieces[2].top;
        temp[7] = pieces[2].left;
        temp[8] = pieces[2].flip;
        return temp;
    }
    

    
    public class PieceCorners{
        public int top;
        public int left;
        public int flip;
        public int[][] filled = new int[4][3];
        
        public PieceCorners(int t, int l, int f, int[][] temp){
            top = t;
            left = l;
            flip = f;
            for(int i = 0; i<4; i++){
                for(int j = 0; j<3; j++){
                    filled[i][j] = temp[i][j];
                }
            }
        }
        
        
        
        public int[] filltosaver(){
            int[] temp = new int[12];
            for(int y = 0; y<4; y++){
                for(int x = 0; x<3; x++){
                    temp[(y*3)+x] = filled[y][x];
                }
            }
            return temp;
        }
        
        public void settop(int i){
            top = (i+12)%12;
        }

        public void setleft(int i){
            left = (i+12)%12;
        }
        
        public void flipme(){
            
            boolean goodempty = true;
            for(int y = 0; y<4; y++){
                for(int x = 0; x<3; x++){
                    if(filled[y][x] == 0){
                        if(board[(y+top)%12][x+left] != emptyspace){
                            goodempty = false;
                        }
                    }
                }
            }
            
            if(goodempty == true){
                if(flip == 0 || flip == 2){
                    for(int y = 0; y<4; y++){
                        
                        int lefter = board[(top+y+12)%12][left];
                        board[(top+y+12)%12][left] = board[(top+y+12)%12][left+2];
                        board[(top+y+12)%12][left+2] = lefter;
                        int lefter2 = filled[y][0];
                        filled[y][0] = filled[y][2];
                        filled[y][2] = lefter2;
                        
                    }
                }
                else{
                    for(int y = 0; y<2; y++){
                        for(int x=0; x<3; x++){
                            int topper = board[(top+y+12)%12][left+x];
                            board[(top+y+12)%12][left+x] = board[(top+3-y+12)%12][left+x];
                            board[(top+3-y+12)%12][left+x] = topper;
                            int topper2 = filled[y][x];
                            filled[y][x] = filled[3-y][x];
                            filled[3-y][x] = topper2;
                        }
                    }
                }
            flip = (flip+1) % 4;
            }
        }
    }
    
        
  
    
    private class GameView extends View{
        public GameView(Context context){
            super(context);
        }
        
        public void drawPause(Canvas canvas){
            /*
            Paint gop = new Paint();
            gop.setColor(Color.LTGRAY);
            float startx = startfieldx+(squaresize);
            float starty = startfieldy+scoreheight+(squaresize*4)+4;
            RectF background = new RectF(startx,starty,startx+(squaresize*10)+12,starty+(squaresize*4)+4);
            canvas.drawRect(background, gop);
            gop.setStyle(Style.FILL);
            gop.setColor(Color.BLACK);
            gop.setTextSize(squaresize*2);
            
            canvas.drawText("Resume?", startfieldx+squaresize*2, squaresize*8, gop);
            */
        }
                
        public void drawScore(Canvas canvas){
            Paint scorep = new Paint();
            scorep.setColor(Color.WHITE);
            scorep.setTextAlign(Paint.Align.RIGHT);
            scorep.setTextSize(scoreheight);
            float scorex = startfieldx + (12*squaresize);
            String scoretxt = Integer.toString(score);
            for(int k=0; k<8-scoretxt.length(); k++){
                scoretxt = "0" + scoretxt;
            }
            canvas.drawText(scoretxt, scorex, startfieldy+scoreheight-2, scorep);
            String lineslft = Integer.toString(linegoal-linescompleted) + " Left";
            scorep.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(lineslft, startfieldx, startfieldy+scoreheight-2, scorep);
        }
        
        public void drawField(Canvas canvas){
            float startx = startfieldx;
            float starty = startfieldy+scoreheight;
            RectF background = new RectF(startx,starty,startx+(squaresize*12)+12,starty+(squaresize*12)+12);
            Paint backcolor = new Paint();
            backcolor.setStyle(Style.FILL);
            backcolor.setColor(Color.WHITE);
            canvas.drawRect(background, backcolor);
            for(int x = 0; x<12; x++){
                for(int y=0; y<12; y++){                    
                    if(board[y][x]!=0){
                        Paint piececolor = new Paint();//new Paint(board[y][x]);
                        piececolor.setColor(board[y][x]);
                        piececolor.setStyle(Style.FILL);
                        RectF piecerect = new RectF(startx+1,starty+1,startx+squaresize-1,starty+squaresize-1);
                        canvas.drawRect(piecerect, piececolor);
                    }
                    starty = starty+squaresize+1;
                }
                starty = startfieldy+scoreheight;
                startx = startx+squaresize+1;
            }
        }
        
        @Override
        protected void onDraw(Canvas canvas){
            canvas.drawColor(Color.BLACK);
            drawScore(canvas);
            drawField(canvas);
            if(paused){
                drawPause(canvas);
            }               
        }
    }

}
