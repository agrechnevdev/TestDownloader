package com.example.anton.testdownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    File file;
    String address = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4";
    Handler h;
    ProgressDialog pd;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dicrectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            String root = dicrectory.toString();
            file = new File(root + "/video");
        }
        if (file.exists()) file.delete();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Загрузка");
                pd.setMessage("сохранение в:  " + file.getAbsolutePath());
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setIndeterminate(true);
                pd.setMax(100);
                pd.show();

                h = new Handler() {
                    public void handleMessage(Message msg) {
                        pd.setIndeterminate(false);
                        if (pd.getProgress() < pd.getMax()) {
                            pd.incrementProgressBy(msg.what);
                            pd.incrementSecondaryProgressBy(msg.what);

                        } else {
                            pd.dismiss();
                        }
                    }
                };

                DownloadTask as = new DownloadTask();
                as.execute();


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    class DownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Message msg;
            try {
                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    FileOutputStream outputStream = new FileOutputStream(file);
                    InputStream is = conn.getInputStream();


                    int progress = 0;
                    int counter = 0;
                    msg = h.obtainMessage(1, progress , 0);
                    h.sendMessage(msg);

                    int read = 0;
                    byte[] bytes = new byte[5*1024];
                    while ((read = is.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                        progress = counter*100/10248;
                        counter++;
                        msg = h.obtainMessage(1,progress , 0);
                        h.sendMessage(msg);
                    }

                    outputStream.close();
                    is.close();
                }
            } catch (Exception e) {
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast toast = Toast.makeText(MainActivity.this, "Загрузка завершена", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
