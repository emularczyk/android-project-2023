package com.calendar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Item for event list view
 */
public class EventItemViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView noteTextView;
    private ConstraintLayout itemLayout;
    private ConstraintLayout constraintLayout;
    private Button editButton;
    private Button deleteButton;
    private boolean expandable = false;

    public EventItemViewHolder(View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        noteTextView = itemView.findViewById(R.id.noteTextView);
        editButton = itemView.findViewById(R.id.editButton);
        deleteButton = itemView.findViewById(R.id.deleteButton);
        itemLayout = itemView.findViewById(R.id.itemLayout);
        constraintLayout = itemView.findViewById(R.id.detailsLayout);
        constraintLayout.setVisibility(View.GONE);
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public TextView getDateTextView() {
        return dateTextView;
    }

    public void setDateTextView(TextView dateTextView) {
        this.dateTextView = dateTextView;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    public TextView getNoteTextView() {
        return noteTextView;
    }

    public void setNoteTextView(TextView noteTextView) {
        this.noteTextView = noteTextView;
    }

    public Button getEditButton() {
        return editButton;
    }

    public void setEditButton(Button editButton) {
        this.editButton = editButton;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setConstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }

    public ConstraintLayout getItemLayout() {
        return itemLayout;
    }

    public void setItemLayout(ConstraintLayout itemLayout) {
        this.itemLayout = itemLayout;
    }
}
