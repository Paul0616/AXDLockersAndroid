package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dotcode.duoline.axdlockers.Models.RetroFilteredResident;
import com.dotcode.duoline.axdlockers.Models.RetroFilteredResidentsList;
import com.dotcode.duoline.axdlockers.Models.RetroFullName;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResidentsFilteredActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private ArrayList<String> first4Strings = new ArrayList<String>();
    private Menu menu;
    private RecyclerView recyclerView;
    private CardView addOrphanCardView;
    private ProgressBar progressBar;
    private TextView emptyString, currentResidentTextView, addOrphanTextView;
    private RetroFilteredResident currentResident;
    private ImageView selectedIcon;
    private List<RetroFilteredResident> residents = new ArrayList<RetroFilteredResident>();

    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 1;
    private ResidentsFilteredActivity.ItemsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residents_filtered);

        ArrayList<String> lines = getIntent().getStringArrayListExtra("first4lines");
        recyclerView = (RecyclerView) findViewById(R.id.residentsRecyclerView);
        addOrphanCardView = (CardView) findViewById(R.id.bAddOrphanParcelCArdView);
        addOrphanTextView = (TextView) findViewById(R.id.bAddOrphanTextView);
        currentResidentTextView = (TextView) findViewById(R.id.currentResidentTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        emptyString = (TextView) findViewById(R.id.emptyMessage2);
        selectedIcon = (ImageView) findViewById(R.id.selectedIcon);
        if(lines != null){
            first4Strings = lines;
           // Toast.makeText(this, ""+first4Strings.toString(), Toast.LENGTH_SHORT).show();
        }
        addOrphanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(ResidentsFilteredActivity.this, "ADD ORPHAN", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ResidentsFilteredActivity.this, AddOrphanParcelActivity.class);
                i.putStringArrayListExtra("first4lines", first4Strings);
                startActivity(i);
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
        emptyString.setVisibility(View.VISIBLE);
        selectedIcon.setVisibility(View.INVISIBLE);
        addOrphanCardView.setEnabled(true);
        addOrphanTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

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

        selectedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentResident = null;
                currentResidentTextView.setText("-");
                selectedIcon.setVisibility(View.INVISIBLE);
                addOrphanCardView.setEnabled(true);
                addOrphanTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                setupMenu();
            }
        });

        adapter = new ItemsAdapter(residents);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeResidentRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        this.menu = menu;
        setupMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_next) {
            Intent i = new Intent(ResidentsFilteredActivity.this, ConfirmResidentActivity.class);
            Gson gson = new Gson();
            String json = gson.toJson(currentResident);
            i.putExtra("JSON_RESIDENT", json);
            startActivity(i);
//            Toast.makeText(this, "NEXT", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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
        emptyString.setVisibility(View.INVISIBLE);
        currentResident = null;
        setupMenu();
        currentResidentTextView.setText("-");
        Map<String, String> param = new HashMap<String, String>();

        if (!first4Strings.isEmpty()) {
            RetroFullName fullName = new RetroFullName();
            fullName.setFullName(first4Strings.get(0));
            if(!first4Strings.get(1).equals("")){
                fullName.setUnitNumber(first4Strings.get(1));
            }

            param.put("per-page", ""+PAGE_SIZE);
            param.put("page", ""+loadedPages);
            param.put("expand", "resident,building.address.city.state.country");

            new SetRequests(getApplicationContext(), ResidentsFilteredActivity.this,
                    Helper.REQUEST_RESIDENTS_GET_BY_FULL_NAME_OR_UNIT, param, fullName);
        }

    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        if (currentRequestId == Helper.REQUEST_RESIDENTS_GET_BY_FULL_NAME_OR_UNIT){
            isLoading = false;
            progressBar.setVisibility(View.INVISIBLE);
            if(result != null && result instanceof RetroFilteredResidentsList) {
                residents = ((RetroFilteredResidentsList) result).getFilteredResidents();
                isLastPage = ((RetroFilteredResidentsList) result).isPastPage();
                loadedPages = ((RetroFilteredResidentsList) result).getCurrentPage() + 1;
                if (loadedPages == 1) adapter.setList(residents);
                else adapter.addToList(residents);

            }
        }

    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    class ItemsAdapter extends RecyclerView.Adapter<ResidentsFilteredActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroFilteredResident> list;
        public ItemsAdapter(List<RetroFilteredResident> list){
            this.list = list;
        }

        public void setList(List<RetroFilteredResident> list){
            this.list = list;
            if (list.size() == 0) {
                emptyString.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
        public void addToList(List<RetroFilteredResident> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0) {
                emptyString.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }


        @Override
        public ResidentsFilteredActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_resident_filtered, parent, false);
            return new ResidentsFilteredActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ResidentsFilteredActivity.ItemsAdapter.ItemViewHolder holder, int position) {
            holder.tvName.setText(list.get(position).getResident().getFirstName() + " " + list.get(position).getResident().getLastName());
            holder.tvUnitNumber.setText(list.get(position).getSuiteNumber());
            holder.tvBuildingName.setText(list.get(position).getBuilding().getName());
            holder.tvStreet.setText(list.get(position).getBuilding().getAddress().getStreetName());
            holder.tvAddress.setText(list.get(position).getBuilding().getAddress().getZipCode() + " " +
                    list.get(position).getBuilding().getAddress().getCity().getName() + " " +
                    list.get(position).getBuilding().getAddress().getCity().getState().getName() + " " +
                    list.get(position).getBuilding().getAddress().getCity().getState().getCountry().getName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvName;
            TextView tvUnitNumber;
            TextView tvBuildingName;
            TextView tvStreet;
            TextView tvAddress;

            public ItemViewHolder(View view){
                super(view);
                tvName = view.findViewById(R.id.tvName);
                tvStreet = view.findViewById(R.id.tvStreet);
                tvBuildingName = view.findViewById(R.id.tvBuildingName);
                tvUnitNumber = view.findViewById(R.id.tvUnitNumber);
                tvAddress = view.findViewById(R.id.tvAddress);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedIcon.setVisibility(View.VISIBLE);
                        addOrphanCardView.setEnabled(false);
                        addOrphanTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_overlay));
                        currentResident = list.get(getAdapterPosition());
                        currentResidentTextView.setText(currentResident.getResident().getFirstName() + " " + currentResident.getResident().getLastName()
                                + " - unit #:" + currentResident.getSuiteNumber());
                        setupMenu();

                    }
                });

            }
        }
    }
}
