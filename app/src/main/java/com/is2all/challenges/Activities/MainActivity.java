package com.is2all.challenges.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.drawee.backends.pipeline.Fresco;
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
import com.is2all.challenges.Helper.CommunicationType;
import com.is2all.challenges.Helper.GAME;
import com.is2all.challenges.Helper.Utils;
import com.is2all.challenges.OnGetEmail;
import com.is2all.challenges.OnNeedCommunicate;
import com.is2all.challenges.OnStartGame;
import com.is2all.challenges.R;
import com.is2all.challenges.fragments.DialogEmail;
import com.is2all.challenges.fragments.DialogInfo;
import com.is2all.challenges.fragments.DialogVPN;
import com.is2all.challenges.fragments.DialogStartGame;
import com.is2all.challenges.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnStartGame, OnGetEmail, OnNeedCommunicate {
    private static final String TAG = "MainActivity__";
    private static final String EMAIL = "email";
    private static final String USER_FIREDS = "user_friends";
    private String name, email;
    private String ID, userID;
    private boolean isLoggedIn;

    private View mVPlayOffline, mVPlayWithFirends, mView;
    private ImageView mIvLOGO, mIvClick, mIvInfo;
    private LoginButton mBtnLogIn;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private AccessToken accessToken;

    private static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private DialogStartGame framgent;
    private DialogEmail dialogEmail;
    private DialogVPN dialogVPN;
    private DialogInfo dialogInfo;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Activity activity;
    static EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Log.d("SDK_VERSION", FacebookSdk.getSdkVersion());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        init();
        editText = findViewById(R.id.et_hash_key);

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
        final boolean isSignIn = sharedPreferences.getBoolean("isSignIn", false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && !isSignIn) {
//                    Toast.makeText(getApplicationContext(), getString(R.string.sign_in_successfully), Toast.LENGTH_SHORT).show();
                    Toasty.success(getApplicationContext(), getString(R.string.sign_in_successfully), Toast.LENGTH_SHORT, true).show();
                    editor.putBoolean("isSignIn", true);
                    editor.commit();
                } else if (!isSignIn)
//                    Toast.makeText(getApplicationContext(), getString(R.string.deos_not_sign_in), Toast.LENGTH_SHORT).show();
                    Toasty.warning(getApplicationContext(), getString(R.string.deos_not_sign_in), Toast.LENGTH_SHORT, true).show();

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

        activity = this;

        mAuth = FirebaseAuth.getInstance();
        mVPlayOffline = findViewById(R.id.v_play_offline);
        mVPlayWithFirends = findViewById(R.id.v_play_with_friends);
        mBtnLogIn = findViewById(R.id.login_button);
        mView = findViewById(R.id.linearLayout);
        mIvLOGO = findViewById(R.id.iv_logo);
        mIvClick = findViewById(R.id.iv_click);
        mIvInfo = findViewById(R.id.iv_info);

        mIvInfo.setOnClickListener(this);
        mBtnLogIn.setOnClickListener(this);
        mVPlayOffline.setOnClickListener(this);
        mVPlayWithFirends.setOnClickListener(this);

        // Write a message to the database
        accessToken = AccessToken.getCurrentAccessToken();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("mahmoud");
        myRef.setValue("Full stack developer");


        mView.setVisibility(View.GONE);
        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.scale);
        final Animation move = AnimationUtils.loadAnimation(this, R.anim.move);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mView.getVisibility() != View.VISIBLE) {
                    mView.setVisibility(View.VISIBLE);
                    mView.startAnimation(slideUp);
                    mIvLOGO.startAnimation(move);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mBtnLogIn.getText().toString().contains("Facebook"))
                                showClickForFacebook();
                        }
                    }, 700);
                }
            }
        }, 500);


        callbackManager = CallbackManager.Factory.create();
        mBtnLogIn.setReadPermissions(EMAIL/*, "public_profile", "user_friends"*/);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
        String s = sharedPreferences.getString("user", "null");
        Log.d("ONSTART_", "user: " + s);
        if (!s.equals("null")) {
            User user = Utils.convertToObject(s);
            createUser(user.getId(), user.getName(), user.getEmail());
        }
        signInUser();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        firebaseAuth.removeAuthStateListener(firebaseAuthListner);
