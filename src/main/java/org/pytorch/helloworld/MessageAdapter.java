package org.pytorch.helloworld;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Message> mListMessage;
    static int count=0;

    public void setData(List<Message> list) {
        this.mListMessage = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);


//        Log.i("adapter", "message\t" + viewType);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mListMessage.get(position);

        if (message==null){
            return;
        }
//

//        if (position%2==0) {
//            Log.i("adapter", "message\t" + message.getMessage());
//            Log.i("adapter", "message\t" + position);
////            holder.tvMessage.setBackgroundColor(R.drawable.b_corner_16_2);
//            holder.layout_message.setGravity(Gravity.LEFT);
//        }

        holder.tvMessage.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        if (mListMessage != null){
            return mListMessage.size();
        }
        return 0;
    }

    public Message getItem(int position) {
        return mListMessage.get(position);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMessage;
        private LinearLayout layout_message;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tv_message);
            layout_message = itemView.findViewById(R.id.layout_message);

//            count = mListMessage.size()-1;
            count++;

            if (count%2!=0){
                layout_message.setGravity(Gravity.LEFT);
                tvMessage.setBackgroundColor(R.drawable.bg_corner_16);
//                tvMessage.
            }
            Log.i("adapter", "message\t" + count);
            Log.i("adapter", "size\t"+ " "+mListMessage.size() + " "+mListMessage.get(mListMessage.size()-1).getMessage());

        }
    }
}
