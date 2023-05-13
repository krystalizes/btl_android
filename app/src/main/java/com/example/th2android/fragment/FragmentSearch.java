package com.example.th2android.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.th2android.R;
import com.example.th2android.adapter.RecycleViewAdapter;
import com.example.th2android.model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FragmentSearch extends Fragment implements View.OnClickListener{
    private RecyclerView recyclerView;
    private TextView tvTong;
    private Button btSearch;
    private SearchView searchView;
    private EditText eFrom,eTo;
    private Spinner spCategory;
    private RecycleViewAdapter adapter;
    private DatabaseReference userRef;
    private List<Item> list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container,false);
        initView(view);
        eFrom.setOnClickListener(this);
        eTo.setOnClickListener(this);
        btSearch.setOnClickListener(this);
        LinearLayoutManager manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notelist");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list=new ArrayList<>();
                // set code to retrive data and replace layout
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Item item = dataSnapshot1.getValue(Item.class);
                    list.add(item);
                }
                adapter = new RecycleViewAdapter(getActivity(),list);
                recyclerView.setAdapter(adapter);
                tvTong.setText("Tong tien:"+tong(list));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // set code to show an error
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Item item = snapshot.getValue(Item.class);
                            list.add(item);
                        }
                        List<Item> mlist=new ArrayList<>();
                        for(Item i:list){
                            if(i.getTitle().toLowerCase().contains(s.toLowerCase())){
                                mlist.add(i);
                            }
                        }
                        tvTong.setText("Tong tien:"+tong(mlist));
                        adapter = new RecycleViewAdapter(getActivity(),mlist);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        });



        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String cate=spCategory.getItemAtPosition(position).toString();
                if(!cate.equalsIgnoreCase("all")) {
                    Query query = userRef.orderByChild("category").equalTo(cate);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Item> catelist = new ArrayList<>();
                            for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                                Item note = noteSnapshot.getValue(Item.class);
                                catelist.add(note);
                            }
                            adapter = new RecycleViewAdapter(getActivity(), catelist);
                            recyclerView.setAdapter(adapter);
                            tvTong.setText("Tong tien:"+tong(catelist));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            list=new ArrayList<>();
                            // set code to retrive data and replace layout
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                            {
                                Item item = dataSnapshot1.getValue(Item.class);
                                list.add(item);
                            }
                            adapter = new RecycleViewAdapter(getActivity(),list);
                            recyclerView.setAdapter(adapter);
                            tvTong.setText("Tong tien:"+tong(list));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // set code to show an error
                            Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private int tong(List<Item> list){
        int t=0;
        for(Item i:list){
            t+=Integer.parseInt(i.getPrice());
        }
        return t;
    }

    private void initView(View view) {
        recyclerView= view.findViewById(R.id.recycleView);
        tvTong= view.findViewById(R.id.tvTong);
        btSearch= view.findViewById(R.id.btSearch);
        searchView= view.findViewById(R.id.search);
        eFrom= view.findViewById(R.id.eFrom);
        eTo= view.findViewById(R.id.eTo);
        spCategory= view.findViewById(R.id.spCategory);
        String[] arr=getResources().getStringArray(R.array.category);
        String[] arr1=new String[arr.length+1];
        arr1[0]="All";
        for(int i=0;i<arr.length;i++){
            arr1[i+1]=arr[i];
        }
        spCategory.setAdapter(new ArrayAdapter<String>(getContext(),R.layout.item_spinner,arr1));

    }

    @Override
    public void onClick(View view) {
        if(view==eFrom){
            final Calendar c= Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                    String date="";
                    if(m>8){
                        date=d+"/"+(m+1)+"/"+y;
                    }else{
                        date=d+"/0"+(m+1)+"/"+y;
                    }
                    eFrom.setText(date);
                }
            },year,month,day);
            dialog.show();
        }
        if(view==eTo){
            final Calendar c= Calendar.getInstance();
            int year=c.get(Calendar.YEAR);
            int month=c.get(Calendar.MONTH);
            int day=c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                    String date="";
                    if(m>8){
                        date=d+"/"+(m+1)+"/"+y;
                    }else{
                        date=d+"/0"+(m+1)+"/"+y;
                    }
                    eTo.setText(date);
                }
            },year,month,day);
            dialog.show();
        }
        if(view==btSearch){
            String from=eFrom.getText().toString();
            String to=eTo.getText().toString();
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Item> llist = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Item item = snapshot.getValue(Item.class);
                        llist.add(item);
                    }
                    List<Item> nlist=new ArrayList<>();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date dfrom= null;
                    try {
                        dfrom = format.parse(from);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    Date dto= null;
                    try {
                        dto = format.parse(to);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    for(Item i:list){
                        Date date = null;
                        try {
                            date = format.parse(i.getDate());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        if(date.compareTo(dfrom)>=0 && date.compareTo(dto)<=0){
                            nlist.add(i);
                        }
                    }
                    adapter.setList(nlist);
                    tvTong.setText("Tong tien:"+tong(nlist));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                }
            });
//            String from=eFrom.getText().toString();
//            String to=eTo.getText().toString();
//            Query query = userRef.orderByChild("date").startAt(from).endAt(to+"\uf8ff");
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    List<Item> llist = new ArrayList<>();
//                    for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
//                        Item item = noteSnapshot.getValue(Item.class);
//                        llist.add(item);
//                    }
//                    adapter.setList(llist);
//                    tvTong.setText("Tong tien:"+tong(llist));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }
}