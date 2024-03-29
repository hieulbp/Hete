package henho.ketban.hete.ChatList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import henho.ketban.hete.Chat.ChatActivity;
import com.simcoder.tinder.R;

/**
 * Created by manel on 10/31/2017.
 */

public class ChatListViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mName,
                    mMessage;
    public ImageView mImage, mOnline;
    public LinearLayout mLayout;
    public ChatListViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mLayout = itemView.findViewById(R.id.layout);
        mName = itemView.findViewById(R.id.name);
        mMessage = itemView.findViewById(R.id.message);
        mImage = itemView.findViewById(R.id.image);
        mOnline = itemView.findViewById(R.id.acitveonline);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mLayout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
