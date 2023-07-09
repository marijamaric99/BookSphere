package ba.sum.fpmoz.booksphere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



import java.util.ArrayList;

import ba.sum.fpmoz.booksphere.R;
import ba.sum.fpmoz.booksphere.databinding.RowCommentBinding;
import ba.sum.fpmoz.booksphere.model.Comments;
import ba.sum.fpmoz.booksphere.model.UserProfile;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.HolderComment>{

    private Context context;
    private ArrayList<Comments> commentArrayList;

    private RowCommentBinding binding;



    public CommentAdapter(Context context, ArrayList<Comments> commentArrayList){
        this.context = context;
        this.commentArrayList = commentArrayList;
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        Comments comments = commentArrayList.get(position);
        String id = comments.getId();
        String bookId = comments.getBookId();
        String timestamp = comments.getTimestamp();
        String comment = comments.getComment();
        String name  = comments.getName();

        holder.commentTv.setText(comment);
        holder.nameTv.setText(name);
    }



    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{

        ShapeableImageView profileTv;
        TextView nameTv, commentTv;

        public HolderComment(@NonNull View itemview){
            super(itemview);

            profileTv = binding.profileTv;
            nameTv = binding.nameTv;
            commentTv = binding.commentTv;
        }
    }
}
