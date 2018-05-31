package com.example.mmc.concurrencycontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    WebView webview;
    TextView txtMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Webview of some online content
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        demo1TrySpecificUrl();
        //webview = (WebView)findViewById(R.id.webView1);

        //There are two ways of creating and executing a java thread
        // (Style1) Create a common Thread, pass a custom Runnable.
        Runnable myRunnable1 = new MyRunnableClass();
        Thread t1 = new Thread(myRunnable1);
        t1.start();

        //(Style2) Create a custom Thread and overide its run() method
        MyThread t2 = new MyThread();
        t2.start();

    } //onCreate



    // ========= CUSTOM Runnable class ===========
    //You need to implement the Runnable interface and
    // provide a version of its mandatory run() method.
    public class MyRunnableClass implements Runnable{

        @Override
        public void run() {

            //Thread.sleep(1000) fakes busy work,
            // the thread sleeps 1000 milisec.
            try {
                for (int i =100; i<200; i++){
                    Thread.sleep(1000);
                    //Toast.makeText(MainActivity.this, "This is " + 1, Toast.LENGTH_SHORT).show();
                    Log.e("t1: <<runnable>>", "runnable talking" + i);
                }
            } catch (InterruptedException e ){
                Log.e("t1:<<runnable>>", e.getMessage());
            }
        } //run
    } //MyRunnableClass


    // ========== CUSTOM Thread ==================
    // You need to extend Thread and
    // provide a version of its run() method
    public class MyThread extends Thread {
        @Override
        public void run() {
            super.run();

            try{
                for (int i = 200; i < 300; i++) {
                    Thread.sleep(1000); //Fakes busy work, by sleeping for 1000 millisecs
                    Log.e("t2:[thread]", "Thread Talking >> " + i);
                }
            }catch (InterruptedException e){
                Log.e("t2:[thread]", e.getMessage());
            }
        } //run
    } //MyThread


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.threads){
            Intent intent = new Intent(this, ThreadMessagesActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.handlerPostRunnables) {
            Intent toHandlerPostRunnableActivity =  new Intent(this, HandlerPostRunnablesActivity.class);
            startActivity(toHandlerPostRunnableActivity);
        }
        else if(item.getItemId() == R.id.goToAsyncActivity){
            Intent gotToAsyncTaskActivity = new Intent(this, AsyncTaskActivity.class);
            startActivity(gotToAsyncTaskActivity);
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void demo1TrySpecificUrl() {
        webview = (WebView) findViewById(R.id.webView1);
        webview.getSettings().setJavaScriptEnabled(true);
        //webview.setWebViewClient(new WebViewClient()); //try later
        // set ebay.com as "home server" - go do some shopping
        webview.setWebViewClient(new MyWebViewClient(txtMsg, "ebay.com"));
        //webview.setWebViewClient(new MyWebViewClient(txtMsg, "google.com"));
        webview.loadUrl("https://victsomie.github.io/");
        //webview.loadUrl("www.google.com");
        //webview.loadUrl("http://www.amazon.com"); //try later
    }
} //MainActivity
