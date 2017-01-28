package com.example.mmc.concurrencycontrol;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class AsyncTaskActivity extends AppCompatActivity {

    Button btnSlowWork;
    Button btnQuickWork;
    EditText txtMsg;
    Long startingMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        txtMsg = (EditText) findViewById(R.id.txtMsg);

        // slow work...for example: delete databases: “dummy1” and “dummy2”
        btnSlowWork = (Button) findViewById(R.id.btnSlow);
        this.btnSlowWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VerySlowTask().execute("dummy1", "dummy2");
            }
        });

        btnQuickWork = (Button) findViewById(R.id.btnQuick);
        this.btnQuickWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtMsg.setText((new Date()).toString()); // quickly show today’s date
            }
        });

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

    private class VerySlowTask extends AsyncTask<String, Long, Void> {

        private final ProgressDialog dialog = new ProgressDialog(AsyncTaskActivity.this);
        String waitMsg = "Wait\nSome SLOW job is being done... ";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startingMillis = System.currentTimeMillis();
            txtMsg.setText("Start Time: " + startingMillis);
            this.dialog.setMessage(waitMsg);
            this.dialog.setCancelable(false); //outside touch doesn't dismiss you this.dialog.show();
        }

        @Override
        protected Void doInBackground(final String... args) {
            // show on Log.e the supplied dummy arguments
            Log.e("doInBackground>>", "Total args: " + args.length);
            Log.e("doInBackground>>", "args[0] = " + args[0]);

            try {
                for (Long i = 0L; i < 5L; i++) {
                    Thread.sleep(10000);
                    // simulate the slow job here . . .
                    publishProgress((Long) i);
                }
            } catch (InterruptedException e) {
                Log.e("slow-job interrupted", e.getMessage());
            }

            return null;
        } // doInBackground


        // periodic updates - it is OK to change UI
        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            dialog.setMessage(waitMsg + values[0]);
            txtMsg.append("\nworking..." + values[0]);
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

            // cleaning-up, all done
            txtMsg.append("\nEnd Time:"
                    + (System.currentTimeMillis() - startingMillis) / 1000);
            txtMsg.append("\ndone!");
        }


    } //AsyncTask  i.e our very slow work

} // AsyncTAskActivity (close the activity)
