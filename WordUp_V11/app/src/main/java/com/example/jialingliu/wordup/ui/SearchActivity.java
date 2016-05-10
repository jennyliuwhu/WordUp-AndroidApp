package com.example.jialingliu.wordup.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jialingliu.wordup.R;
import com.example.jialingliu.wordup.exception.ReturnEmptyException;
import com.example.jialingliu.wordup.model.FavoriteWord;
import com.example.jialingliu.wordup.model.Word;
import com.example.jialingliu.wordup.util.ToastUtils;
import com.example.jialingliu.wordup.ws.remote.Api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jialingliu on 4/27/16.
 */

public class SearchActivity extends AppCompatActivity {
    private static final int REQUEST_SEARCH = 0;
    private ProgressDialog progress;
    public static boolean gotWord = false;
    public String body;
    @Bind(R.id.input_word) EditText wordsearch;
    @Bind(R.id.btn_search) Button btnsearch;
    @Bind(R.id.btn_favorite) Button btnfavorite;
    @Bind(R.id.link_main) TextView mainlink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        btnfavorite.setEnabled(false);
        btnfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite();
            }

        });
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }

        });

        //redirect to main page
        mainlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH);
            }
        });
    }
    public boolean isGotWord() {
        return gotWord;
    }

    public void setGotWord(boolean new_gotWord) {
        gotWord = new_gotWord;
    }

    private String parseJSONString() {
        try {
            JSONArray jArr = new JSONArray(body);
            StringBuilder sb = new StringBuilder(jArr.get(0).toString());
            sb.append(":\n");
            for (int i = 1; i < jArr.length(); i++) {
                sb.append(i).append(".").append("\n");
                JSONObject jsonObject = (JSONObject) jArr.get(i);
                String definition = jsonObject.getString("definition");
                String speech = jsonObject.getString("partOfSpeech");
                String example = jsonObject.getString("example");
                if (definition.length() > 0) {
                    sb.append("definition:").append(definition).append("\n");
                }
                if (speech.length() > 0) {
                    sb.append("speech:").append(speech).append("\n");
                }
                if (example.length() > 0) {
                    sb.append("example:").append(example).append("\n");
                }
                //System.out.println(sb.toString());
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public void favorite() {
        final Word word = getWord();
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setTitle("Add to which list?");
        materialDialog.setMessage("Press a button to confirm");
        materialDialog.setNegativeButton("Memorized", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveWord0(materialDialog, word);
                    }
                }
        );
        materialDialog.setPositiveButton("Favorite", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveWord(materialDialog, word);
                    }
                }
        );
        materialDialog.show();
    }
    private void saveWord(MaterialDialog materialDialog, Word word) {
        List<FavoriteWord> words = DataSupport.where("word = ?", word.getWord()).find(FavoriteWord.class);
        if (!words.isEmpty()) {
            ToastUtils.showShort("Already in your favorite");
            materialDialog.dismiss();
            return;
        }

        ToastUtils.showShort("Add to Favorite");
        materialDialog.dismiss();
        FavoriteWord favoriteWord = new FavoriteWord(word);
        favoriteWord.save();
    }
    private void saveWord0(MaterialDialog materialDialog, Word word) {
        ToastUtils.showShort("Add to Memorized");
        materialDialog.dismiss();
        word.save();
    }
    private Word getWord() {
        Word word = new Word();
        StringBuilder def = new StringBuilder();
        StringBuilder sp = new StringBuilder();
        StringBuilder ex = new StringBuilder();
        try {
            JSONArray jArr = new JSONArray(body);
            String prefix = "";
            String self = jArr.getString(0);
            for (int i = 1; i < jArr.length(); i++) {
                JSONObject jsonObject = (JSONObject) jArr.get(i);
                String definition = jsonObject.getString("definition");
                String speech = jsonObject.getString("partOfSpeech");
                String example = jsonObject.getString("example");
                def.append(prefix).append(definition);
                sp.append(prefix).append(speech);
                ex.append(prefix).append(example);
                prefix=";";
            }
            word.setWord(self);
            word.setExplanation(def.toString());
            word.setSpeech(sp.toString());
            word.setExample(ex.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return word;
    }

    public void search() {
        final TextView outputView = (TextView) findViewById(R.id.showOutput);
        if (!validate()) {
            Toast.makeText(getBaseContext(), "Word cannont be empty. Please retry", Toast.LENGTH_LONG).show();
            btnsearch.setEnabled(true);
            return;
        }

        btnsearch.setEnabled(false);

        String word = wordsearch.getText().toString();

        //send URL request
        String urlstr = Api.getSearchAPI(word);
        GetClass getClass = new GetClass(this);
        getClass.execute(urlstr);
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }

        //authenticate the user
        if(isGotWord()) {
            Toast.makeText(SearchActivity.this, "Got Successfull", Toast.LENGTH_LONG).show();
            System.out.println("body="+body);
            outputView.setText(parseJSONString());
            btnsearch.setEnabled(true);
            btnfavorite.setEnabled(true);
        } else {
            try{
                throw new ReturnEmptyException();
            }catch (ReturnEmptyException ree){
                Toast.makeText(SearchActivity.this, "No Such Word", Toast.LENGTH_LONG).show();
                btnsearch.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public boolean validate() {
        boolean bool = true;

        String word = wordsearch.getText().toString();

        if (word.isEmpty()) {
            wordsearch.setError("Word cannont be empty");
            bool = false;
        }
        return bool;
    }

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public GetClass(Context c){
            this.context = c;
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

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
                body = new String(baos.toByteArray(), encoding);
                in.close();
                connection.disconnect();
                if (!body.trim().equals("No Such Word")){
                    setGotWord(true);
                } else {
                    setGotWord(false);
                }
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}