package henho.ketban.hete.Login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import henho.ketban.hete.MainActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.simcoder.tinder.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class RegisterActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener{
    private EditText mEmail, mPassword, mName, mAge;
    private Button mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private ImageView mImaAvatar;
    private Uri resultUri;
    private RadioRealButtonGroup mRadioGroup;
    private String Age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mImaAvatar = findViewById(R.id.avatar);
        mAge = findViewById(R.id.age);

        mRegistration = findViewById(R.id.register);

        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mRadioGroup.setPosition(0);

        mImaAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDate();

            }
        });
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String age = Age;
                final String accountType;
                int selectId = mRadioGroup.getPosition();


                switch (selectId){
                    case 1:
                        accountType = "Female";
                        break;
                    default:
                        accountType = "Male";
                }

                if(email.equals("") || password.equals("") || name.equals("") || age.equals(""))
                {
                    Snackbar.make(findViewById(R.id.layout), "Register error", Toast.LENGTH_SHORT).show();
                }
                else if(resultUri==null)
                {
                    Snackbar.make(findViewById(R.id.layout), "haven't updated avatars", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!isValid(email)) {
                        Snackbar.make(findViewById(R.id.layout), "Email wrong", Toast.LENGTH_SHORT).show();
                    }
                    else if (password.length() <= 5) {
                        Snackbar.make(findViewById(R.id.layout), "Password > 6 character", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                                } else {
                                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    final DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("name", name);
                                    userInfo.put("sex", accountType);
                                    userInfo.put("profileImageUrl", "default");
                                    userInfo.put("distance", "100");
                                    userInfo.put("minage", "0");
                                    userInfo.put("maxage", "100");
                                    userInfo.put("age", age);
                                    userInfo.put("lattiude", "0");
                                    userInfo.put("longitude", "0");
                                    switch (accountType) {
                                        case "Male":
                                            userInfo.put("interest", "Female");
                                            break;
                                        case "Female":
                                            userInfo.put("interest", "Male");
                                            break;
                                    }
                                    currentUserDb.updateChildren(userInfo);

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
                                                        currentUserDb.updateChildren(newImage);

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

                            }
                        });
                    }
                }
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


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
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
                        .into(mImaAvatar);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Age = getAge(year, month, dayOfMonth);
        mAge.setText(dayOfMonth+"/"+month+"/"+year);
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
}
