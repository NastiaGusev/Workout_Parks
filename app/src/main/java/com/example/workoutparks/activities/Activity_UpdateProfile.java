package com.example.workoutparks.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.workoutparks.R;
import com.example.workoutparks.callbacks.CallBack_Favorites;
import com.example.workoutparks.callbacks.CallBack_Home;
import com.example.workoutparks.callbacks.CallBack_MyProfile;
import com.example.workoutparks.callbacks.CallBack_Parks;
import com.example.workoutparks.fragments.Fragment_bottomButtons;
import com.example.workoutparks.objects.User;
import com.example.workoutparks.utils.MyDataBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Activity_UpdateProfile extends Activity_Base {

    private static final int PICK_IMAGE_REQUEST = 1;
    private final String USERS = "Users";

    private ImageButton updateProfile_BTN_choose;
    private ProgressBar updateProfile_BAR_progressBar;
    private MaterialButton updateProfile_BTN_upload;
    private ShapeableImageView updateProfile_IMG_user;
    private TextInputLayout updateProfile_EDT_name;
    private TextInputLayout updateProfile_EDT_des;
    private Slider updateProfile_SLD_slider;
    private MaterialButtonToggleGroup updateProfile_BTN_gender;
    private MaterialButton updateProfile_BTN_female;
    private MaterialButton updateProfile_BTN_male;
    private TextView updateProfile_TXT_age;

    private FrameLayout updateProfile_LAY_bottomButtons;
    private Fragment_bottomButtons fragment_buttons;

    private User currentUser;
    private Uri imageUri;
    private StorageTask uploadTask;
    private MyDataBase myDatabase = new MyDataBase();

    private CallBack_Home callBack_home = new CallBack_Home() {
        @Override
        public void gotoHome() {
            Intent myIntent = new Intent(Activity_UpdateProfile.this, Activity_Home.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_MyProfile callBack_myProfile = new CallBack_MyProfile() {
        @Override
        public void gotoMyProfile() {
            Intent myIntent = new Intent(Activity_UpdateProfile.this, Activity_UserProfile.class);
            myIntent.putExtra(Activity_UserProfile.USER, currentUser);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Parks callBack_parks = new CallBack_Parks() {
        @Override
        public void gotoParks() {
            Intent myIntent = new Intent(Activity_UpdateProfile.this, Activity_Parks.class);
            startActivity(myIntent);
            finish();
        }
    };

    private CallBack_Favorites callBack_favorites = new CallBack_Favorites() {
        @Override
        public void gotoFavorites() {
            Intent myIntent = new Intent(Activity_UpdateProfile.this, Activity_Favorites.class);
            startActivity(myIntent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDoublePressToClose = true;
        setContentView(R.layout.activity__update_profile);
        findViews();
        initBottomFragment();
        getUserFromDataBase();
        initViews();
    }

    private void findViews() {
        updateProfile_LAY_bottomButtons = findViewById(R.id.updateProfile_LAY_bottomButtons);
        updateProfile_BTN_choose = findViewById(R.id.updateProfile_BTN_choose);
        updateProfile_BAR_progressBar = findViewById(R.id.updateProfile_BAR_progressBar);
        updateProfile_BTN_upload = findViewById(R.id.updateProfile_BTN_upload);
        updateProfile_EDT_name = findViewById(R.id.updateProfile_EDT_name);
        updateProfile_EDT_des = findViewById(R.id.updateProfile_EDT_des);
        updateProfile_SLD_slider = findViewById(R.id.updateProfile_SLD_slider);
        updateProfile_TXT_age = findViewById(R.id.updateProfile_TXT_age);
        updateProfile_BTN_gender = findViewById(R.id.updateProfile_BTN_gender);
        updateProfile_BTN_female = findViewById(R.id.updateProfile_BTN_female);
        updateProfile_BTN_male = findViewById(R.id.updateProfile_BTN_male);
    }

    private void initBottomFragment() {
        fragment_buttons = new Fragment_bottomButtons();
        fragment_buttons.setCallBack_Home(callBack_home);
        fragment_buttons.setCallBack_Parks(callBack_parks);
        fragment_buttons.setCallBack_myProfile(callBack_myProfile);
        fragment_buttons.setCallBack_favorites(callBack_favorites);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.updateProfile_LAY_bottomButtons, fragment_buttons)
                .commit();
    }

    private void getUserFromDataBase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS);
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                updateUserDetails();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void updateUserDetails() {
        updateProfile_IMG_user = findViewById(R.id.updateProfile_IMG_user);
        if(!currentUser.getImgURL().equals("")){
            Picasso.with(this).load(currentUser.getImgURL()).into(updateProfile_IMG_user);
        }
        if(!currentUser.getName().equals("New User")){
            updateProfile_EDT_name.setPlaceholderText(currentUser.getName());
        }
        if(!currentUser.getDescription().equals("")){
            updateProfile_EDT_des.setPlaceholderText(currentUser.getDescription());
        }
        if(currentUser.getAge()!=0 ){
            updateProfile_SLD_slider.setValue(currentUser.getAge());
            updateProfile_TXT_age.setText("Age: " + currentUser.getAge());
        }
        if(!currentUser.getGender().equals("") ){
            if(currentUser.getGender().equals("female")){
                updateProfile_BTN_gender.check(updateProfile_BTN_female.getId());
            }else {
                updateProfile_BTN_gender.check(updateProfile_BTN_male.getId());
            }
        }
    }

    private void initViews() {
        updateProfile_BTN_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        updateProfile_BTN_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadTask != null && uploadTask.isInProgress()){
                    Toast.makeText(Activity_UpdateProfile.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFile() {
        StorageReference storage = FirebaseStorage.getInstance().getReference(currentUser.getUid());
        if (imageUri != null) {
            StorageReference fileRefrence = storage.child("profile_picture." + getFileExtension(imageUri));
            uploadTask = fileRefrence.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateProfile_BAR_progressBar.setProgress(0);
                                }
                            }, 500);
                            String url = uri.toString();
                            currentUser.setImgURL(url);
                            updateUser(true);
                            Toast.makeText(Activity_UpdateProfile.this, "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Activity_UpdateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    updateProfile_BAR_progressBar.setProgress((int) progress);
                }
            });
        } else {
            if(updateUser(false)){
                Toast.makeText(Activity_UpdateProfile.this, "Update Successful!", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(Activity_UpdateProfile.this, "Nothing to update", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean updateUser(boolean imageUpdated) {
        String name = updateProfile_EDT_name.getEditText().getText().toString();
        String description = updateProfile_EDT_des.getEditText().getText().toString();
        float age = updateProfile_SLD_slider.getValue();
        int genderId = updateProfile_BTN_gender.getCheckedButtonId();
        boolean changed = false;

        if(imageUpdated){
            changed = true;
        }
        //Check if the updated information has changed
        if(!name.equals("") && !currentUser.getName().equals(name)){
            currentUser.setName(name);
            changed= true;
        }
        if (!description.equals("") && !currentUser.getDescription().equals(description)){
            currentUser.setDescription(description);
            changed= true;
        }
        if (age!= 0.0 && currentUser.getAge()!=(int)age){
            currentUser.setAge((int)age);
            changed= true;
        }

        if(genderId == updateProfile_BTN_male.getId() && !currentUser.getGender().equals("male")){
            currentUser.setGender("male");
            changed= true;
        }else if (genderId == updateProfile_BTN_female.getId() && !currentUser.getGender().equals("female")){
            currentUser.setGender("female");
            changed= true;
        }
        if(changed){
            myDatabase.updateUserInDataBase(currentUser);
        }
        return changed;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(updateProfile_IMG_user);
        }
    }

}