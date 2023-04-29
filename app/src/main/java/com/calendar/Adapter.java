package com.calendar;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private final Context context;

    Adapter(ArrayList<Event> eventList, String date, Context context) {
        this.eventList = eventList;
        this.date = date;
        this.context = context;
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
        eventItemVH.getTitleTextView().setText(eventList.get(position).getTitle());
        eventItemVH.getNoteTextView().setText(eventList.get(position).getNote());
        eventItemVH.getEditButton().setText(R.string.edit);
        eventItemVH.getEditButton().setOnClickListener(v -> editEvent(holder));
        eventItemVH.getDeleteButton().setText(R.string.delete);
        eventItemVH.getDeleteButton().setOnClickListener(v -> deleteEvent(holder));

        eventItemVH.getItemLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout itemLayout = ((EventItemViewHolder) holder).getConstraintLayout();
                itemLayout.setVisibility(itemLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void deleteEvent(@NonNull RecyclerView.ViewHolder holder) {
        String eventId = eventList.get(holder.getAdapterPosition()).getId();
        Query eventQuery = databaseReference.child(eventList.get(holder.getAdapterPosition()).getDate()).child(eventId);
        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                eventList.removeIf(event -> event.getId().equals(eventId));
                notifyItemRemoved(holder.getAdapterPosition());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "deleteButton - onCancelled", databaseError.toException());
            }
        });
    }

    private void editEvent(@NonNull RecyclerView.ViewHolder holder) {
        Intent intent = new Intent(context, CreateEventActivity.class);
        intent.putExtra("event", eventList.get(holder.getAdapterPosition()));
        context.startActivity(intent);
    }
}