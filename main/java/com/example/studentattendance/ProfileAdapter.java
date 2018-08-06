package com.example.studentattendance;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.profileDataViewHolder> {

    String[] profileData;
    private static final String TAG = "ProfileAdapter";

    public ProfileAdapter(String[] array)
    {
        profileData = array;
        Log.i(TAG, array.toString());
    }

    @NonNull
    @Override
    public profileDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.profile_recycle, parent, false);
        profileDataViewHolder vh = new profileDataViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull profileDataViewHolder holder, int position) {

        String temp = profileData[position];
        holder.dataView.setText(temp);
    }


    @Override
    public int getItemCount() {
        return profileData.length;
    }

    class profileDataViewHolder extends RecyclerView.ViewHolder {

        TextView dataView;

        public profileDataViewHolder(View itemView) {
            super(itemView);
            dataView = (TextView) itemView.findViewById(R.id.tv_profileDataViewHolder);
        }
    }
}



