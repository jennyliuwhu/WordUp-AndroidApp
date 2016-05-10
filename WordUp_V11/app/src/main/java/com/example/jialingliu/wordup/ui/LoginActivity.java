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
public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_SKIP = 0;

    private ProgressDialog progress;
    public static boolean passwordmatch = false;


    @Bind(R.id.input_name_login) EditText namelogin;
    @Bind(R.id.input_password_login) EditText passwordlogin;
    @Bind(R.id.btn_login) Button btnlogin;
    @Bind(R.id.link_signup) TextView signuplink;
    @Bind(R.id.link_skip) TextView skiplink;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }

        });

        //redirect to register page
        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
        // redirect to main page
        skiplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_SKIP);
            }
        });
    }

    public boolean isPasswordmatch() {
        return passwordmatch;
    }

    public void setPasswordmatch(boolean new_passwordmatch) {
        passwordmatch = new_passwordmatch;
    }

    public void login() {
        try{
            validate();
        }catch(EmptyException ee){
            Toast.makeText(getBaseContext(), "Name or password cannot be empty!", Toast.LENGTH_LONG).show();
            btnlogin.setEnabled(true);
            return;
        }catch(FormatException fe){
            Toast.makeText(getBaseContext(), "Password should be 4-10 characters!", Toast.LENGTH_LONG).show();
            btnlogin.setEnabled(true);
            return;
        }

        btnlogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        progressDialog.dismiss();

        String name = namelogin.getText().toString();
        String password = passwordlogin.getText().toString();

        //send URL request
        String urlstr = Api.getLoginAPI(name);
//        String urlstr = "http://172.29.92.158:8080/WordUp/login?"+name;
        GetClass getClass = new GetClass(this);
        getClass.execute("login",password,urlstr);
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //authenticate the user
        if(isPasswordmatch()) {
            Toast.makeText(LoginActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            btnlogin.setEnabled(true);
                            finish();
                        }
                    }, 3000);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        } else {
            Toast.makeText(LoginActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
            btnlogin.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    //customized exception handling
    public void validate() throws EmptyException, FormatException {

        String name = namelogin.getText().toString();
        String password = passwordlogin.getText().toString();

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
                if(params[0].equals("login")){
                    URL url = new URL(params[2]);

                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                    connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                    InputStream in = connection.getInputStream();
                    String encoding = connection.getContentEncoding();
                    encoding = encoding == null ? "UTF-8" : encoding;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buf = new byte[2048];
                    int len = 0;
                    while ((len = in.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                    String body = new String(baos.toByteArray(), encoding);

                    in.close();
                    connection.disconnect();
                    if (body.trim().equals(params[1])){
                        setPasswordmatch(true);
                    } else {
                        setPasswordmatch(false);
                    }

                    LoginActivity.this.runOnUiThread(new Runnable() {
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
