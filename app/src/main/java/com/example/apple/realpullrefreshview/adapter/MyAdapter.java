package com.example.apple.realpullrefreshview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.realpullrefreshview.R;
import com.example.apple.realpullrefreshview.bean.Body;

import java.util.List;

/**
 * Created by apple on 2017/7/14.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private Context context;
    private List<Body> bodyList;

    RecyclerView.ViewHolder holder;

    public MyAdapter(Context context, List<Body> bodyList) {
        this.context = context;
        this.bodyList = bodyList;
        inflater = LayoutInflater.from(context);

    }

    private static final int TYPE_BODY = 1;
    private static final int TYPE_FOOT = 2;

    @Override
    public int getItemViewType(int position) {
        int viewType = -1;

     if (position == bodyList.size() ) {
            viewType = TYPE_FOOT;
        } else {
            viewType = TYPE_BODY;
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case TYPE_BODY:
                holder = new BodyViewHolder(inflater.inflate(R.layout.item_body, parent, false));
                break;
            case TYPE_FOOT:
                holder = new FootViewHolder(inflater.inflate(R.layout.item_foot, parent, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BodyViewHolder) {

            bindBodyView((BodyViewHolder) holder,position);

        } else {

            bindFootView((FootViewHolder) holder,position);

        }


    }





    private void bindBodyView(BodyViewHolder holder, final int position) {

        Body body = bodyList.get(position);
        holder.tvBody.setText(body.getName()+"     hello     "+body.getAge());


        holder.tvBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void bindFootView(FootViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return bodyList.size() + 1;
    }




    class BodyViewHolder extends RecyclerView.ViewHolder {


        private final TextView tvBody;

        public BodyViewHolder(View itemView) {
            super(itemView);
            tvBody = (TextView) itemView.findViewById(R.id.tv_body);

        }
    }


    class FootViewHolder extends RecyclerView.ViewHolder {


        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
}
