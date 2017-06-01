package pt12.frigidarium;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.UUID;

import pt12.frigidarium.database2.models.CheckExist;
import pt12.frigidarium.database2.models.Stock;
import pt12.frigidarium.database2.models.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnClickListener {


    private static final String TAG = "loginActivity";
    private static final int RC_SIGN_IN = 12;
    public static final String STOCKPREFERNCEKEY = "current_stock";
    private static final int WRITE = 0;// todo
    private static final int READ = 0;  // todo

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private Class<?> nextActivity = MainActivity.class;
    private static SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        TextView tv1= (TextView) findViewById(R.id.text_login);
        TextView tv2= (TextView) findViewById(R.id.text_login2);
        tv1.setText(R.string.login_text);
        tv2.setText(getString(R.string.login_text2));
        tv1.setTextColor(Color.BLUE);
        tv1.setTypeface(Typeface.SERIF);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private void signIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                return;
            }
//            this.signingIn  = false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public static String getCurrentStock(){
        if (pref == null){
            throw new RuntimeException("shared preferences has not been set");
        }
        return pref.getString(STOCKPREFERNCEKEY, "");
    }
    public static void setCurrentStock(String stockUid){
        if (pref == null){
            throw new RuntimeException("shared preferences has not been set");
        }
        pref.edit().putString(STOCKPREFERNCEKEY,stockUid).apply();
    }
    private void updateUI(final FirebaseUser user) {
        pref = getPreferences(Context.MODE_PRIVATE);
        if (user != null) {
            CheckExist<User>  onReadyCallback = new CheckExist<User>() {
                boolean called  = false;
                @Override
                public void onExist(User owner) {
                    if (!called) {
                        called = true;
                        String stockID = getCurrentStock();
                        if (stockID.equals("")){
                            if (owner.getStocks() .size() == 0){//the user is in no stocks
                                String stockUid = UUID.randomUUID().toString();
                                Stock.createStock(new Stock(stockUid, owner.getName()),owner.getUid());
                                User.addUserToStock(FirebaseAuth.getInstance().getCurrentUser().getUid(),stockUid);
                                setCurrentStock(stockUid);
                            }else {
                                for (String stockId : owner.getStocks().values()){// put the first stock in the list as the current stock visible.
                                    setCurrentStock(stockId);
                                    break;
                                }
                            }
                        }
                        goToNextActivity();
                    }
                }

                @Override
                public void onDoesNotExist(String uid) {
                    if (!called) {
                        called = true;
                        User.createUser(new User(user.getUid(), user.getDisplayName()));
                        String stockUid = UUID.randomUUID().toString();
                        Stock.createStock(new Stock(stockUid, user.getDisplayName()),user.getUid());
                        setCurrentStock(stockUid);
                        goToNextActivity();
                    }
                }

                @Override
                public void onError(DatabaseError error) {
                    //// TODO: 25/05/17 handle error
                }
            };
            User.checkExist(user.getUid(), onReadyCallback);
        }
    }

    private void goToNextActivity() {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }
}

