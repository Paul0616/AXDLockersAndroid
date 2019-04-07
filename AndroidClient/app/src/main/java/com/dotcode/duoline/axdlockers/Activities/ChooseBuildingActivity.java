package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroBuilding;
import com.dotcode.duoline.axdlockers.Models.RetroBuildingList;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseBuildingActivity extends AppCompatActivity implements SetRequests.GetDataResponse{

    private RecyclerView recyclerView;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 1;
    private List<RetroBuilding> buildingsList = new ArrayList<RetroBuilding>();
    private ChooseBuildingActivity.ItemsAdapter adapter;
    private ProgressBar progressBar;
    private boolean search;
    private String searchString = "";
    private TextView emptyArrayMessage;
    private RetroBuilding currentBuilding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_building);
        setTitle("Choose Building");
        recyclerView = (RecyclerView) findViewById(R.id.rvBuildings);
        progressBar = (ProgressBar) findViewById(R.id.progressBarBuilding);
        emptyArrayMessage = (TextView) findViewById(R.id.emptyMessage);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if(!isLoading && !isLastPage){
                    if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        isLoading = true;
                        makeBuildingRequest();
                    }
                }
            }
        });


        adapter = new ItemsAdapter(buildingsList);
        recyclerView.setAdapter(adapter);
    }

    private void makeBuildingRequest(){
        progressBar.setVisibility(View.VISIBLE);
        emptyArrayMessage.setVisibility(View.INVISIBLE);
        Map<String, String> param = new HashMap<String, String>();
        param.put("expand", "address.city.state");
        param.put("sort", "buildingUniqueNumber");
        param.put("per-page", String.valueOf(PAGE_SIZE));
        param.put("page", String.valueOf(loadedPages));
        if (search) {
            param.put("filter[or][][buildingUniqueNumber][like]", searchString);
            param.put("filter[or][][name][like]", searchString);
        }
        new SetRequests(getApplicationContext(), ChooseBuildingActivity.this, Helper.REQUEST_CHECK_BUILDING, param, null);
    }

    @Override
    protected void onResume() {
        super.onResume();


        isLoading = true;

        makeBuildingRequest();
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        if(result instanceof RetroBuildingList){
            buildingsList = ((RetroBuildingList) result).getBuildings();
            isLastPage = ((RetroBuildingList) result).isPastPage();
            loadedPages = ((RetroBuildingList) result).getCurrentPage() + 1;
            if (loadedPages == 1) adapter.setList(buildingsList);
            else adapter.addToList(buildingsList);
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);


        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getQuery().toString().equals("")){
                    search = false;
                    loadedPages = 1;
                }
                searchView.setIconified(true);
                buildingsList = new ArrayList<RetroBuilding>();
                adapter.setList(buildingsList);
                makeBuildingRequest();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                search = true;
                searchString = s;
                loadedPages = 1;
                buildingsList = new ArrayList<RetroBuilding>();
                adapter.setList(buildingsList);
                makeBuildingRequest();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlert(Context ctx, String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveSharedPreferences.setBuilding(getApplicationContext(), currentBuilding);
                finish();
            }
        });
        builder.show();
    }

    class ItemsAdapter extends RecyclerView.Adapter<ChooseBuildingActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroBuilding> list;
        public ItemsAdapter(List<RetroBuilding> list){
            this.list = list;
        }

        public void setList(List<RetroBuilding> list){
            this.list = list;
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        public void addToList(List<RetroBuilding> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }


        @Override
        public ChooseBuildingActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_building, parent, false);
            return new ChooseBuildingActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChooseBuildingActivity.ItemsAdapter.ItemViewHolder holder, int position) {
            holder.buildingUniqueNumber.setText(list.get(position).getBuildingUniqueNumber());
            holder.buldingAddress.setText(list.get(position).getName()+", "+list.get(position).getAddress().getStreetName()+ ", " +
                    list.get(position).getAddress().getCity().getName() + ", " + list.get(position).getAddress().getCity().getState().getName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView buildingUniqueNumber, buldingAddress;

            public ItemViewHolder(View view){
                super(view);
                buildingUniqueNumber = view.findViewById(R.id.buildingUniqueNumber);
                buldingAddress = view.findViewById(R.id.buildingAddress);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentBuilding = list.get(getAdapterPosition());
                        showAlert(ChooseBuildingActivity.this, "Building tapped", "Would you want to take over this building?");
                    }
                });

            }
        }
    }
}
