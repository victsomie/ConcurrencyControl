package com.example.mmc.concurrencycontrol;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class HandlerPostRunnablesActivity extends AppCompatActivity {

    ProgressBar myBarHorizontal;
    ProgressBar myBarCircular;
    TextView lblTopCaption;
    EditText txtDataBox;
    Button btnDoSomething;
    Button btnDoItAgain;

    int progressStep = 5;
    final int MAX_PROGRESS = 100;

    int globalVar = 0;
    int accum = 0;
    long startingMills = System.currentTimeMillis();
    boolean isRunning = false;

    String PATIENCE = "Some important data is being collected now. " + "\nPlease be patient...wait...\n ";
    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_post_runnables);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lblTopCaption = (TextView) findViewById(R.id.lblTopCaption);

        myBarHorizontal = (ProgressBar) findViewById(R.id.myBarHor);
        myBarCircular = (ProgressBar) findViewById(R.id.myBarCir);
        txtDataBox = (EditText) findViewById(R.id.txtBox1);
        txtDataBox.setHint(" Foreground distraction\n Enter some data here...");
        btnDoItAgain = (Button) findViewById(R.id.btnDoItAgain);
        btnDoItAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
            }//
        });// setOnClickListener

        btnDoSomething = (Button) findViewById(R.id.btnDoSomething);
        btnDoSomething.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = txtDataBox.getText().toString();
                Toast.makeText(HandlerPostRunnablesActivity.this, "I’m quick - You said >> \n" + text, Toast.LENGTH_SHORT).show();

            }// onClick
        });// setOnClickListener


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                               .setAction("Action", null).show();
                                   }
                               }

        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // prepare UI components
        txtDataBox.setText("");
        btnDoItAgain.setEnabled(false);

        // reset and show progress bars
        accum = 0;
        myBarHorizontal.setMax(MAX_PROGRESS);
        myBarHorizontal.setProgress(0);
        myBarHorizontal.setVisibility(View.VISIBLE);
        myBarCircular.setVisibility(View.VISIBLE);

        // create-start background thread were the busy work will be done
        Thread myBackgroundThread = new Thread(backgroundTask, "backAlias1");
        myBackgroundThread.start();
    }

    // FOREGROUND
    // this foreground Runnable works on behave of the background thread,
    // its mission is to update the main UI which is unreachable to back worker
    private Runnable foregroundRunnanble = new Runnable() {
        @Override
        public void run() {
            try {
                // update UI, observe globalVar is changed in back thread
                lblTopCaption.setText(PATIENCE + "\nPct progress: " + accum + " globalVar: " + globalVar);
                // advance ProgressBar
                myBarHorizontal.incrementProgressBy(progressStep);
                accum += progressStep;

                if (accum >= myBarHorizontal.getMax()) {
                    lblTopCaption.setText("Slow background work over");
                    myBarHorizontal.setVisibility(View.INVISIBLE);
                    myBarCircular.setVisibility(View.INVISIBLE);
                    btnDoItAgain.setEnabled(true);
                }

            } catch (Exception e) {
                Log.e("<<foregroundTask>>", e.getMessage());
            }
        }
    }; //Runnable foregroundRunnable

    // BACKGROUND
    // This is the back runnable that executes the slow work
    private Runnable backgroundTask = new Runnable() {
        @Override
        public void run() {

            // Start the busy work here
            try{
                for (int i = 0; i < 20; i++) {
                    // Simulate 1sec of some busy work
                    Thread.sleep(1000);

                    //Change the global varuable (globalVar)
                    globalVar++; // increaments the global variable

                    // try: next two UI operations should NOT work //
                    // Toast.makeText(getApplication(), "Hi ", 1).show();
                    // txtDataBox.setText("Hi ");


                    // Wake the foreground task to speak for you
                    myHandler.post(foregroundRunnanble);
                }

            }catch (Exception e){
                Log.e("<<backgroundTask>>", e.getMessage());
            }

        } //run
    }; // backgroundTask

}
