package henho.ketban.hete.Matches;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.simcoder.tinder.R;

import java.util.List;

/**
 * Created by manel on 10/31/2017.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders>{
    private List<MatchesObject> matchesList;
    private Context context;


    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolders holder, int position) {
        holder.mLayout.setTag(matchesList.get(position).getUserId());
        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default"))
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mMatchImage);
        if(!matchesList.get(position).getActive().equals("http://xemmienphi.xyz/hieumlxam.png"))
            Glide.with(context).load(matchesList.get(position).getActive()).apply(RequestOptions.circleCropTransform()).into(holder.mActive);


    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
