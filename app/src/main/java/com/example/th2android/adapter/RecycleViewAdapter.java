package com.example.th2android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.th2android.R;
import com.example.th2android.UpdateDeleteActivity;
import com.example.th2android.model.Item;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.HomeViewHolder>{
    private List<Item> list;

    private Context mContext;
    private ItemListener itemListener;
    public RecycleViewAdapter(Context mContext,List<Item> list) {
        this.mContext = mContext;
        this.list=list;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setList(List<Item> list){
        this.list=list;
        notifyDataSetChanged();
    }
    public Item getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Item item=list.get(position);
        holder.title.setText(item.getTitle());
        holder.category.setText(item.getCategory());
        holder.price.setText(item.getPrice());
        holder.date.setText(item.getDate()+"");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, UpdateDeleteActivity.class);
                intent.putExtra("id",item.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView title,category,price,date;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.tvTitle);
            category= itemView.findViewById(R.id.tvCategory);
            price= itemView.findViewById(R.id.tvPrice);
            date= itemView.findViewById(R.id.tvDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemListener!=null){
                itemListener.onClick(view,getAdapterPosition());
            }
        }
    }
    public interface ItemListener{
        void onClick(View view,int position);
    }
}
