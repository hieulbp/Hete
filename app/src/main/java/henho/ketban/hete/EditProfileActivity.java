package henho.ketban.hete;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simcoder.tinder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class EditProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private EditText    mName,
                        mPhone,
                        mAge,
                        mJob,
                        mAbout;

    private RadioRealButtonGroup mRadioGroup;

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String  userId,
                    name,
                    phone,
                    profileImageUrl,
                    userSex,
                    job,
                    age,
                    about;


    private Uri resultUri;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mName = findViewById(R.id.name);
        mPhone = findViewById(R.id.phone);
        mAge = findViewById(R.id.age);
        mJob = findViewById(R.id.job);
        mAbout = findViewById(R.id.about);
        btnSave = findViewById(R.id.btnSave);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);

        mProfileImage = findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDate();

            }
        });




        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
                //finish();
            }
        });
    }

    private void showDialogDate() {
        DatePickerDialog datePickerDialog= new DatePickerDialog(
                this, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("name").getValue()!=null)
                        name = dataSnapshot.child("name").getValue().toString();
                    if(dataSnapshot.child("phone").getValue()!=null)
                        phone = dataSnapshot.child("phone").getValue().toString();
                    if(dataSnapshot.child("sex").getValue()!=null)
                        userSex = dataSnapshot.child("sex").getValue().toString();
                    if(dataSnapshot.child("age").getValue()!=null)
                        age = dataSnapshot.child("age").getValue().toString();
                    if(dataSnapshot.child("job").getValue()!=null)
                        job = dataSnapshot.child("job").getValue().toString();
                    if(dataSnapshot.child("about").getValue()!=null)
                        about = dataSnapshot.child("about").getValue().toString();
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null)
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                    mName.setText(name);
                    mPhone.setText(phone);
                    mAge.setText(age);
                    mJob.setText(job);
                    mAbout.setText(about);
                    if(!profileImageUrl.equals("default"))
                        Glide.with(getApplicationContext()).load(profileImageUrl).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                    if(userSex.equals("Male"))
                        mRadioGroup.setPosition(0);
                    else
                        mRadioGroup.setPosition(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {
        Toast.makeText(getApplicationContext(), "dfjgdkhgkldjshgkldhsfg", Toast.LENGTH_LONG).show();
        name = mName.getText().toString();
        phone = mPhone.getText().toString();
        age = mAge.getText().toString();
        job = mJob.getText().toString();
        about = mAbout.getText().toString();
        if(mRadioGroup.getPosition()==0)
            userSex = "Male";
        else
            userSex = "Female";

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("age", age);
        userInfo.put("job", job);
        userInfo.put("sex", userSex);
        userInfo.put("about", about);
        mUserDatabase.updateChildren(userInfo);

        if(resultUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            mUserDatabase.updateChildren(newImage);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                Glide.with(getApplication())
                        .load(bitmap) // Uri of the picture
                        .apply(RequestOptions.circleCropTransform())
                        .into(mProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //String date = dayOfMonth + "/" + month + "/" + year;
        String age = getAge(year, month, dayOfMonth);
        mAge.setText(age);

    }
    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

//    @Override

//    public boolean onOptionsItemSelected(MenuItem item) {
//        saveUserInformation();
//        return false;
//    }

    private void active(String ac)
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("active", ac);
        currentUserDb.updateChildren(userInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        active("http://xemmienphi.xyz/hieumlxanh.png");
    }

    @Override
    protected void onPause() {
        super.onPause();
        active("http://xemmienphi.xyz/hieumlxam.png");
    }

}
