package com.example.projektspecjalnosciowy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    private String key = "";
    private String order;
    private String measurement;
    private String glass;
    private String count;
    private String date;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Zlecenia");
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);


        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        String onlineUserID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("order").child(onlineUserID);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(view -> addOrder());

    }

    private void addOrder() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.user_input, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);


        EditText order = myView.findViewById(R.id.addOrder);
        EditText measurement = myView.findViewById(R.id.addWymiar);
         EditText count = myView.findViewById(R.id.addCount);
        EditText glass = myView.findViewById(R.id.addGlass);
         EditText date = myView.findViewById(R.id.addDate);

        Button save = myView.findViewById(R.id.saveButton);
        Button cancel = myView.findViewById(R.id.cancelButton);

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        save.setOnClickListener(v -> {
            String newOrder = order.getText().toString().trim();
            String newMeasurement = measurement.getText().toString().trim();
            String newCount = count.getText().toString().trim();
            String newGlass = glass.getText().toString().trim();
            String newDate = date.getText().toString().trim();
            String id = databaseReference.push().getKey();

            //gdy zabraknie jakichś informacji, informujemy użytkownika o konieczności ich wstawienia:
            if (TextUtils.isEmpty(newOrder)){
                order.setError("Numer zlecenia jest wymagany!");
                return;
            }
            if (TextUtils.isEmpty(newMeasurement)){
                measurement.setError("Wymiar jest wymagany!");
                return;
            }
            if (TextUtils.isEmpty(newCount)){
                count.setError("Liczba sztuk jest wymagana!");
                return;
            }
            if (TextUtils.isEmpty(newGlass)){
                glass.setError("Rodzaj szkła jest wymagany!");
                return;
            }if (TextUtils.isEmpty(newDate)){
                date.setError("Data jest wymagana!");
                return;
            }else {
                //gdy wszystko przebiegnie pomyślnie, dodajemy nowe zlecenie i informujemy o tym
                //użytkownika
                dialog.setMessage("Zlecenie dodane");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Order addedOrder = new Order(newOrder,newMeasurement,newCount,newGlass, id, newDate);
                databaseReference.child(id).setValue(addedOrder).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(Home.this, "Zlecenie dodane!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }else{
                        String err = task.getException().toString();
                        Toast.makeText(Home.this, "Nie udało się dodać zlecenia! \n" + err, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Order> recyclerOptions = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(databaseReference,Order.class)
                .build();

        FirebaseRecyclerAdapter<Order, ViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Order, ViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Order model) {
                //holder.setDate(model.getDate());
                holder.setOrder(model.getOrder());

                //gdy użytkownik kliknie na dane zlecenie, przenosimy go do nowego layoutu z możliwością zaktualizowania zlecenia
                holder.myView.setOnClickListener(view -> {
                    key = getRef(position).getKey();
                    order = model.getOrder();
                    measurement = model.getMeasurement();
                    date = model.getDate();
                    count = model.getCount();
                    glass = model.getGlass();

                    updateOrder();
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent, false);
                return new ViewHolder(v);
            }
        };
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View myView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setOrder(String order){
            TextView orderTv = myView.findViewById(R.id.orderTv);
            orderTv.setText(order);
        }
        /*public void setDate(String date){
            TextView dateTv = myView.findViewById(R.id.dateTv);
            dateTv.setText(date);
        }*/
    }

    private void updateOrder(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View v = layoutInflater.inflate(R.layout.update_order_data, null);
        alertDialog.setView(v);

        AlertDialog dialog = alertDialog.create();
        EditText newOrder = v.findViewById(R.id.updateOrder);
        EditText newDate = v.findViewById(R.id.updateDate);
        EditText newMeasurement = v.findViewById(R.id.updateWymiar);
        EditText newGlass = v.findViewById(R.id.updateGlass);
        EditText newCount = v.findViewById(R.id.updateCount);

        newOrder.setText(order);
        newDate.setText(date);
        newMeasurement.setText(measurement);
        newGlass.setText(glass);
        newCount.setText(count);

        Button deleteButton = v.findViewById(R.id.deleteButton);
        Button saveButton = v.findViewById(R.id.updateSaveButton);

        saveButton.setOnClickListener(view -> {
            order = newOrder.getText().toString().trim();
            date = newDate.getText().toString().trim();
            measurement = newMeasurement.getText().toString().trim();
            glass = newGlass.getText().toString().trim();
            count = newCount.getText().toString().trim();
            //następnie umieszczamy dane w bazie danych Firebase

            Order newOrder1 = new Order (order, measurement, count, glass, key, date);

            databaseReference.child(key).setValue(newOrder1).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(Home.this, "Pomyślnie zaktualizowano zlecenie", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Home.this, "Coś poszło nie tak!", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();

        });
        deleteButton.setOnClickListener(view -> {
            databaseReference.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(Home.this, "Pomyślnie usunięto zlecenie", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Home.this, "Coś poszło nie tak!", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

  @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(Home.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}