package com.dstechnologies.vault_dstech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;

import javax.xml.transform.Result;

import static com.dstechnologies.vault_dstech.R.color.colorAccent;

public class VaultActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE=1001;
    Button browse;
    TextView pathToFile;
    EditText userKey;
    TextView errorMessage;
    Button submit;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        browse = findViewById(R.id.browse);
        pathToFile= findViewById(R.id.path_to_file);
        userKey = findViewById(R.id.key_user);
        errorMessage = findViewById(R.id.error_msg);
        submit = findViewById(R.id.submit);

        // when user browses and selects a file to encrypt
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        // when user enters the secret key to encrypt the selected file
        String partialKeyToEncrypt=userKey.getText().toString();

        // if key length is less than the minimum length prescribed
        if (partialKeyToEncrypt.length()<Integer.valueOf(getString(R.string.size_key_default))){
            userKey.getText().clear();
            errorMessage.setTextColor(R.color.colorPrimary);
            }

        // when user taps on submit button and process of encryption commences.
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pathToFile!=null) {
                    File file = new File(pathToFile.getText().toString());
                }
            }
        });

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case FILE_SELECT_CODE:
                if (resultCode== RESULT_OK){
                    Uri uri = data.getData();
                    Log.d("file","File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this,uri);
                        pathToFile.setText(path);
                        Log.d("path", "File Path: " + path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
            break;
                }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private void showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {

            startActivityForResult(Intent.createChooser(intent,"Select a file to perform encryption!"),FILE_SELECT_CODE);
            }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(this,"Please install a File Manager",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vault, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
