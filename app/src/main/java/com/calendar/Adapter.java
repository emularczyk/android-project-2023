package com.calendar;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<Event> eventList;
    private final DatabaseReference databaseReference;
    private final String date;

    Adapter(ArrayList<Event> eventList, String date) {
        this.eventList = eventList;
        this.date = date;
        this.databaseReference = FirebaseDatabase.
                getInstance().
                getReference("Calendar");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;
    }

    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View view = inflater.inflate(R.layout.item, parent, false);
        viewHolder = new EventItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EventItemViewHolder eventItemVH = (EventItemViewHolder) holder;
        eventItemVH.getTitleTextView().setText(eventList.get(holder.getAdapterPosition()).getTitle());

        eventItemVH.getDeleteButton().setText(R.string.delete);
        eventItemVH.getDeleteButton().setOnClickListener(v -> removeEvent(holder));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void removeEvent(@NonNull RecyclerView.ViewHolder holder) {
        String eventTitle = eventList.get(holder.getAdapterPosition()).getTitle();
        Query eventQuery = databaseReference.child(date).child(eventTitle);
        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                eventList.removeIf(obj -> obj.getTitle().equals(eventTitle));
                notifyItemRemoved(holder.getAdapterPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "deleteButton - onCancelled", databaseError.toException());
            }
        });
    }

}