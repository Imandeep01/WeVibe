package com.example.datingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingapp.R;
import com.example.datingapp.modal.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    public static final int msgTypeLeft = 0;
    public static final int msgTypeRight = 1;
    private Context mcontext;
    private List<Chat> mchat;
    FirebaseUser firebaseUser;
    public MessagesAdapter(Context mcontext, List<Chat> mchat){
        this.mcontext = mcontext;
        this.mchat = mchat;
    }
    //@NonNulls
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == msgTypeRight){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.item_sent_messages, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }else{
            View view  = LayoutInflater.from(mcontext).inflate(R.layout.item_received_messages, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        Chat chat = mchat.get(position);
        holder.show_message.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.textView_message);
        }
    }
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mchat.get(position).getSender().equals(firebaseUser.getUid())){
            return msgTypeRight;
        }else{
            return msgTypeLeft;
        }
    }
}
