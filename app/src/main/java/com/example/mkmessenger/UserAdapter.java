package com.example.mkmessenger;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewholder> {
    MainActivity mainActivity;
    ArrayList<Users> usersArrayList;
    public UserAdapter(MainActivity mainActivity, ArrayList<Users> usersArrayList) {
        this.mainActivity = mainActivity;
        this.usersArrayList = usersArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.viewholder holder, int position) {
        Users users = usersArrayList.get(position);
        holder.username.setText(users.userName);
        holder.userstatus.setText(users.status);

        if (users.profilepic != null && !users.profilepic.isEmpty()) {
            Picasso.get()
                    .load(users.profilepic)
                    .placeholder(R.drawable.photocamera)
                    .error(R.drawable.man)
                    .into(holder.userimg);
        } else {
            holder.userimg.setImageResource(R.drawable.man); // fallback image
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("IntentDebug", "Username: " + users.getUserName());
                    Log.d("IntentDebug", "ProfilePic: " + users.getProfilepic());
                    Log.d("IntentDebug", "UID: " + users.getUserId());

                    Intent intent = new Intent(mainActivity, ChatWin.class);
                    intent.putExtra("nameeee", users.getUserName() != null ? users.getUserName() : "Unknown");
                    intent.putExtra("reciverImg", users.getProfilepic() != null ? users.getProfilepic() : "");
                    intent.putExtra("uid", users.getUserId() != null ? users.getUserId() : "");

                    mainActivity.startActivity(intent);
                } catch (Exception e) {
                    Log.e("IntentError", "Failed to start ChatWin", e);
                    Toast.makeText(mainActivity, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        TextView userstatus;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
        }
    }
}
