package com.gal.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gal.project.R;
import com.gal.project.models.Event;
import com.gal.project.screens.SearchEventsActivity;
import com.gal.project.screens.UserEvents;


import java.util.List;

public class EventUserAdapter extends RecyclerView.Adapter<EventUserAdapter.EventViewHolder> {

        private List<Event> eventList;

        private Context context;

        private String uid;


        // Constructor to initialize the list


    public EventUserAdapter(List<Event> eventList, Context context, String uid) {
        this.eventList = eventList;
        this.context = context;
        this.uid = uid;
    }


        @Override
        public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate the layout for each list item
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_event, parent, false);


            return new  EventViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);



        // Bind the data to the views
        holder.tvEventName.setText(event.getName());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventTime.setText(event.getTime());
        holder.tvEventCat.setText(event.getType());
        holder.tvEventCity.setText(event.getCity());




        if (context instanceof UserEvents)
        {

        if(event.getUserAdmin().getId().equals(uid))

                holder.btnJoin.setText("עריכה");

           else    holder.btnJoin.setText("יציאה");
        }






        holder.tvEventJoined.setText((event.getJoined().size())+"/"+event.getMaxJoin());



        holder.btnJoin.setOnClickListener(v -> {

            if( holder.btnJoin.getText().equals("יציאה"))
                ( (UserEvents) context).eventExit (event);
            else  if( holder.btnJoin.getText().equals("עריכה"))
                ( (UserEvents) context).goToEditEvent (event);

            else         ((SearchEventsActivity) context).joinEvent(event); //
        });

    }



        @Override
        public int getItemCount() {
            return eventList.size();
        }



        // ViewHolder class to hold references to the views in the layout
        public static class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventName, tvEventDate, tvEventTime, tvEventCat, tvEventCity, tvEventJoined;
            Button btnJoin;
            public EventViewHolder(View itemView) {
                super(itemView);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvEventDate = itemView.findViewById(R.id.tvEventDate);
                tvEventTime = itemView.findViewById(R.id.tvEventTime);
                tvEventCat = itemView.findViewById(R.id.tvEventCat);
                tvEventCity = itemView.findViewById(R.id.tvEventCity);
                tvEventJoined = itemView.findViewById(R.id.tvEventJoinedNe);
                btnJoin=itemView.findViewById(R.id.JoinBT);


            }
        }
    }


