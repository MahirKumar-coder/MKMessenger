package com.example.mkmessenger;

import static com.example.mkmessenger.ChatWin.receiverIImg;
import static com.example.mkmessenger.ChatWin.senderImg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<msgModdelClass> messageAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;

    public messagesAdapter(Context context, ArrayList<msgModdelClass> messageAdapterArrayList) {
        this.context = context;
        this.messageAdapterArrayList = messageAdapterArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModdelClass messages = messageAdapterArrayList.get(position);
        if (holder.getClass() == senderViewHolder.class){
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            if (senderImg != null && !senderImg.isEmpty()) {
                Picasso.get().load(senderImg).into(viewHolder.circleImageView);
            } else {
                viewHolder.circleImageView.setImageResource(R.drawable.man); // fallback/default
            }

            if (receiverIImg != null && !receiverIImg.isEmpty()) {
                Picasso.get().load(receiverIImg).into(viewHolder.circleImageView);
            } else {
                viewHolder.circleImageView.setImageResource(R.drawable.man); // fallback/default
            }

        }
        else{
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            if (senderImg != null && !senderImg.isEmpty()) {
                Picasso.get().load(senderImg).into(viewHolder.circleImageView);
            } else {
                viewHolder.circleImageView.setImageResource(R.drawable.man); // fallback/default
            }

            if (receiverIImg != null && !receiverIImg.isEmpty()) {
                Picasso.get().load(receiverIImg).into(viewHolder.circleImageView);
            } else {
                viewHolder.circleImageView.setImageResource(R.drawable.man); // fallback/default
            }

        }
    }

    @Override
    public int getItemCount() {
        return messageAdapterArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        msgModdelClass messages = messageAdapterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())){
            return ITEM_SEND;
        }
        else {
            return ITEM_RECIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
        }
    }

    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
        }
    }

}
