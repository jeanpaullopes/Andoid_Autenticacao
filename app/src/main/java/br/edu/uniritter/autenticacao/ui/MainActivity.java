package br.edu.uniritter.autenticacao.ui;

import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import br.edu.uniritter.autenticacao.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth autenticadorFirebase;
    private FirebaseUser usuarioFirebase;

    private BiometricManager biometricManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp fapp = FirebaseApp.initializeApp(this);
        findViewById(R.id.textViewCriar).setOnClickListener((view)->{
            findViewById(R.id.cardLogin).setVisibility(View.INVISIBLE);
            findViewById(R.id.cardNewUser).setVisibility(android.view.View.VISIBLE);
        });
        findViewById(R.id.buttonNewOk).setOnClickListener((view)->{
            String email = ((android.widget.EditText)findViewById(R.id.edNewEmail)).getText().toString();
            String password = ((android.widget.EditText)findViewById(R.id.edNewSenha)).getText().toString();
            criarUsuario(email, password);
        });
        findViewById(R.id.buttonLoginOK).setOnClickListener((view)->{
            String email = ((android.widget.EditText)findViewById(R.id.edMail)).getText().toString();
            String password = ((android.widget.EditText)findViewById(R.id.edSenha)).getText().toString();
            logarUsuario(email, password);
        });

        findViewById(R.id.buttonLogout).setOnClickListener((view)->{
            autenticadorFirebase.signOut();
            usuarioFirebase = null;
            noUser();
        });
        usuarioFirebase = null;

        biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e(TAG, "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, 1);
                break;
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        autenticadorFirebase = FirebaseAuth.getInstance();
        usuarioFirebase = autenticadorFirebase.getCurrentUser();
        if (usuarioFirebase == null) {
            noUser();
        } else {
            login();
        }
    }
    private void noUser() {
        findViewById(R.id.cardLogin).setVisibility(android.view.View.VISIBLE);
        //startActivity(new Intent(this, LoginActivity.class));
        //finish();
    }


    private void logarUsuario(String email, String password) {
        autenticadorFirebase.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            usuarioFirebase = autenticadorFirebase.getCurrentUser();
                            login();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private void criarUsuario(String email, String password) {
        autenticadorFirebase.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            usuarioFirebase = autenticadorFirebase.getCurrentUser();
                            login();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void login() {
        if (usuarioFirebase != null) {
            Toast.makeText(this, "foi, logado "+usuarioFirebase.getEmail(), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, HomeActivity.class));
            //finish();


            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(MainActivity.this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(getApplicationContext(),
                                    "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(getApplicationContext(), "Authentication failed",
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build();
            biometricPrompt.authenticate(promptInfo);

        }
    }
}