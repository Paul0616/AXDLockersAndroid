package com.dotcode.duoline.axdlockers.Activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
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
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistory;
import com.dotcode.duoline.axdlockers.Models.RetroLockerHistoryList;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddResidentActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private TextView lockerNumber, lockerSize, lockerAddress, buildingUniqueNumber, buildingAddress, currentResidentTextView, emptyResidentsTextView;
    private RecyclerView recyclerView;
    private ConstraintLayout lockerContainer, pickBuilding;
    private ProgressBar progressBar;
    private SearchView searchView;
    private boolean search;
    private String searchString = "";
    private String emptyResidentMessage = "No available residents";
    private Menu menu;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 1;
    private RetroLocker locker;
    private RetroBuilding currentBuilding;
   // private RetroLockerHistory lockerHistory;
    private List<RetroFilteredResident> residents = new ArrayList<RetroFilteredResident>();
    private RetroFilteredResident currentResident;
    private AddResidentActivity.ItemsAdapter adapter;

    private String qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resident);
        setTitle(getString(R.string.add_resident));

        lockerNumber = (TextView) findViewById(R.id.lockerNumber);
        lockerSize = (TextView) findViewById(R.id.lockerSize);
        lockerAddress = (TextView) findViewById(R.id.address);
        buildingUniqueNumber = (TextView) findViewById(R.id.buildingUniqueNumber);
        buildingAddress = (TextView) findViewById(R.id.buildingAddress);
        currentResidentTextView = (TextView) findViewById(R.id.currentResidentTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBarResident);

        lockerContainer = (ConstraintLayout) findViewById(R.id.lockerConstraintLayout);
        pickBuilding = (ConstraintLayout) findViewById(R.id.pickBuildingConstraintLayout);
        pickBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentBuilding = null;
                buildingAddress.setText("-");
                buildingUniqueNumber.setText("-");
                startActivity(new Intent(AddResidentActivity.this,ChooseBuildingActivity.class));
            }
        });
        emptyResidentsTextView = (TextView) findViewById(R.id.emptyResidentsListMessage);
        emptyResidentsTextView.setText(emptyResidentMessage);
        qrCode = getIntent().getStringExtra("qrCode");
        searchView=(SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search cities");
        recyclerView = (RecyclerView)findViewById(R.id.rvResidents);
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
                        makeResidentRequest();
                    }
                }
            }
        });

        View closeButton = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getQuery().toString().equals("")){
                    search = false;
                    loadedPages = 1;
                }
                searchView.setIconified(true);
                residents = new ArrayList<RetroFilteredResident>();
                adapter.setList(residents);
                makeResidentRequest();
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    currentResidentTextView.setVisibility(View.GONE);
                    lockerContainer.setVisibility(View.GONE);
                } else {
                    currentResidentTextView.setVisibility(View.VISIBLE);
                    lockerContainer.setVisibility(View.VISIBLE);
                }
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
                residents = new ArrayList<RetroFilteredResident>();
                adapter.setList(residents);
                makeResidentRequest();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });


        adapter = new ItemsAdapter(residents);
        recyclerView.setAdapter(adapter);
        SaveSharedPreferences.setBuildingNull(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLoading = true;

        currentBuilding = SaveSharedPreferences.getBuilding(getApplicationContext());

        if(currentBuilding == null) {
            Map<String, String> param = new HashMap<String, String>();
            param.put("filter[qrCode]", qrCode);
            param.put("expand", "address.city.state");
            buildingUniqueNumber.setText("-");
            buildingAddress.setText("-");
            loadedPages = 1;
            adapter.setList(new ArrayList<RetroFilteredResident>());
            emptyResidentsTextView.setVisibility(View.INVISIBLE);

            setupMenu();
            progressBar.setVisibility(View.VISIBLE);
            new SetRequests(getApplicationContext(), AddResidentActivity.this, Helper.REQUEST_LOCKERS, param, null);
        } else {
            emptyResidentMessage = "No available residents";
            buildingUniqueNumber.setText(currentBuilding.getBuildingUniqueNumber());
            buildingAddress.setText(currentBuilding.getAddress().getStreetName() + ", " + currentBuilding.getAddress().getCity().getName() + ", " +
                    currentBuilding.getAddress().getCity().getState().getName() +", " +currentBuilding.getAddress().getZipCode());
            loadedPages = 1;
            adapter.setList(new ArrayList<RetroFilteredResident>());
            makeResidentRequest();
        }

    }

    private void setupMenu(){
        if (menu != null) {
            if (currentResident != null)
                menu.getItem(0).setEnabled(true);
            else
                menu.getItem(0).setEnabled(false);
        }
    }

    private void makeResidentRequest(){
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        emptyResidentsTextView.setVisibility(View.INVISIBLE);
        currentResident = null;
        setupMenu();
        currentResidentTextView.setText("-");
        Map<String, String> param = new HashMap<String, String>();
        if (currentBuilding != null) {
            param.put("filter[buildingId]", "" + currentBuilding.getId());
        }
        param.put("per-page", ""+PAGE_SIZE);
        param.put("page", ""+loadedPages);
        param.put("sort", "suiteNumber");
        param.put("expand", "resident,building.address.city.state");
        if (search) {
            param.put("filter[residentName]", searchString);
            param.put("filter[suiteNumber]", searchString);
        }
        new SetRequests(getApplicationContext(), AddResidentActivity.this,
                Helper.REQUEST_FILTERED_RESIDENTS, param, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        this.menu = menu;
        menu.getItem(0).setEnabled(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            Intent i = new Intent(AddResidentActivity.this, SecurityCodeActivity.class);
            Gson gson = new Gson();
            String json = gson.toJson(locker);
            i.putExtra("JSON_LOCKER", json);
//            gson = new Gson();
//            json = gson.toJson(currentBuilding);
//            i.putExtra("JSON_BUILDING", json);
            gson = new Gson();
            json = gson.toJson(currentResident);
            i.putExtra("JSON_RESIDENT", json);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {

        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        if (currentRequestId == Helper.REQUEST_LOCKERS){
            if(result != null && result instanceof RetroLocker) {
                locker = ((RetroLocker) result);
                lockerNumber.setText(locker.getNumber());
                if (locker.getSize() != null){
                    lockerSize.setText(locker.getSize());
                }
                lockerAddress.setText(locker.getAddress().getStreetName() + ", " + locker.getAddress().getCity().getName() + ", " +
                        locker.getAddress().getCity().getState().getName() + ", " + locker.getAddress().getZipCode());
                if (currentBuilding == null) {
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("filter[qrCode]", qrCode);
                    param.put("sort", "-createdAt");
                    new SetRequests(getApplicationContext(), AddResidentActivity.this,
                            Helper.REQUEST_LOCKER_HISTORIES, param, null);
                }
            }
        }
        if (currentRequestId == Helper.REQUEST_LOCKER_HISTORIES){
            if(result != null && result instanceof RetroLockerHistoryList) {
                if (((RetroLockerHistoryList) result).getLockerHistories().size() > 0){
                    List<RetroLockerHistory> histories = ((RetroLockerHistoryList) result).getLockerHistories();
                    //lockerHistory = ((RetroLockerHistoryList) result).getFirstLockerHistory();

                    if (histories.size() > 0){
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("filter[buildingUniqueNumber]", histories.get(0).getBuildingUniqueNumber());
                        param.put("expand", "address.city.state");
                        new SetRequests(getApplicationContext(), AddResidentActivity.this,
                                Helper.REQUEST_CHECK_BUILDING, param, null);
                    } else {

                       // makeResidentRequest();
                        emptyResidentMessage = "You have to choose building\nto see its residents";
                        adapter.setList(residents);
                    }


                } else {
                    emptyResidentMessage = "You have to choose building\nto see its residents";
                    adapter.setList(residents);
//                    makeResidentRequest();
                }

            }
        }

        if (currentRequestId == Helper.REQUEST_CHECK_BUILDING){
            if(result != null && result instanceof RetroBuildingList) {
                if (((RetroBuildingList) result).getBuildings().size() > 0){
                    currentBuilding = ((RetroBuildingList) result).getBuildings().get(0);
                    buildingUniqueNumber.setText(currentBuilding.getBuildingUniqueNumber());
                    buildingAddress.setText(currentBuilding.getAddress().getStreetName() + ", " + currentBuilding.getAddress().getCity().getName() + ", " +
                            currentBuilding.getAddress().getCity().getState().getName() +", " +currentBuilding.getAddress().getZipCode());
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("filter[buildingId]", "" + currentBuilding.getId());
                    param.put("per-page", ""+PAGE_SIZE);
                    param.put("page", ""+loadedPages);
                    param.put("sort", "suiteNumber");
                    param.put("expand", "resident,building.address.city.state");
                    new SetRequests(getApplicationContext(), AddResidentActivity.this,
                            Helper.REQUEST_FILTERED_RESIDENTS, param, null);
                } else {
                    buildingUniqueNumber.setText("-");
                    buildingAddress.setText("-");
                    makeResidentRequest();
                }

            }
        }

        if (currentRequestId == Helper.REQUEST_FILTERED_RESIDENTS){
            isLoading = false;
            progressBar.setVisibility(View.INVISIBLE);
            if(result != null && result instanceof RetroFilteredResidentsList) {
                residents = ((RetroFilteredResidentsList) result).getFilteredResidents();
                isLastPage = ((RetroFilteredResidentsList) result).isPastPage();
                loadedPages = ((RetroFilteredResidentsList) result).getCurrentPage() + 1;
                if (loadedPages == 1) adapter.setList(residents);
                else adapter.addToList(residents);

            }
            searchString = "";
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    class ItemsAdapter extends RecyclerView.Adapter<AddResidentActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroFilteredResident> list;
        public ItemsAdapter(List<RetroFilteredResident> list){
            this.list = list;
        }

        public void setList(List<RetroFilteredResident> list){
            this.list = list;
            if (list.size() == 0) {
                emptyResidentsTextView.setText(emptyResidentMessage);
                emptyResidentsTextView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
        public void addToList(List<RetroFilteredResident> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0) {
                emptyResidentsTextView.setText(emptyResidentMessage);
                emptyResidentsTextView.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }


        @Override
        public AddResidentActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_resident, parent, false);
            return new AddResidentActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddResidentActivity.ItemsAdapter.ItemViewHolder holder, int position) {
           holder.tvName.setText(list.get(position).getResident().getFirstName() + " " + list.get(position).getResident().getLastName());
           holder.tvSuiteNumber.setText(list.get(position).getSuiteNumber());
           holder.tvPhone.setText(list.get(position).getResident().getPhoneNumber());
           holder.tvEmail.setText(list.get(position).getResident().getEmail());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvName;
            TextView tvSuiteNumber;
            TextView tvPhone;
            TextView tvEmail;

            public ItemViewHolder(View view){
                super(view);
                tvName = view.findViewById(R.id.tvName);
                tvEmail = view.findViewById(R.id.tvEmail);
                tvPhone = view.findViewById(R.id.tvPhone);
                tvSuiteNumber = view.findViewById(R.id.tvSuiteNumber);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    currentResident = list.get(getAdapterPosition());
                    currentResidentTextView.setText(currentResident.getResident().getFirstName() + " " + currentResident.getResident().getLastName());
                    setupMenu();

                    }
                });

            }
        }
    }
}
