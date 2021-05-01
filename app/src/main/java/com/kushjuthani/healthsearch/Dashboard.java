package com.kushjuthani.healthsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;

public class Dashboard extends AppCompatActivity {

    FirebaseFirestore firebaseFirestoreDashboard;


    private FirestoreRecyclerAdapter adapterDoc,adapterHos;
    private RecyclerView doctorApp,hospitalApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        doctorApp = findViewById(R.id.doctorApp);
        hospitalApp = findViewById(R.id.hospitalApp);


        CentralStorage cs = new CentralStorage(Dashboard.this);;
        String string = cs.getData("userid");

        firebaseFirestoreDashboard =  FirebaseFirestore.getInstance();

        CollectionReference collectionReference = firebaseFirestoreDashboard.collection("USER");
        Query queryDoc = collectionReference.document(string).collection("doctor");

        FirestoreRecyclerOptions<appData> optionsDoc = new FirestoreRecyclerOptions.Builder<appData>()
                .setLifecycleOwner(this)
                .setQuery(queryDoc, new SnapshotParser<appData>() {
                    @NonNull
                    @Override
                    public appData parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        appData productsModel = snapshot.toObject(appData.class);
                        String ID = snapshot.getId();
                        productsModel.setDocumentId(ID);
                        Log.i(String.valueOf(Dashboard.this),productsModel.getName());
                        return productsModel;
                    }
                })
                .build();

        adapterDoc = new FirestoreRecyclerAdapter<appData, TypeViewHolder>(optionsDoc) {
            @Override
            protected void onBindViewHolder(@NonNull TypeViewHolder holder, int position, @NonNull appData model) {
                holder.nameTV.setText(model.getName());
                holder.timeTV.setText(model.getTime());
                holder.dateTV.setText(model.getDate());
            }

            @NonNull
            @Override
            public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.app_data,parent, false);
                return new TypeViewHolder(view);
            }
        };

        doctorApp.setHasFixedSize(true);
        doctorApp.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        doctorApp.setAdapter(adapterDoc);

        //Hospital

        Query queryHos = collectionReference.document(string).collection("hospital");
        FirestoreRecyclerOptions<appData> optionsHos = new FirestoreRecyclerOptions.Builder<appData>()
                .setLifecycleOwner(this)
                .setQuery(queryHos, new SnapshotParser<appData>() {
                    @NonNull
                    @Override
                    public appData parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        appData productsModel = snapshot.toObject(appData.class);
                        String ID = snapshot.getId();
                        productsModel.setDocumentId(ID);
                        Log.i(String.valueOf(Dashboard.this),productsModel.getName());
                        return productsModel;
                    }
                })
                .build();

        adapterHos = new FirestoreRecyclerAdapter<appData, TypeViewHolder>(optionsHos) {
            @Override
            protected void onBindViewHolder(@NonNull TypeViewHolder holder, int position, @NonNull appData model) {
                holder.nameTV.setText(model.getName());
                holder.timeTV.setText(model.getTime());
                holder.dateTV.setText(model.getDate());
            }

            @NonNull
            @Override
            public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.app_data,parent, false);
                return new TypeViewHolder(view);
            }
        };

        hospitalApp.setHasFixedSize(true);
        hospitalApp.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        hospitalApp.setAdapter(adapterHos);
    }
    private  class TypeViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV, timeTV, dateTV;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameTextView);
            dateTV = itemView.findViewById(R.id.dateTextView);
            timeTV = itemView.findViewById(R.id.timeTextView);

        }


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