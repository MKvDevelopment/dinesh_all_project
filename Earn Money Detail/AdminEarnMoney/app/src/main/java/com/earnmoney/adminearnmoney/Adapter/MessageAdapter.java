package com.earnmoney.adminearnmoney.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.earnmoney.adminearnmoney.Model.Message;
import com.earnmoney.adminearnmoney.R;
import com.earnmoney.adminearnmoney.holders.MessageHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static com.earnmoney.adminearnmoney.Activity.ChatActivity.otherUserId;


public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

    private messageAdapterListner messageAdapterListner;
    private List<Message> messagesList;

    public void setMessageAdapterListner(MessageAdapter.messageAdapterListner messageAdapterListner) {
        this.messageAdapterListner = messageAdapterListner;
    }

    public MessageAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        return new MessageHolder(view, view.getContext());
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Message c = messagesList.get(position);

        if (messagesList.size() - 1 == position) {
            holder.setLastMessage(currentUserId, c.getFrom(), c.getTo());
        } else {
            holder.hideBottom();
        }

        if (c.getFrom().equals(currentUserId)) {
            holder.setRightMessage(c.getFrom(), c.getMessage(), c.getTimestamp(), c.getType());
        } else {
            holder.setLeftMessage(c.getFrom(), c.getMessage(), c.getTimestamp(), c.getType());
        }
        holder.itemView.setOnClickListener(v -> {
            messageAdapterListner.onclickListner(position);
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public interface messageAdapterListner{
        void onclickListner(int postion);
    }
}
