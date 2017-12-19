package com.example.my.sapproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        public EditText search_text;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<User> myDataset;
    StorageReference mStorage;
    ArrayList<String> photo = new ArrayList<>();
    ArrayList<String> txt = new ArrayList<>();
    StorageReference photoLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton search_btn = findViewById(R.id.search_btn);
        ImageButton add_btn = findViewById(R.id.addpic_btn);
        ImageButton setting_btn = findViewById(R.id.setting_btn);
        search_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        myDataset = new ArrayList<>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = mDatabase.child("user").child(user.getUid());

        mStorage = FirebaseStorage.getInstance().getReference();




        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren()){
                    User userData = dataSnapshot.getValue(User.class);
//                    Log.e("Geun", userData.photo);
                }

                collectInfo((Map<String, Object>) dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void collectInfo(Map<String, Object> users){

        for(Map.Entry<String, Object> entry : users.entrySet()){
                Map user_data = (Map) entry.getValue();

                photo.add(user_data.get("photo").toString());
                txt.add((String)user_data.get("txt"));
            }


            for(int i=0; i<photo.size(); i++){
                Log.e("geun", photo.get(i) + " " + txt.get(i));
                myDataset.add(new User("",photo.get(i),txt.get(i)));
        }

            for(int i=0; i<photo.size(); i++){
                String loc_path = "images/"+photo.get(i);
                photoLocation = mStorage.child(loc_path);
            }

        mAdapter = new MyAdapter(myDataset,getApplicationContext(), photoLocation);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        int i  = view.getId();
        if(i == R.id.search_btn){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.30.1.39:8000"));
//            Intent intent = new Intent(this, SearchPhotoActivity.class);
//            intent.putExtra("SEARCH_TEXT", search_text.getText().toString());
            startActivity(intent);

        }else if(i == R.id.addpic_btn){
            startActivity(new Intent(this, AddPictureAcitivity.class));
        }else if(i == R.id.setting_btn){

        }
    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<User> mDataset;

    Context context;
    StorageReference photoLocation;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.addphoto);
            mTextView = view.findViewById(R.id.photo_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<User> myDataset, Context context, StorageReference photoLocation) {
        mDataset = myDataset;
        this.context = context;
        this.photoLocation = photoLocation;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position).txt);
        StorageReference str = FirebaseStorage.getInstance().getReference();



        String path = "images/"+Uri.parse(mDataset.get(position).photo);
        StorageReference loc = str.child(path);
        Log.e("GEUN_PHOTO", mDataset.get(position).photo);

        Glide.with(holder.mImageView.getContext())
                .using(new FirebaseImageLoader())
                .load(loc)
                .into(holder.mImageView);

//        holder.mImageView.setImageURI(Uri.parse(mDataset.get(position).toString()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

