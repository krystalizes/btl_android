package com.example.th2android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.th2android.R;
import com.example.th2android.UpdateDeleteActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentHome extends Fragment {
    private RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    private TextView tvTong;
    public List<Item> list;

    DatabaseReference userRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container,false);
        initView(view);
        LinearLayoutManager manager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notelist");
        return view;
    }

    private void initView(View view) {
        recyclerView=view.findViewById(R.id.recycleView);
        tvTong=view.findViewById(R.id.tvTong);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Date d=new Date();
        SimpleDateFormat f=new SimpleDateFormat("d/MM/yyyy");
        String currentDate = f.format(d);
        Query query = userRef.orderByChild("date").equalTo(currentDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        list.add(item);
                    }
                    // handle the list of items with current date
                    adapter = new RecycleViewAdapter(getActivity(),list);
                    recyclerView.setAdapter(adapter);
                    tvTong.setText("Tong tien:"+tong(list));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle the error
                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public void onClick(View view, int position) {
//        Item item= adapter.getItem(position);
//        Intent intent=new Intent(getActivity(), UpdateDeleteActivity.class);
//        intent.putExtra("id",item.getId());
//        startActivity(intent);
//    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        Date d=new Date();
//        SimpleDateFormat f=new SimpleDateFormat("d/MM/yyyy");
//        String currentDate = f.format(d);
//        Query query = myRef.orderByChild("date").equalTo(currentDate);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    list = new ArrayList<>();
//                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
//                        Item item = itemSnapshot.getValue(Item.class);
//                        list.add(item);
//                    }
//                    // handle the list of items with current date
//                    adapter = new RecycleViewAdapter(getActivity(),list);
//                    recyclerView.setAdapter(adapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // handle the error
//                Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
//            }
//        });
//        tvTong.setText("Tong tien:"+tong(list));
//    }
}