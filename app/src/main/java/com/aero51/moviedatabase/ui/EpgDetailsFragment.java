package com.aero51.moviedatabase.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aero51.moviedatabase.BuildConfig;
import com.aero51.moviedatabase.R;
import com.aero51.moviedatabase.databinding.FragmentEpgDetailsBinding;
import com.aero51.moviedatabase.repository.model.epg.EpgProgram;
import com.aero51.moviedatabase.repository.model.tmdb.credits.ActorSearchResponse;
import com.aero51.moviedatabase.ui.adapter.ActorSearchAdapter;
import com.aero51.moviedatabase.utils.Constants;
import com.aero51.moviedatabase.utils.EndlessRecyclerViewScrollListener;
import com.aero51.moviedatabase.utils.Resource;
import com.aero51.moviedatabase.utils.Status;
import com.aero51.moviedatabase.viewmodel.EpgDetailsViewModel;
import com.aero51.moviedatabase.viewmodel.SharedViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EpgDetailsFragment extends Fragment implements ActorSearchAdapter.ItemClickListener {

    private FragmentEpgDetailsBinding binding;
    private SharedViewModel sharedViewModel;
    private EpgDetailsViewModel epgDetailsViewModel;
    private ActorSearchAdapter actorSearchAdapter;
    private List<ActorSearchResponse.ActorSearch> actorSearchList;
    private MutableLiveData<Boolean> isLoading;
    private EndlessRecyclerViewScrollListener scrollListener;
    private List<String> actors;

    public EpgDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        epgDetailsViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(EpgDetailsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEpgDetailsBinding.inflate(inflater, container, false);


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //toolbar.setTitle("text");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                Log.d(Constants.LOG, "Toolbar clicked!");
                showBackButton(false);
            }
        });

        showBackButton(true);
        showToolbar(true);
        showBottomNavigation(true);


        //actorSearchRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.actorSearchRecyclerView.setLayoutManager(linearLayoutManager);

        actorSearchList = new ArrayList<>();
        actorSearchAdapter = new ActorSearchAdapter(actorSearchList, EpgDetailsFragment.this::onItemClick);
        binding.actorSearchRecyclerView.setAdapter(actorSearchAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(Constants.LOG, "EndlessRecyclerViewScrollListener page: " + page + " total items count: " + totalItemsCount);
                multipleActorsFetch();
            }
        };
        binding.actorSearchRecyclerView.addOnScrollListener(scrollListener);

        sharedViewModel.getLiveDataProgram().observe(getViewLifecycleOwner(), new Observer<EpgProgram>() {
            @Override
            public void onChanged(EpgProgram epgProgram) {
                // sharedViewModel.getLiveDataProgram().removeObserver(this);
                binding.textViewTitle.setText(epgProgram.getTitle());
                binding.textViewDate.setText(epgProgram.getDate() + "");
                binding.textViewDescription.setText(epgProgram.getDesc());
                // Picasso.get().load(epgProgram.getIcon()).into(image_view);
                Log.d(Constants.LOG, "icon: " + epgProgram.getIcon());
                //  Picasso.get().load(epgProgram.getIcon()).into(image_view);
                Picasso.get().load(epgProgram.getIcon()).fit().centerCrop().placeholder(R.drawable.picture_template).into(binding.imageViewProgram, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        binding.imageViewProgram.setBackgroundResource(R.drawable.picture_template);
                    }
                });

                Uri picture_path = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/" + epgProgram.getChannel());
                Picasso.get().load(picture_path).placeholder(R.drawable.picture_template).into(binding.imageViewChannel);
                binding.textViewCast.setText(epgProgram.getCredits());

                extractJsonCredits(epgProgram.getCredits());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }


    private void extractJsonCredits(String credits) {

        List<String> writers = new ArrayList<>();
        List<String> directors = new ArrayList<>();
        actors = new ArrayList<>();
        if (credits != null) {
            try {
                JSONObject jsonObjCredits = new JSONObject(credits);
                JSONArray ja_writers = jsonObjCredits.getJSONArray("Writers");

                for (int i = 0; i < ja_writers.length(); i++) {
                    writers.add(ja_writers.getString(i));
                }

                JSONArray ja_directors = jsonObjCredits.getJSONArray("Directors");
                for (int i = 0; i < ja_directors.length(); i++) {
                    directors.add(ja_directors.getString(i));
                }

                JSONArray ja_actors = jsonObjCredits.getJSONArray("Actors");
                for (int i = 0; i < ja_actors.length(); i++) {
                    actors.add(ja_actors.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(Constants.LOG, "writers!: " + writers.toString());
            Log.d(Constants.LOG, "directors!: " + directors.toString());
            Log.d(Constants.LOG, "actors!: " + actors.toString());

            multipleActorsFetch();
        }

    }

    private void multipleActorsFetch() {
        isLoading = new MutableLiveData<>();
        isLoading.setValue(false);

        binding.actorSearchRecyclerView.removeOnScrollListener(scrollListener);

        int temp = actorSearchAdapter.getItemCount();
        isLoading.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (!loading) {
                    int adapterItemCount = actorSearchAdapter.getItemCount();
                    if (adapterItemCount < temp + 5 && actors.size() > adapterItemCount) {
                        registerGetActor(actors.get(adapterItemCount));
                    } else {
                        binding.actorSearchRecyclerView.addOnScrollListener(scrollListener);
                        isLoading.removeObserver(this);
                    }
                }
            }
        });

    }

    private void registerGetActor(String actor_name) {
        isLoading.setValue(true);
        epgDetailsViewModel.getActorSearch(actor_name).observe(getViewLifecycleOwner(), new Observer<Resource<ActorSearchResponse.ActorSearch>>() {
            @Override
            public void onChanged(Resource<ActorSearchResponse.ActorSearch> actorSearchResource) {
                if (actorSearchResource.getStatus() == Status.SUCCESS && actorSearchResource.getData() != null) {
                    epgDetailsViewModel.getLiveActorSearchResult().removeObserver(this);
                    Log.d(Constants.LOG, "actorSearchResource: " + actorSearchResource.getData().getName() + " , " + actorSearchResource.getData().getId());
                    actorSearchList.add(actorSearchResource.getData());
                    actorSearchAdapter.notifyItemInserted(actorSearchList.size() - 1);
                    isLoading.setValue(false);
                }
            }
        });


    }

    private void showToolbar(boolean isShown) {
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
        appBarLayout.setExpanded(isShown, true);
    }

    private void showBottomNavigation(boolean isShown) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        ViewGroup.LayoutParams layoutParams = bottomNavigationView.getLayoutParams();
        if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior =
                    ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
            if (behavior instanceof HideBottomViewOnScrollBehavior) {
                HideBottomViewOnScrollBehavior<BottomNavigationView> hideShowBehavior =
                        (HideBottomViewOnScrollBehavior<BottomNavigationView>) behavior;
                if (isShown) {
                    hideShowBehavior.slideUp(bottomNavigationView);
                } else {
                    hideShowBehavior.slideDown(bottomNavigationView);
                }
            }
        }
    }

    private void showBackButton(boolean isShown) {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(isShown);
        }
    }


    @Override
    public void onItemClick(ActorSearchResponse.ActorSearch actorSearch, int position) {
        sharedViewModel.changeToActorFragment(position, actorSearch.getId());
    }
}