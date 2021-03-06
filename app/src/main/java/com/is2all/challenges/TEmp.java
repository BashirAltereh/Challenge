package com.is2all.challenges;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.is2all.challenges.Activities.UsersList;
import com.is2all.challenges.Activities.QuestionsActivity;
import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TEmp extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity__";
    private static final String EMAIL = "email";
    private static final String USER_FIREDS = "user_friends";
    private String name, email;
    private String ID, userID;
    private boolean isLoggedIn;

    private View mVPlayOffline, mVPlayWithFirends;
    private LoginButton mBtnLogIn;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private AccessToken accessToken;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Log.d("SDK_VERSION", FacebookSdk.getSdkVersion());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        init();


        /* make the API call */

        // If you are using in a fragment, call loginButton.setFragment(this);
        Log.d("Result_", "start");
        // Callback registration
        mBtnLogIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("Result_", "onSuccess1: " + loginResult.toString());
                Toast.makeText(getApplicationContext(), "onSuccess1: " + loginResult.toString(), Toast.LENGTH_SHORT).show();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("Result_", "onCancel: ");
                Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("Result_", "onError: " + exception.getMessage());
                Toast.makeText(getApplicationContext(), "onError: " + exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
            }
        };


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("Result_", "onSuccess1: " + loginResult.toString());
                        Toast.makeText(getApplicationContext(), "onSuccess: " + loginResult.toString(), Toast.LENGTH_SHORT).show();
                        handleFacebookAccessToken(loginResult.getAccessToken());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("Result_", "onCancel: ");
                        Toast.makeText(getApplicationContext(), "onCancel", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("Result_", "onError: " + exception.getMessage());
                        Toast.makeText(getApplicationContext(), "onError: " + exception.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

//        Log.d("onComplete_","id: "+accessToken.getUserId());
        printKeyHash(this);


        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();

//        getUsers();


    }

    public void init() {
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();
        mVPlayOffline = findViewById(R.id.v_play_offline);
        mVPlayWithFirends = findViewById(R.id.v_play_with_friends);
        mBtnLogIn = findViewById(R.id.login_button);
        mBtnLogIn.setOnClickListener(this);
        mVPlayOffline.setOnClickListener(this);
        mVPlayWithFirends.setOnClickListener(this);
        // Write a message to the database
        accessToken = AccessToken.getCurrentAccessToken();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("mahmoud");
        myRef.setValue("Full stack developer");

        callbackManager = CallbackManager.Factory.create();
        mBtnLogIn.setReadPermissions(Arrays.asList(EMAIL));
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);

        signInUser();

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListner);
        firebaseAuth.signOut();

    }

    public void returen(View view) {
        MediaPlayer media2 = MediaPlayer.create(this, R.raw.sound_click);
        media2.start();

        Intent windows_asila = new Intent(this, QuestionsActivity.class);
        windows_asila.putExtra("rtn", true);
        startActivity(windows_asila);
    }


    public void Share(View view) {
        MediaPlayer media4 = MediaPlayer.create(this, R.raw.sound_click);
        media4.start();

        Intent myintent = new Intent(Intent.ACTION_SEND);
        myintent.setType("text/plain");
        String body = "تطبيق نسألك وانت تجيب رائع  \n" + "\n" +
                "https://play.google.com/store/apps/com.is2all.as2ila_jawab";
        String sub = "تطبيق نسالك وانت تجيب \n";
        myintent.putExtra(Intent.EXTRA_SUBJECT, sub);
        myintent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(myintent, "مشاركة البرنامج"));
    }


    private boolean isAnonymous() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentUser == null || currentUser.isAnonymous();
    }

    private boolean arePlayServicesOk() {
        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, 5000).show();
            }
            return false;
        }

        return true;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FacebookAccessToken_", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FacebookAccessToken_", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Authentication done.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            Log.d("FacebookAccessToken_", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                        getInfo();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.v_play_offline:
                MediaPlayer media1 = MediaPlayer.create(this, R.raw.sound_click);
                media1.start();

                Intent windows_asila = new Intent(this, QuestionsActivity.class);
                windows_asila.putExtra("rtn", false);
                startActivity(windows_asila);
                break;

            case R.id.v_play_with_friends:
                if (!arePlayServicesOk()) {
                    Toast.makeText(this, "arePlayServices not Ok", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAnonymous()) {
                    Toast.makeText(this, "Anonymous", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

                } else {
                    Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, UsersList.class);
                    startActivity(intent);
//                    ID = firebaseAuth.getCurrentUser().getUid();
//                    Log.d("ID__", "id: " + ID);
//                    List list = getFriendsList();
//                    Log.d("LIST_", list.toString());
//                    fetchUsers();
                    getUsers();
                }

                break;

            case R.id.login_button:
//                if (isLoggedIn)
//                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
                break;
        }
    }


    private List<String> getFriendsList() {
        final List<String> friendslist = new ArrayList<String>();

//                new GraphRequest(accessToken, "/me/friendlists", null, HttpMethod.GET, new GraphRequest.Callback() {
//            public void onCompleted(GraphResponse response) {
//                /* handle the result */
//                Log.d("FriendsList_", response.toString());
//                try {
//                    JSONObject responseObject = response.getJSONObject();
//                    JSONArray dataArray = responseObject.getJSONArray("data");
//
//                    for (int i = 0; i < dataArray.length(); i++) {
//                        JSONObject dataObject = dataArray.getJSONObject(i);
//                        String fbId = dataObject.getString("id");
//                        String fbName = dataObject.getString("name");
//                        Log.d("FbId_", fbId);
//                        Log.d("FbName_", fbName);
//                        friendslist.add(fbId);
//                    }
//                    Log.d("fbfriendList_", friendslist.toString());
//                    List<String> list = friendslist;
//                    String friends = "";
//                    if (list != null && list.size() > 0) {
//                        friends = list.toString();
//                        if (friends.contains("[")) {
//                            friends = (friends.substring(1, friends.length() - 1));
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                }
//            }
//        }).executeAsync();