//        firebaseAuth.signOut();
        Log.d("onStop_", "onStop");
    }

    public void returen(View view) {
        MediaPlayer media2 = MediaPlayer.create(this, R.raw.sound_click);
        media2.start();

        Intent windows_asila = new Intent(this, com.is2all.challenges.Activities.windows_asila.class);
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

//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d("FacebookAccessToken_", "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(getApplicationContext(), "Authentication done.",
//                                    Toast.LENGTH_SHORT).show();
//                            User tUser = getUserInfo();
//                            Log.d("FacebookAccessToken_","info: "+Utils.convertToJSon(tUser));
//                            addUserToDataBase(tUser.getEmail(), tUser.getName(),tUser.getId());
//
////                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//
//                            Log.d("FacebookAccessToken_", "signInWithCredential:failure", task.getException());
//                            Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
        getInfo();
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
                mVPlayOffline.setClickable(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVPlayOffline.setClickable(true);
                    }
                }, 1000);
                MediaPlayer media1 = MediaPlayer.create(this, R.raw.sound_click);
                media1.start();
                framgent = new DialogStartGame(this, this, this);

                framgent.show(getSupportFragmentManager(), TAG);

                break;

            case R.id.v_play_with_friends:
                if (!arePlayServicesOk()) {
                    Toast.makeText(this, "arePlayServices not Ok", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAnonymous()) {
                    showClickForFacebook();
//                    Toast.makeText(this, "Anonymous", Toast.LENGTH_SHORT).show();
//                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL,"public_profile"));

                } else {
//                    Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
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

            case R.id.iv_info:
                dialogInfo = new DialogInfo(this, this);
                dialogInfo.show(getSupportFragmentManager(), TAG);
                break;
        }
    }

    private void showClickForFacebook() {
        mIvClick.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mIvClick.startAnimation(animation);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIvClick.setVisibility(View.GONE);
            }
        }, 1000);
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

    public User getUserInfo() {
        final User user = new User();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity_", response.toString());
                        if (object == null) {
                            Toast.makeText(getApplicationContext(), "Can't get your account, please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Application code
                        try {
                            user.setId(object.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setName(object.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setEmail(object.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
        return user;
    }

    public void getInfo() {
        // App code
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity_", response.toString());
                        if (object == null) {
                            Toast.makeText(getApplicationContext(), "Can't get your account, please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
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
                            String temp = object.getString("email");
                            if (temp != null && !temp.equals("null"))
                                email = temp;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String birthday = object.getString("birthday"); // 01/31/1980 format
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("INFO_", "user: " + Utils.convertToJSon(new User(email, name, userID, "")));
                        Toast.makeText(getApplicationContext(), "user: " + Utils.convertToJSon(new User(email, name, userID, "")), Toast.LENGTH_LONG).show();
                        editor.putString("user", Utils.convertToJSon(new User(email, name, userID, "")));
                        editor.commit();

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
        editText.setText(key);


        return key;
    }


    public void createUser(final String id, final String name, final String email) {
        Log.d("createUser_", "id: " + id + " , name: " + name + " , email: " + email);
        if (email == null || name == null) {
            Toast.makeText(this, "Problem in your email", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, id)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String exception;
                        try {
                            exception = task.getException().toString();
                        } catch (Exception e) {
                            exception = "";
                        }
                        if (exception.contains("403")) {
//                            Toast.makeText(getApplicationContext(), "need VPN", Toast.LENGTH_SHORT).show();
                            dialogVPN = new DialogVPN(activity);
                            try {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            dialogVPN.show(getSupportFragmentManager(), TAG);
                                        } catch (Exception e) {
                                        }

                                    }
                                }, 1000);
                            } catch (Exception e) {
                            }
                            return;
                        }
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
                            firebaseAuth.addAuthStateListener(firebaseAuthListner);

                        } else {
                            exception = task.getException().toString();
//                                Toast.makeText(getApplicationContext(),"need VPN: "+task.getException(),Toast.LENGTH_SHORT).show();

//                            if (exception.contains("403")) {
////                                Toast.makeText(getApplicationContext(),"need VPN",Toast.LENGTH_SHORT).show();
//                                dialogVPN = new DialogVPN(activity);
//                                try {
//                                    dialogVPN.show(getSupportFragmentManager(), TAG);
//                                } catch (Exception e) {
//                                }
//                            }
                            Log.d("createUser_", "loginWithEmail: unsuccessful:wowowowow " + task.getException());
                            mAuth.signInWithEmailAndPassword(email, id)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Log.d("createUser_", "task: " + task.toString());

                                            if (task.isSuccessful()) {
                                                addUserToDataBase(email, name, id);

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
                            firebaseAuth.addAuthStateListener(firebaseAuthListner);
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

    public void signInUser() {
        final String tUser = sharedPreferences.getString("user", "null");
        if (tUser.equals("null"))
            return;
        Gson gson = new Gson();
        final User user = gson.fromJson(tUser, User.class);
        if (user.getEmail() == null || user.getName() == null) {
            Toasty.warning(getApplicationContext(), R.string.email_problem, Toast.LENGTH_SHORT, true).show();
            dialogEmail = new DialogEmail(this, this, this);

            dialogEmail.setCancelable(false);
            dialogEmail.show(getSupportFragmentManager(), TAG);
            return;
        }
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getId())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            Log.d("createUser_", "singInWithEmail successfully ONCreate: " + tUser);
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                                    .setValue(user);

                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            Utils.savePushToken(refreshedToken, uid);

                        }
                        Log.d("createUser_", "unknown");
                    }
                });
    }


    @Override
    public void onStartGameListner(GAME game) {
        if (game == GAME.NEW_GAME) {
            MediaPlayer media2 = MediaPlayer.create(this, R.raw.sound_click);
            media2.start();

            Intent windows_asila = new Intent(this, windows_asila.class);
            windows_asila.putExtra("rtn", false);
            startActivity(windows_asila);
            framgent.dismiss();

        } else if (game == GAME.RESUME) {

            MediaPlayer media2 = MediaPlayer.create(this, R.raw.sound_click);
            media2.start();

            Intent windows_asila = new Intent(this, windows_asila.class);
            windows_asila.putExtra("rtn", true);
            startActivity(windows_asila);
            framgent.dismiss();
        }
    }

    @Override
    public void onGetEmailListner(String tEmail) {
//        Toast.makeText(this, "Fsdf", Toast.LENGTH_SHORT).show();
        final String tUser = sharedPreferences.getString("user", "null");
        if (tUser.equals("null"))
            return;
        Gson gson = new Gson();
        User user = gson.fromJson(tUser, User.class);
        user.setEmail(email);
        editor.putString("user", Utils.convertToJSon(new User(tEmail, user.getName(), user.getId(), "")));
        editor.commit();
        createUser(user.getId(), user.getName(), tEmail);
        dialogEmail.dismiss();

    }

    private void addUserToDataBase(String email, String name, String id) {
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

    @Override
    public void OnNeedCommunicateListner(CommunicationType type) {
        Intent intent;
        switch (type) {
            case EMAIL:
                intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"bashiralterh1998@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Challenge game");
                intent.putExtra(Intent.EXTRA_TEXT, "Dear Bashir ...........");
//                intent.putExtra(Intent.EXTRA_CC, "ghi");
                intent.setType("text/plain");
                dialogInfo.dismiss();
                startActivity(intent);
                break;
            case FACEBOOK:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=100003729378979"));
                dialogInfo.dismiss();
                startActivity(intent);
                break;
            case PHONE_NUMBER:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0954226805"));
                dialogInfo.dismiss();
                startActivity(intent);
                break;
        }

    }
}
