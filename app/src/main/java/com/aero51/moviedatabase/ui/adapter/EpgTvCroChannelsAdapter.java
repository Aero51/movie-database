package com.aero51.moviedatabase.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.repository.model.epg.EpgProgram;
import com.aero51.moviedatabase.utils.Resource;

import java.util.ArrayList;
import java.util.List;


public class EpgTvCroChannelsAdapter extends RecyclerView.Adapter<EpgTvCroChannelsAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<List<EpgProgram>> sortedList;


    public EpgTvCroChannelsAdapter(Context context, Resource<List<EpgProgram>> listResource) {
        this.mInflater = LayoutInflater.from(context);
        Log.d("moviedatabaselog", "prije obrade");
        sortedList = new ArrayList<>();
        List<EpgProgram> hrt1List = new ArrayList<>();
        List<EpgProgram> hrt2List = new ArrayList<>();
        List<EpgProgram> hrt3List = new ArrayList<>();
        List<EpgProgram> novaTvList = new ArrayList<>();
        List<EpgProgram> rtlTelevizijaList = new ArrayList<>();
        List<EpgProgram> rtl2List = new ArrayList<>();
        List<EpgProgram> domaTvList = new ArrayList<>();
        List<EpgProgram> rtlKockicaList = new ArrayList<>();
        List<EpgProgram> hrt4List = new ArrayList<>();
        List<EpgProgram> programList = listResource.data;
        for (EpgProgram program : programList) {
            if (program.getChannel().equals("HRT1")) hrt1List.add(program);
            if (program.getChannel().equals("HRT2")) hrt2List.add(program);
            if (program.getChannel().equals("HRT3")) hrt3List.add(program);
            if (program.getChannel().equals("NOVATV")) novaTvList.add(program);
            if (program.getChannel().equals("RTLTELEVIZIJA")) rtlTelevizijaList.add(program);
            if (program.getChannel().equals("RTL2")) rtl2List.add(program);
            if (program.getChannel().equals("DOMATV")) domaTvList.add(program);
            if (program.getChannel().equals("RTLKOCKICA")) rtlKockicaList.add(program);
            if (program.getChannel().equals("HRT4")) hrt4List.add(program);
        }
        sortedList.add(hrt1List);
        sortedList.add(hrt2List);
        sortedList.add(hrt3List);
        sortedList.add(novaTvList);
        sortedList.add(rtlTelevizijaList);
        sortedList.add(rtl2List);
        sortedList.add(domaTvList);
        sortedList.add(rtlKockicaList);
        sortedList.add(hrt4List);
        Log.d("moviedatabaselog", "poslije obrade");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.epg_tv_cro_parent_item, parent, false);
        return new EpgTvCroChannelsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //  val parent = parents[position]

        holder.tv_epg_tv_parent_item.setText(sortedList.get(position).get(0).getChannel());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.child_recycler.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.child_recycler.setHasFixedSize(true);
        holder.child_recycler.setLayoutManager(linearLayoutManager);
        holder.child_recycler.setRecycledViewPool(viewPool);
        EpgTvCroChannelsChildAdapter epgTvCroChannelsChildAdapter = new EpgTvCroChannelsChildAdapter(sortedList.get(position));
        holder.child_recycler.setAdapter(epgTvCroChannelsChildAdapter);


    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_epg_tv_parent_item;
        private RecyclerView child_recycler;

        ViewHolder(View itemView) {
            super(itemView);
            tv_epg_tv_parent_item = itemView.findViewById(R.id.tv_epg_tv_parent_item);
            child_recycler = itemView.findViewById(R.id.rv_child);
        }


    }

}
