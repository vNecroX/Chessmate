package com.jorjaiz.chessmateapplicationv1.Chat;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.R;

import java.util.List;

public class Adapter_Chat extends RecyclerView.Adapter<Adapter_Chat.MessagesViewHolder>
{
    private List<POJO_Chat> list;
    private Context context;

    public Adapter_Chat(List<POJO_Chat> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_messages, viewGroup, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder messagesViewHolder, int i)
    {
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) messagesViewHolder.cvMessages.getLayoutParams();
        FrameLayout.LayoutParams fl = (FrameLayout.LayoutParams) messagesViewHolder.messageBG.getLayoutParams();

        LinearLayout.LayoutParams llmssg = (LinearLayout.LayoutParams) messagesViewHolder.tvMssgTime.getLayoutParams();
        LinearLayout.LayoutParams llmssgT = (LinearLayout.LayoutParams) messagesViewHolder.tvMssgTime.getLayoutParams();

        if(list.get(i).getMessType() == 1)
        {
            messagesViewHolder.messageBG.setBackgroundResource(R.drawable.in_message_bgk);
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            fl.gravity = Gravity.END;
            llmssg.gravity = Gravity.END;
            llmssgT.gravity = Gravity.END;
            messagesViewHolder.tvMessage.setGravity(Gravity.END);
        }
        else if(list.get(i).getMessType() == 2)
        {
            messagesViewHolder.messageBG.setBackgroundResource(R.drawable.out_message_bgk);
            rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            fl.gravity = Gravity.START;
            llmssg.gravity = Gravity.START;
            llmssgT.gravity = Gravity.START;
            messagesViewHolder.tvMessage.setGravity(Gravity.START);
        }

        messagesViewHolder.cvMessages.setLayoutParams(rl);
        messagesViewHolder.messageBG.setLayoutParams(fl);
        messagesViewHolder.tvMessage.setLayoutParams(llmssg);
        messagesViewHolder.tvMssgTime.setLayoutParams(llmssgT);

        messagesViewHolder.tvMessage.setText(list.get(i).getMessage());
        messagesViewHolder.tvMssgTime.setText(list.get(i).getMessageTime());

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            messagesViewHolder.cvMessages.getBackground().setAlpha(0);
        else
            messagesViewHolder.cvMessages.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        CardView cvMessages;
        TextView tvMessage;
        TextView tvMssgTime;
        LinearLayout messageBG;

        MessagesViewHolder(View itemView)
        {
            super(itemView);
            cvMessages = itemView.findViewById(R.id.cvMessages);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvMssgTime = itemView.findViewById(R.id.tvMssgTime);
            messageBG = itemView.findViewById(R.id.messageBG);
        }
    }
}

