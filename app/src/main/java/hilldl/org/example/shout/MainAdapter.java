package hilldl.org.example.shout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private static final String TAG = "MainAdapter";
    private List<Post> mPostList;
    private FirebaseAuth mAuth;
    private String mFirebaseUser;
    private AdapterCallbacks mCallbacks;

    public interface AdapterCallbacks {
         void deletePost(String postDate);
    }

    public MainAdapter(List<Post> postList, FirebaseAuth auth, AdapterCallbacks callback) {
        mPostList = postList;
        mAuth = auth;
        mCallbacks = callback;
        mFirebaseUser = mAuth.getCurrentUser().getUid();
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
        if (user1.equals(mFirebaseUser)) {
            holder.selfPostIdentifier.setVisibility(View.VISIBLE);
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
            this.postImage = itemview.findViewById(R.id.post_imgvw_profile);
            this.selfPostIdentifier = itemview.findViewById(R.id.post_imgvw_person);
        }
    }

    public void loadNewData(List<Post> posts) {
        mPostList = posts;
        notifyDataSetChanged();
    }

}
