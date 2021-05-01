package com.kushjuthani.healthsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class showHospitalData extends AppCompatActivity {

    TextView hosName,hosAddress,hosMail,hosNumber,chooseTime,chooseDate;//hosType;
    Button hosSchedule,submit;

    String hosmailid,hosPhoneNumber;


    String userData,appTime,appDate,appNumber;
    String TAG  = "showHospitalData";
    Map<String,Object> map = new HashMap<>();

    DatePickerDialog.OnDateSetListener chooseDateSetListener;
    TimePickerDialog.OnTimeSetListener chooseTimeSetListener;

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_hospital_data);

        hosName = findViewById(R.id.HosName);
        hosAddress = findViewById(R.id.HosAddress);
        hosMail = findViewById(R.id.HosMail);
        hosNumber = findViewById(R.id.HosNumber);
        hosSchedule = findViewById(R.id.HosSchedule);
        chooseTime = findViewById(R.id.chTime);
        chooseDate = findViewById(R.id.chDate);
        submit = findViewById(R.id.submitBtn);


        Intent in = getIntent();

        final String hosID = in.getStringExtra("dataID");
        Log.d(TAG, "HosID : "+hosID);
        DocumentReference db =  firebaseFirestore.collection("hospitals").document(hosID);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        map = document.getData();
                        //add all fileds to get data
                        hosName.setText(String.valueOf(map.get("name")));
                        hosAddress.setText(String.valueOf(map.get("address")));
                        hosNumber.setText(String.valueOf(map.get("phone number")));
                        hosmailid = String.valueOf(map.get("mail"));
                        hosMail.setText(hosmailid);
                        //ArrayList arr = (ArrayList) map.get("type");
                        //String arrText = arr.toString();
                        //hosType.setText(arrText.substring(1,arrText.length()-1));

                    }

                }
            }
        });

        hosSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hosSchedule.setVisibility(View.GONE);
                chooseTime.setVisibility(View.VISIBLE);
                chooseDate.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);

                //selection of time and date
                chooseTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar cal = Calendar.getInstance();
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int min =  cal.get(Calendar.MINUTE);
                        int ampm = cal.get(Calendar.AM_PM);
                        TimePickerDialog dialog = new TimePickerDialog(showHospitalData.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                (TimePickerDialog.OnTimeSetListener)chooseTimeSetListener,hour,min,false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                chooseTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hr, int min) {
                        String ampm;
                        if (hr>12) {
                            ampm = "PM";
                            hr=hr-12;
                        }else {
                            ampm = "AM";
                        }
                        appTime = " "+hr+" : "+min+" "+ampm;
                        chooseTime.setText(appTime);
                    }
                };
                chooseDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog dialog = new DatePickerDialog(showHospitalData.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                (DatePickerDialog.OnDateSetListener)chooseDateSetListener,year,month,day);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                });
                chooseDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month=month+1;
                        appDate = " "+dayOfMonth + "/" + month + "/" + year+" ";
                        chooseDate.setText(appDate);
                    }
                };

                //reading from firebase
                FirebaseFirestore ffread = FirebaseFirestore.getInstance();
                CentralStorage cs = new CentralStorage(showHospitalData.this);;
                final String userid = cs.getData("userid");

                DocumentReference docRef = ffread.collection("USER").document(userid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                map =  document.getData();
                                Log.d(String.valueOf(showHospitalData.this),"DocumentSnapshot data: "+map);
                                //userinfo.setText( String.valueOf(info));
                                userData =  "\n Name :\t"+ String.valueOf(map.get("Name")) +
                                            "\n Email :\t"+ String.valueOf(map.get("Email"))+
                                            "\n Phone :\t"+ String.valueOf(map.get("Phone"))+
                                            "\n Address :\t"+ String.valueOf(map.get("Address"))+
                                            "\n Pincode :\t"+ String.valueOf(map.get("Pincode"))+
                                            "\n Gender :\t"+ String.valueOf(map.get("Gender"))+
                                            "\n Date Of Birth :\t"+ String.valueOf(map.get("DOB"))+
                                            "\n Blood Group :\t"+ String.valueOf(map.get("Blood Group"))+
                                            "\n Weight :\t"+ String.valueOf(map.get("Weight"))+
                                            "\n Height :\t"+ String.valueOf(map.get("Height"));
                                int count =Integer.parseInt(String.valueOf(map.get("appointments")));
                                count++;
                                appNumber = String.valueOf(count);
                                Log.d(TAG, "AppNumber : "+count);


                                //.setText(info);

                            } else {
                                Log.d(TAG, "No such document"+userid);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



                //sending mail to hospital
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //user appointment update in firebase
                        FirebaseFirestore userWrite = FirebaseFirestore.getInstance();
                        Map<String,Object> newApp = new HashMap<>();
                        newApp.put("name",hosName.getText().toString());
                        newApp.put("time",appTime);
                        newApp.put("date",appDate);
                        userWrite.collection("USER").document(userid).collection("hospital").document(appNumber).set(newApp)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failure
                                        Toast.makeText(showHospitalData.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void aVoid) {
                                                              //success
                                                              Toast.makeText(showHospitalData.this, "Appointment Booked",
                                                                      Toast.LENGTH_LONG).show();
                                                          }
                                                      }
                                );
                        //updateing appointments
                        userWrite.collection("USER").document(userid).update("appointments",appNumber)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(showHospitalData.this,
                                                    "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(showHospitalData.this,"Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(showHospitalData.this,
                                                e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                        userData = "To "+hosName.getText().toString()+",\nI would Like to take an appointment on "+appDate+"at"+appTime
                                +". My personal and medical information are as follows :\n"+userData;
                        Intent in = new Intent(Intent.ACTION_SEND);
                        in.setData(Uri.parse("mailto:"));
                        in.setType("text/plain");
                        in.putExtra(Intent.EXTRA_EMAIL,new String[]{hosmailid});
                        in.putExtra(Intent.EXTRA_SUBJECT,"APPOINTMENT AT "+appTime+" ON "+appDate);
                        in.putExtra(Intent.EXTRA_TEXT,userData);
                        startActivity(in);
                    }
                });
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.threedotsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(this,user_profile.class));
                return true;

            case R.id.aboutus:
                startActivity(new Intent(this,AboutUs.class));
                return true;

            case R.id.help:
                startActivity(new Intent(this,Help.class));
                return true;

            case R.id.logout:
                Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT ).show();
                CentralStorage cs = new CentralStorage(this);
                cs.clearData();
                cs.removeData("userid");
                startActivity(new Intent(this,login_page.class));
                this.finish();
                return true;

            default: return super.onOptionsItemSelected(item);
        }

    }
}