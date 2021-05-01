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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
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

public class showDoctorData extends AppCompatActivity {

    TextView docName,docMail,docNumber,docType,chooseTime,chooseDate;
    Button callNow,bookAppointment,submit;
    ArrayAdapter<String> degree;

    String docmailid,docPhoneNumber;
    String userData,appTime,appDate,appNumber;
    String TAG  = "showDoctorData";
    Map<String,Object> map = new HashMap<>();

    DatePickerDialog.OnDateSetListener chooseDateSetListener;
    TimePickerDialog.OnTimeSetListener chooseTimeSetListener;

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doctor_data);

        docName = findViewById(R.id.DocName);
        docMail = findViewById(R.id.DocMail);
        docNumber = findViewById(R.id.DocNumber);
        docType = findViewById(R.id.DocDegree);
        callNow = findViewById(R.id.callnow);
        bookAppointment = findViewById(R.id.bookappointment);
        chooseTime = findViewById(R.id.chTime);
        chooseDate = findViewById(R.id.chDate);
        submit = findViewById(R.id.submitBtn);



        //degree = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
        Intent in = getIntent();
        final String docID = in.getStringExtra("dataID");
        Log.d(TAG, "DocID : "+docID);

        DocumentReference db =  firebaseFirestore.collection("doctors").document(docID);
        db.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        map = document.getData();
                        //add all fileds to get data
                        docName.setText("Dr. "+String.valueOf(map.get("name")));
                        docmailid = String.valueOf(map.get("mail"));
                        docMail.setText(docmailid);
                        docPhoneNumber = String.valueOf(map.get("number"));
                        docNumber.setText(docPhoneNumber);
                        ArrayList arr = (ArrayList) map.get("degree");
                        //for(int  i = 0 ; i < arr.size();i++)
                            //degree.add(arr.get(i).toString());
                        //docDegree.setAdapter(degree);
                        String arrText = arr.toString();
                        docType.setText(arrText.substring(1,arrText.length()-1));
                    }

                }
            }
       });

        callNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent caller = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+docPhoneNumber));
                startActivity(caller);
            }
        });

        bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookAppointment.setVisibility(View.GONE);
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
                        TimePickerDialog dialog = new TimePickerDialog(showDoctorData.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

                        DatePickerDialog dialog = new DatePickerDialog(showDoctorData.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                CentralStorage cs = new CentralStorage(showDoctorData.this);;
                final String userid = cs.getData("userid");

                DocumentReference docRef = ffread.collection("USER").document(userid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                map =  document.getData();
                                Log.d(String.valueOf(showDoctorData.this),"DocumentSnapshot data: "+map);
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



                //sending mail to doctor
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //user appointment update in firebase
                        FirebaseFirestore userWrite = FirebaseFirestore.getInstance();
                        Map<String,Object> newApp = new HashMap<>();
                        newApp.put("name",docName.getText().toString());
                        newApp.put("time",appTime);
                        newApp.put("date",appDate);
                        userWrite.collection("USER").document(userid).collection("doctor").document(appNumber).set(newApp)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failure
                                        Toast.makeText(showDoctorData.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //success
                                        Toast.makeText(showDoctorData.this, "Appointment Booked",
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
                                            Toast.makeText(showDoctorData.this,
                                                    "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(showDoctorData.this,"Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(showDoctorData.this,
                                                e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                        userData = "To "+docName.getText().toString()+",\nI would Like to take an appointment on "+appDate+"at"+appTime
                                +". My personal and medical information are as follows :\n"+userData;
                        Intent in = new Intent(Intent.ACTION_SEND);
                        in.setData(Uri.parse("mailto:"));
                        in.setType("text/plain");
                        in.putExtra(Intent.EXTRA_EMAIL,new String[]{docmailid});
                        in.putExtra(Intent.EXTRA_SUBJECT,"APPOINTMENT AT "+appTime+" ON "+appDate);
                        in.putExtra(Intent.EXTRA_TEXT,userData);
                        startActivity(in);
                    }
                });
            }
        });
        /*SIMLPLER MAILER
        * GMAIL API - https://developers.google.com/gmail/api/guides/sending
        *               https://developers.google.com/gsuite/guides/android
         */



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