//        taggable_friends
//        invitable_friends
        final GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + "me" + "/friends",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here

                        Log.d("FriendsList_", "ID: " + userID + response.toString());

                    }
                });

        request.executeAsync();


        GraphRequest request2 = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/feed",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        Log.d("onCompleted_", response.toString());
                    }
                });

        request2.executeAsync();


        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/801520253539954",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.d("onCompleted_1", response.toString());

                    }
                }
        ).executeAsync();
        return friendslist;
    }

    public void getInfo() {
        // App code
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity_", response.toString());
                        if (object == null)
                            return;
                        // Application code
                        try {
                            userID = object.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            name = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            email = object.getString("email");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String birthday = object.getString("birthday"); // 01/31/1980 format
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        createUser(userID, name, email);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void fetchUsers() {
// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("fetchUsers_", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("fetchUsers_", "Failed to read value.", error.toException());
            }
        });


        //        FirebaseDatabase.getInstance().getReference().child("users")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String value = dataSnapshot.getValue(String.class);
//                      Log.d("fetchUsers_","count: "+value);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                      Log.d("fetchUsers_",databaseError.toString());
//
//                    }
//                });
    }


    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.d("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.d("Key Hash=", "key: " + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.d("NameNotFound", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("NoSuchAnAlgorithm", e.toString());
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }

        return key;
    }


    public void createUser(final String id, final String name, final String email) {
        Log.d("createUser_", "id: " + id + " , name: " + name + " , email: " + email);
        mAuth.createUserWithEmailAndPassword(email, id)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("createUser_", "task: " + task.toString());
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            User user = new User(email, name, id, uid);
                            Gson gson = new Gson();
                            String json = gson.toJson(user);
                            editor.putString("user", json);
                            editor.commit();
                            Log.d("createUser_", "singUpWithEmail successfully " + json);

                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                    .setValue(user);

                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            Utils.savePushToken(refreshedToken, uid);

//                        startActivity(new Intent(this, MainActivity.class)
//                                .putExtra("type", "wifi"));

//                        startActivity(new Intent(this, UserListActivity.class));

                        } else {
                            Log.d("createUser_", "loginWithEmail: unsuccessful");
                            mAuth.signInWithEmailAndPassword(email, id)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String uid = mAuth.getCurrentUser().getUid();

                                                User user = new User(email, name, id, uid);

                                                Gson gson = new Gson();
                                                String json = gson.toJson(user);
                                                editor.putString("user", json);
                                                editor.commit();

                                                Log.d("createUser_", "singInWithEmail successfully " + json);
                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                                        .setValue(user);

                                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                                Utils.savePushToken(refreshedToken, uid);

                                            }

//                                            if (!isAnonymous()) {
//                                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//                                                Utils.savePushToken(refreshedToken, Utils.getCurrentUserId());
//                                                Log.d("createUser_", "isAnonymous");
//
////                                        startActivity(new Intent(this, MainActivity.class)
////                                                .putExtra("type", "wifi"));
//
//
//                                            }
                                            Log.d("createUser_", "unknown");
                                        }
                                    });
                        }
                    }
                });
    }


    public void getUsers() {
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 1;
                        Log.d("getUsers_", dataSnapshot.toString());

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            Log.d("List_", "id: " + user.getId() + " , name: " + user.getName() + " , email: " + user.getEmail());

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("getUsers_", databaseError.toString());

                    }
                });
    }
    public void signInUser(){
        final String tUser = sharedPreferences.getString("user", "null");
        if (tUser.equals("null"))
            return;
        Gson gson = new Gson();
        final User user = gson.fromJson(tUser, User.class);
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getId())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            Log.d("createUser_", "singInWithEmail successfully ONCreate: "+tUser);
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                    .setValue(user);

                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            Utils.savePushToken(refreshedToken, uid);

                        }
                        Log.d("createUser_", "unknown");
                    }
                });
    }


}
