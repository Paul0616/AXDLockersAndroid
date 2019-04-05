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

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroAddressList;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseAddressActivity extends AppCompatActivity implements SetRequests.GetDataResponse {

    private RecyclerView recyclerView;
    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 1;
    private List<RetroAddress> addressList = new ArrayList<RetroAddress>();
    private ChooseAddressActivity.ItemsAdapter adapter;
    private ProgressBar progressBar;
    private boolean search;
    private String searchString = "";
    private TextView emptyArrayMessage;
    private static final int ADD_ADDRESS = 1;
    private static final int CHOOSE_ADDRESS = 2;
    private RetroAddress currentAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);
        setTitle("Choose Address");
        recyclerView = (RecyclerView) findViewById(R.id.rvAddresses);
        progressBar = (ProgressBar) findViewById(R.id.progressBarAddress);
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
                       makeAddressRequest();
                    }
                }
            }
        });


        adapter = new ItemsAdapter(addressList);
        recyclerView.setAdapter(adapter);
    }

    private void makeAddressRequest(){
        progressBar.setVisibility(View.VISIBLE);
        emptyArrayMessage.setVisibility(View.INVISIBLE);
        Map<String, String> param = new HashMap<String, String>();
        param.put("expand", "city.state");
        param.put("sort", "streetName");
        param.put("per-page", String.valueOf(PAGE_SIZE));
        param.put("page", String.valueOf(loadedPages));
        if (search) {
            param.put("filter[streetName][like]", searchString);
        }
        new SetRequests(getApplicationContext(), ChooseAddressActivity.this, Helper.REQUEST_ADDRESSES, param);
    }

    @Override
    protected void onResume() {
        super.onResume();


        isLoading = true;

       makeAddressRequest();
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        if(result instanceof RetroAddressList){
            addressList = ((RetroAddressList) result).getAddresses();
            isLastPage = ((RetroAddressList) result).isPastPage();
            loadedPages = ((RetroAddressList) result).getCurrentPage() + 1;
            if (loadedPages == 1) adapter.setList(addressList);
            else adapter.addToList(addressList);
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

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
                addressList = new ArrayList<RetroAddress>();
                adapter.setList(addressList);
                makeAddressRequest();
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
                addressList = new ArrayList<RetroAddress>();
                adapter.setList(addressList);
                makeAddressRequest();
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
        if (id == R.id.action_add) {
            showAlert(ChooseAddressActivity.this, "Add Address", "Would you want to add new ADDRESS?", ADD_ADDRESS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showAlert(Context ctx, String title, String msg, final int code) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
//        builder.setIcon(R.drawable.ic_error_outline_yellow_24dp);
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
                switch (code) {
                    case ADD_ADDRESS:
                        startActivity(new Intent(ChooseAddressActivity.this, AddAddressActivity.class));
                        break;
                    case CHOOSE_ADDRESS:
                        SaveSharedPreferences.setAddress(getApplicationContext(), currentAddress);
                        finish();
                        break;
                }


            }
        });
        builder.show();
    }

    class ItemsAdapter extends RecyclerView.Adapter<ChooseAddressActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroAddress> list;
        public ItemsAdapter(List<RetroAddress> list){
            this.list = list;
        }

        public void setList(List<RetroAddress> list){
            this.list = list;
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        public void addToList(List<RetroAddress> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }


        @Override
        public ChooseAddressActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_address, parent, false);
            return new ChooseAddressActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChooseAddressActivity.ItemsAdapter.ItemViewHolder holder, int position) {
            holder.tvStreet.setText(list.get(position).getStreetName());
            holder.tvCity.setText(list.get(position).getCity().getName() + ", " + list.get(position).getCity().getState().getName());
            holder.tvZipCode.setText(list.get(position).getZipCode());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvStreet, tvCity, tvZipCode;

            public ItemViewHolder(View view){
                super(view);
                tvStreet = view.findViewById(R.id.tvStreet);
                tvCity = view.findViewById(R.id.tvCity);
                tvZipCode = view.findViewById(R.id.tvZipCode);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentAddress = list.get(getAdapterPosition());
                        showAlert(ChooseAddressActivity.this, "Address tapped", "Would you want to take over this address?", CHOOSE_ADDRESS);
                    }
                });

            }
        }
    }
}
