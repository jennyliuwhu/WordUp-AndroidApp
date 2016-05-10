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
import com.example.jialingliu.wordup.exception.EmptyException;
import com.example.jialingliu.wordup.exception.FormatException;
import com.example.jialingliu.wordup.ws.remote.Api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sagejoyoox on 4/27/16.
 */
public class SignupActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progress;
    public static boolean userexisted = false;

    @Bind(R.id.input_name)
    EditText namesignup;
    @Bind(R.id.input_password)
    EditText passwordsignup;
    @Bind(R.id.btn_signup)
    Button btnsignup;
    @Bind(R.id.link_login)
    TextView loginlink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish registration and return to the Login activity
                finish();
            }
        });
    }

    public boolean isUserexisted() {
        return userexisted;
    }

    public void setUserexisted(boolean new_userexisted) {
        userexisted = new_userexisted;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public void signup() {
        try{
            validate();
        }catch(EmptyException ee){
            Toast.makeText(getBaseContext(), "Name or password cannot be empty!", Toast.LENGTH_LONG).show();
            btnsignup.setEnabled(true);
            return;
        }catch(FormatException fe){
            Toast.makeText(getBaseContext(), "Password should be 4-10 characters!", Toast.LENGTH_LONG).show();
            btnsignup.setEnabled(true);
            return;
        }

        btnsignup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = namesignup.getText().toString();
        String password = passwordsignup.getText().toString();

        //send URL request
        String urlstr = Api.getRegisterAPI(name, password);
//        String urlstr = "http://172.29.92.158:8080/WordUp/register?"+name+","+password;
        GetClass getClass = new GetClass(this);
        getClass.execute("register",urlstr);
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //authenticate the user
        if(isUserexisted()) {
            Toast.makeText(getBaseContext(), "User name already existed, please choose another one.", Toast.LENGTH_LONG).show();
            btnsignup.setEnabled(true);
            return;
        }else {
            Toast.makeText(SignupActivity.this, "Welcome and Enjoy Your WordUp Journey!", Toast.LENGTH_LONG).show();
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        btnsignup.setEnabled(true);
                        setResult(RESULT_OK, null);
                        finish();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    //customized exception handling
    public void validate() throws EmptyException, FormatException {
        String name = namesignup.getText().toString();
        String password = passwordsignup.getText().toString();

        if (name.isEmpty()||password.isEmpty() ) {
            throw new EmptyException();
        }
        if (password.length() < 4 || password.length() > 10) {
            throw new FormatException();
        }
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
                if(params[0].equals("register")){
                    URL url = new URL(params[1]);

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
                    if (body.trim().equals("already existed")){
                        setUserexisted(true);
                    } else {
                        setUserexisted(false);
                    }

                    SignupActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
