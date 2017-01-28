package com.example.mmc.concurrencycontrol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Random;

public class ThreadMessagesActivity extends AppCompatActivity {
    ProgressBar bar1, bar2;

    TextView msgWorking, msgReturned;
    ScrollView myScrollView;

    // this is a control var used by backg. threads
    protected boolean isRunning = false;

    // lifetime (in seconds) for background thread
    protected final int MAX_SEC = 30;

    // global value seen by all threads – add synchronized get/set protected
    int globalIntTest = 0;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnedValue = (String) msg.obj;

            //do something with the value sent by the background thread here
            msgReturned.append("\n returned value: " + returnedValue);
            myScrollView.fullScroll(View.FOCUS_DOWN); //Focuses the scrollview to the last element at the bottom
            bar1.incrementProgressBy(2); // Increaments the progress bar

            //Testing early termination
            if (bar1.getProgress() == MAX_SEC) {
                msgReturned.append(" \nDone \n back thread has been stopped");
                isRunning = false;
            }

            if (bar1.getProgress() == bar1.getMax()) {
                msgWorking.setText("Done");
                bar1.setVisibility(View.INVISIBLE);
                bar2.setVisibility(View.INVISIBLE);
            } else {
                msgWorking.setText("Working.... " + bar1.getProgress());
            }

            //super.handleMessage(msg);
        } // handleMessage
    }; // Handler


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bar1 = (ProgressBar) findViewById(R.id.progress1);
        bar1.setProgress(0);
        bar1.setMax(MAX_SEC);

        bar2 = (ProgressBar) findViewById(R.id.progress2);

        msgWorking = (TextView) findViewById(R.id.txtWorkProgress);
        msgReturned = (TextView) findViewById(R.id.txtReturnedValues);
        myScrollView = (ScrollView) findViewById(R.id.myscroller);

        // set global var (to be accessed by background thread(s) )
        globalIntTest = 1;


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    } // onCreate

    @Override
    protected void onStart() {
        super.onStart();

        // this code creates the background activity where busy work is done
        Thread background = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < MAX_SEC && isRunning; i++) {
                        // try a Toast method here (it will not work!)
                        // fake busy busy work here
                        Thread.sleep(1000); // 1000 msec.

                        // this is a locally generated value between 0-100
                        Random rnd = new Random();
                        int localData = (int) rnd.nextInt(101);

                        // we can see and change (global) class variables [unsafe!]
                        // use SYNCHRONIZED get-set accessor MONITORs
                        String data = "Data- " + getGlobalIntTest() + "-" + localData;
                        increaseGlobalIntTest(1);

                        //Request a message token and put some data in it
                        Message msg = handler.obtainMessage(1, (String) data);

                        // if this thread is still alive send the message
                        if (isRunning) {
                            handler.sendMessage(msg);
                        }
                    }

                } catch (Throwable t) {
                    // just end the background thread
                    isRunning = false;
                }//catch
            }
        }); // Thread


        isRunning = true;
        background.start();
    } // onStart

    public void onStop() {
        super.onStop();
        isRunning = false;
    }//onStop

    public synchronized int getGlobalIntTest() {
        return globalIntTest;
    }

    public synchronized int increaseGlobalIntTest(int inc) {
        return globalIntTest += inc;
    }
} // Class ThreadMessagesActivity
