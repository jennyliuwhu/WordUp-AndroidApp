package com.example.jialingliu.wordup.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.jialingliu.wordup.BuildConfig;
import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.entities.PullScrollView;
import com.example.jialingliu.wordup.model.RecommendWord;
import com.example.jialingliu.wordup.util.HttpDownloader;
import com.example.jialingliu.wordup.util.TaskUtils;
import com.example.jialingliu.wordup.ws.remote.Api;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jialingliu on 4/14/16.
 */
public class SplashActivity extends Activity {
    private ProgressDialog progress;
    public static RecommendWord recommendWord;

    public static RecommendWord getRecommendWord() {
        return recommendWord;
    }

    public void setRecommendWord(RecommendWord new_recommendWord) {
        recommendWord = new_recommendWord;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.activity_splash);

        getRecommendWordfromServer();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        PullScrollView.mWidth = metric.widthPixels / 2;

        //Display the current version number
        TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
        versionNumber.setText("Version " + BuildConfig.VERSION_NAME);

        TaskUtils.executeAsyncTask(
                new AsyncTask<Object, Object, Object>() {
                    String note;
                    //Version version;
                    @Override
                    protected Object doInBackground(Object... params) {
                        HttpDownloader httpDownloader = new HttpDownloader();
                        note = httpDownloader.download(Api.GET_NOTE);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                        intent.putExtra(MainActivity.NOTE, note);
                                        SplashActivity.this.startActivity(intent);
                                        SplashActivity.this.finish();
                                    }
                                }, 680
                        );
                    }
                }
        );
    }

    public void getRecommendWordfromServer() {
        String urlstr = Api.RECOMMENDAPI;
        GetClass getClass = new GetClass(this);
        getClass.execute(urlstr);
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private Context context;

        public GetClass(Context c){
            this.context = c;
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
//                if(params[0].equals("login")){
                URL url = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                InputStream in = connection.getInputStream();
                String encoding = connection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];
                int len;
                while ((len = in.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                String body = new String(baos.toByteArray(), encoding);

                in.close();
                connection.disconnect();
                if (body != null && body.trim().length() != 0 && !body.trim().equals("No Such Word")){
                    try{
                        JSONObject jsonObject = new JSONObject(body);
                        String word = jsonObject.getString("word");
                        String partOfSpeech = jsonObject.getString("speech");
                        String definition = jsonObject.getString("explanation");
                        String example = jsonObject.getString("example");
                        RecommendWord wordtemp = new RecommendWord();
                        wordtemp.setWord(word);
                        wordtemp.setPhonetic("");
                        wordtemp.setSpeech(partOfSpeech);
                        wordtemp.setExplanation(definition);
                        wordtemp.setExample(example);
//                        wordtemp.save();
                        setRecommendWord(wordtemp);
                    }catch(Exception fe){
                        RecommendWord wordtemp = reservedRecommendWord();
//                        wordtemp.save();
                        setRecommendWord(wordtemp);
                    }
                } else {
                    RecommendWord wordtemp = reservedRecommendWord();
//                    wordtemp.save();
                    setRecommendWord(wordtemp);
                }

                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private RecommendWord reservedRecommendWord() {
        RecommendWord wordtemp = new RecommendWord();
        wordtemp.setWord("luminous");
        wordtemp.setPhonetic("");
        wordtemp.setSpeech("adjective");
        wordtemp.setExplanation("producing or seeming to produce light;\nfilled with light : brightly lit;\nvery bright in color");
        wordtemp.setExample("eg. I saw the cat's luminous eyes in my car's headlights..");
        return wordtemp;
    }
}
