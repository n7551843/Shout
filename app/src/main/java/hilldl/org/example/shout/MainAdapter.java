package hilldl.org.example.shout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import hilldl.org.example.shout.entities.Post;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private static final String TAG = "MainAdapter";
    private List<Post> mPostList;
    private String mUserID;

    public MainAdapter(List<Post> postList) {
        mPostList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //note - parent.getContext might cause a problem here because it's in a fragment, not
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final Post post = mPostList.get(position);
        final String forDeletion = post.getDateAndTime();
        String user1 = post.getUserID();
        holder.postContent.setText(post.getPost());
        holder.postNickname.setText(post.getNickname());
        holder.postUserID = user1;
        holder.dateOfPost = post.getDateAndTime();


        // If The current user matches the User ID of the Poster, make delete button visible.
        if (mUserID != null) {
            if (user1.equals(mUserID)) {
                holder.selfPostIdentifier.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ((mPostList != null) && (mPostList.size() != 0) ? mPostList.size() : 0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView postContent;
        public TextView postNickname;
        public ImageView postImage;
        public String postUserID;
        public String dateOfPost;
        public ImageView selfPostIdentifier;

        public MyViewHolder(View itemview) {
            super(itemview);
            this.postContent = itemView.findViewById(R.id.post_txtvw_content);
            this.postNickname = itemview.findViewById(R.id.post_txtvw_user);
            this.postImage = itemview.findViewById(R.id.post_imgvw_profile_poster);
            this.selfPostIdentifier = itemview.findViewById(R.id.post_imgvw_person);
        }
    }

    public void loadNewData(List<Post> posts) {
        mPostList = posts;
        notifyDataSetChanged();
    }

    public void setUserID(String id) {
        mUserID = id;
    }
}
