package com.lenovo.hospital_management_system;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PhysicalTestBookingAdapter extends RecyclerView.Adapter<PhysicalTestBookingAdapter.PhysicalTestViewHolder> {

    private ArrayList<PhysicalTest> physicalTestList;
    private OnPhysicalTestClickListener onPhysicalTestClickListener;

    public PhysicalTestBookingAdapter(ArrayList<PhysicalTest> physicalTestList, OnPhysicalTestClickListener onPhysicalTestClickListener){
        this.physicalTestList = physicalTestList;
        this.onPhysicalTestClickListener = onPhysicalTestClickListener;
    }

    @Override
    public PhysicalTestViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_physical_test, parent, false);
        return new PhysicalTestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhysicalTestViewHolder holder, int position){
        PhysicalTest physicalTest = physicalTestList.get(position);
        holder.testName.setText(physicalTest.getName());
        holder.testDescription.setText(physicalTest.getDescription());
        holder.testAvailability.setText(physicalTest.getAvailability());

        //setting on click listener to open booking page for the physical test
        holder.cardView.setOnClickListener(v -> onPhysicalTestClickListener.onPhysicalTestClick(physicalTest));
    }

    @Override
    public int getItemCount(){
        return physicalTestList.size();
    }

    public interface OnPhysicalTestClickListener{
        void onPhysicalTestClick(PhysicalTest physicalTest);
    }

    public static class PhysicalTestViewHolder extends RecyclerView.ViewHolder{

        TextView testName, testDescription, testAvailability;
        CardView cardView;

        public PhysicalTestViewHolder(View itemView){
            super(itemView);
            testName = itemView.findViewById(R.id.testName);
            testDescription = itemView.findViewById(R.id.testDescription);
            testAvailability = itemView.findViewById(R.id.testAvailability);
            cardView = itemView.findViewById(R.id.cardViewPhysicalTest);
        }
    }
}
