package com.aero51.moviedatabase.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.repository.model.epg.EpgChannel;
import com.aero51.moviedatabase.repository.model.epg.ChannelWithPrograms;
import com.aero51.moviedatabase.repository.model.epg.EpgProgram;
import com.aero51.moviedatabase.ui.adapter.EpgAdapter;
import com.aero51.moviedatabase.utils.ChannelItemClickListener;
import com.aero51.moviedatabase.utils.ChannelsPreferenceHelper;
import com.aero51.moviedatabase.utils.Constants;
import com.aero51.moviedatabase.utils.EndlessRecyclerViewScrollListener;
import com.aero51.moviedatabase.utils.ProgramItemClickListener;
import com.aero51.moviedatabase.utils.Resource;
import com.aero51.moviedatabase.utils.SpeedyLinearLayoutManager;
import com.aero51.moviedatabase.utils.Status;
import com.aero51.moviedatabase.viewmodel.EpgViewModel;
import com.aero51.moviedatabase.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EpgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EpgFragment extends Fragment implements ProgramItemClickListener, ChannelItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private EpgViewModel epgViewModel;
    private RecyclerView recycler_view_epg_tv;
    private SwipeRefreshLayout pullToRefresh;
    private EpgAdapter epgAdapter;

    private SharedViewModel sharedViewModel;
    private List<ChannelWithPrograms> programsForChannellList;
    private MutableLiveData<Boolean> isLoading;
    private LinearLayoutManager linearLayoutManager;

    private List<EpgChannel> channelList;

    private EndlessRecyclerViewScrollListener scrollListener;

    public EpgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EpgTvFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EpgFragment newInstance(String param1, String param2) {
        EpgFragment fragment = new EpgFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        channelList = ChannelsPreferenceHelper.extractChannels();

        epgViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(EpgViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_epg, container, false);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpRecyclerView();
                //refreshData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });
        recycler_view_epg_tv = view.findViewById(R.id.recycler_view_cro_parent);
        //this messes up scroll listener
        //recycler_view_epg_tv.setHasFixedSize(true);
        //recycler_view_epg_tv.setNestedScrollingEnabled(true);
        if (channelList.size() > 0) {
            setUpRecyclerView();
        }

        return view;
    }
    

    private void setUpRecyclerView() {

        linearLayoutManager = new SpeedyLinearLayoutManager(getContext(), SpeedyLinearLayoutManager.VERTICAL, false);
        recycler_view_epg_tv.setLayoutManager(linearLayoutManager);

        programsForChannellList = new ArrayList<>();
        epgAdapter = new EpgAdapter(getContext(), channelList, programsForChannellList, this, this);
        recycler_view_epg_tv.setAdapter(epgAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(Constants.LOG, "EndlessRecyclerViewScrollListener page: " + page + " total items count: " + totalItemsCount);
                fetchProgramsForMultipleChannels();
            }
        };
        recycler_view_epg_tv.addOnScrollListener(scrollListener);
        fetchProgramsForMultipleChannels();

    }

    private void fetchProgramsForMultipleChannels() {
        isLoading = new MutableLiveData<>();
        isLoading.setValue(false);
        sharedViewModel.setHasEpgTvFragmentFinishedLoading(false);


        recycler_view_epg_tv.removeOnScrollListener(scrollListener);
        int temp = epgAdapter.getItemCount();
        isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (!loading) {
                    int adapterItemCount = epgAdapter.getItemCount();
                    if (adapterItemCount < temp + 5 && channelList.size() > adapterItemCount) {
                        registerGetProgramsForChannel(channelList.get(adapterItemCount).getName());

                    } else {
                        recycler_view_epg_tv.addOnScrollListener(scrollListener);
                        sharedViewModel.setHasEpgTvFragmentFinishedLoading(true);
                        isLoading.removeObserver(this);
                    }
                }
            }
        });
    }

    private void registerGetProgramsForChannel(String channelName) {
        isLoading.setValue(true);
        epgViewModel.getProgramsForChannel(channelName).observe(getViewLifecycleOwner(), new Observer<Resource<List<EpgProgram>>>() {
            @Override
            public void onChanged(Resource<List<EpgProgram>> listResource) {
                Log.d(Constants.LOG, "EpgTvFragment onChanged listResource.status: " + listResource.status);
                if (listResource.data.size() > 0 && listResource.status == Status.SUCCESS) {
                    Log.d(Constants.LOG, "EpgTvFragment onChanged channelName: " + channelName + " ,get Programs code: " + listResource.code + " , status: " + listResource.status + " list size: " + listResource.data.size() + " ,message: " + listResource.message);
                    epgViewModel.getResourceLiveData().removeObserver(this);

                    ChannelWithPrograms item = epgViewModel.calculateTimeStuff(listResource.data);

                    programsForChannellList.add(item);
                    epgAdapter.notifyItemInserted(programsForChannellList.size() - 1);
                    isLoading.setValue(false);
                }
            }
        });

    }

    @Override
    public void onItemClick(int position, int db_id, EpgProgram epgProgram) {
        //intentional crash
        // Toast.makeText(null, "Crashed before shown.", Toast.LENGTH_SHORT).show();
        sharedViewModel.changeToEpgDetailsFragment(position, epgProgram);
        ArrayList<String> daysOfWeek = new ArrayList<String>();

    }

    @Override
    public void onItemClick(ChannelWithPrograms channelWithPrograms) {
        Log.d(Constants.LOG, "channel item clicked: " + channelWithPrograms.getChannel().getName());
        sharedViewModel.changeToEpgAllProgramsFragment(channelWithPrograms);
    }
}