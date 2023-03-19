package com.calendar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class EventItemVH extends RecyclerView.ViewHolder {
    private TextView titleTextView;

    private Button deleteButton;

    public EventItemVH(View itemView) {
        super(itemView);

        titleTextView = itemView.findViewById(R.id.titleTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }
}
