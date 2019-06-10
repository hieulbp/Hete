package henho.ketban.hete;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import henho.ketban.hete.Login.ChooseLoginRegistrationActivity;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.simcoder.tinder.R;

import java.util.HashMap;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class SettingsActivity extends AppCompatActivity {

    private RadioRealButtonGroup mRadioGroup;
    private Button mLogOut, mSave;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String  interest = "Male",
                    userId;

    private RangeSeekBar rangeSeekBar;
    private RangeSeekBar rangeSeekBar2;

    private Uri resultUri;
    private String minAge, maxAge, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRadioGroup = findViewById(R.id.radioRealButtonGroup);
        mLogOut = findViewById(R.id.logOut);
        mSave = findViewById(R.id.save);
        rangeSeekBar =(RangeSeekBar)findViewById(R.id.seekbar);
        rangeSeekBar2 =(RangeSeekBar)findViewById(R.id.seekbar2);

        rangeSeekBar.setRange(0, 100);//set min and max
        rangeSeekBar.setSeekBarMode(0);
        rangeSeekBar.setRangeInterval(0);
        rangeSeekBar.setIndicatorTextDecimalFormat("0"); //format number text like "0.00"


        rangeSeekBar2.setRange(0, 100);//set min and max
        rangeSeekBar2.setSeekBarMode(2);
        rangeSeekBar2.setRangeInterval(0);
        rangeSeekBar2.setIndicatorTextDecimalFormat("0"); //format number text like "0.00"
        rangeSeekBar2.setValue(16,100);

        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                int dis = (int) Math.round(leftValue);
                distance = String.valueOf(dis);

            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //stop tracking touch
            }
        });

        rangeSeekBar2.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                int min = (int) Math.round(leftValue);
                minAge = String.valueOf(min);
                int max = (int) Math.round(rightValue);
                maxAge = String.valueOf(max);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //start tracking touch
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view,  boolean isLeft) {
                //stop tracking touch
            }
        });





        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        getUserInfo();

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(getApplicationContext(), "dfjgdkhgkldjshgkldhsfg", Toast.LENGTH_LONG).show();
                saveUserInformation();
                //finish();
            }
        });

    }


    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("interest").getValue()!=null)
                        interest = dataSnapshot.child("interest").getValue().toString();
                    if(dataSnapshot.child("distance").getValue()!=null)
                    {
                        distance = dataSnapshot.child("distance").getValue().toString();
                        rangeSeekBar.setValue(Float.parseFloat(distance));
                    }
                    if(dataSnapshot.child("minage").getValue()!=null) {
                        minAge = dataSnapshot.child("minage").getValue().toString();
                    }
                    if(dataSnapshot.child("maxage").getValue()!=null) {
                        maxAge = dataSnapshot.child("maxage").getValue().toString();
                    }
                    rangeSeekBar2.setValue(Float.parseFloat(minAge), Float.parseFloat(maxAge));

                    if(interest.equals("Male"))
                        mRadioGroup.setPosition(0);
                    else if(interest.equals("Female"))
                        mRadioGroup.setPosition(1);
                    else
                        mRadioGroup.setPosition(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserInformation() {



        switch(mRadioGroup.getPosition()){
            case 0:
                interest = "Male";
                break;
            case 1:
                interest = "Female";
                break;
            case 2:
                interest = "Both";
                break;
        }




        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("interest", interest);
        userInfo.put("minage", minAge);
        userInfo.put("maxage", maxAge);
        userInfo.put("distance", distance);
        mUserDatabase.updateChildren(userInfo);



    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SettingsActivity.this, ChooseLoginRegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        saveUserInformation();
//        finish();
//        return false;
//    }




}
