package com.dstechnologies.vault_dstech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.xml.transform.Result;

import static com.dstechnologies.vault_dstech.R.color.colorAccent;

public class VaultActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE=1001;
    Button browse;
    TextView pathToFile;
    EditText userKey;
    TextView errorMessage;
    Button submit;
    Button decrypt;
    SecretKey finalEncryptionKey;
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
        decrypt = findViewById(R.id.decrypt);
        // when user browses and selects a file to encrypt
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        // when user enters the secret key to encrypt the selected file
        final String partialKeyToEncrypt=userKey.getText().toString();

        // if key length is less than the minimum length prescribed
        if (partialKeyToEncrypt.length()<Integer.valueOf(getString(R.string.size_key_default))){
            userKey.getText().clear();
            errorMessage.setTextColor(R.color.colorPrimary);
            }

        // when user taps on submit button and process of encryption commences.
        submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(pathToFile!=null) {
                    File file = new File(pathToFile.getText().toString());
                    try {
                        ////
                        // Encrypting the file , using Standard Encryption class
                        ////

                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));

                        // Secure calculation of AES encryption key

                        /*
                        If your app needs additional encryption, a recommended approach is to require a passphase or PIN to access your application.
                        This passphrase could be fed into PBKDF2 to generate the encryption key.
                       (PBKDF2 is a commonly used algorithm for deriving key material from a passphrase, using a technique known as “key stretching”.)
                        Android provides an implementation of this algorithm inside SecretKeyFactory as PBKDF2WithHmacSHA1:
                         */
                        finalEncryptionKey = StandardEncryption.generateKey(partialKeyToEncrypt.toCharArray(),StandardEncryption.generateSalt().toString().getBytes());

                        byte[] fileDataBytes = StandardEncryption.encodeFile(finalEncryptionKey,FileUtils.getFileData(pathToFile.getText().toString()));
                        bufferedOutputStream.write(fileDataBytes);
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////
                // Decrypting the file using same key provided by the user
                /////

                decodeFile();
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

    public void decodeFile(){
        try{
            byte[] decodedFileData = StandardEncryption.decodeFile(finalEncryptionKey,readFile());
            if(decodedFileData!=null) {
                Toast.makeText(this, "Your file has been decoded!", Toast.LENGTH_LONG).show();
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public byte[] readFile(){
        byte[] fileContents = null;
        File file = new File(pathToFile.getText().toString());
        int size = (int) file.length();
        fileContents = new byte[size];
        try{
            BufferedInputStream bufferedInputStream =  new BufferedInputStream(new FileInputStream(file));
            try{
                bufferedInputStream.read(fileContents);
                bufferedInputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return fileContents;
    }


}
