package com.dotcode.duoline.axdlockers.Activities;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
//import android.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroAddress;
import com.dotcode.duoline.axdlockers.Models.RetroCity;
import com.dotcode.duoline.axdlockers.Models.RetroCityList;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity implements SetRequests.GetDataResponse {
    private SearchView searchView;
    private AutoCompleteTextView streetEditText, zipCodeEdiText;
    private RecyclerView recyclerView;
    private Menu menu;

    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 1;
    private List<RetroCity> citiesList = new ArrayList<RetroCity>();
    private AddAddressActivity.ItemsAdapter adapter;
    private ProgressBar progressBar;
    private boolean search;
    private String searchString = "";
    private TextView emptyArrayMessage;
//    private static final int ADD_ADDRESS = 1;
//    private static final int CHOOSE_ADDRESS = 2;
    private RetroCity currentCity;
    private TextView currentCityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        setTitle(getString(R.string.add_address));
        searchView=(SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search_cities));
        recyclerView = (RecyclerView) findViewById(R.id.rvCities);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCities);
        emptyArrayMessage = (TextView) findViewById(R.id.emptyCitiesListMessage);
        streetEditText = (AutoCompleteTextView) findViewById(R.id.streetEditText);
        zipCodeEdiText = (AutoCompleteTextView) findViewById(R.id.zipCodeEditText);
        currentCityTextView = (TextView) findViewById(R.id.currentCityTextView);

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
                        makeCityRequest();
                    }
                }
            }
        });

        streetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setupMenu();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        zipCodeEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setupMenu();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        View closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getQuery().toString().equals("")){
                    search = false;
                    loadedPages = 1;
                }
                searchView.setIconified(true);
                citiesList = new ArrayList<RetroCity>();
                adapter.setList(citiesList);
                makeCityRequest();
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    currentCityTextView.setVisibility(View.GONE);
                } else {
                    currentCityTextView.setVisibility(View.VISIBLE);
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
                citiesList = new ArrayList<RetroCity>();
                adapter.setList(citiesList);
                makeCityRequest();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });


        adapter = new ItemsAdapter(citiesList);
        recyclerView.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        streetEditText.clearFocus();
        makeCityRequest();
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {

        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        if(currentRequestId == Helper.REQUEST_CITIES) {
            if (result instanceof RetroCityList) {
                citiesList = ((RetroCityList) result).getCities();
                isLastPage = ((RetroCityList) result).isPastPage();
                loadedPages = ((RetroCityList) result).getCurrentPage() + 1;
                if (loadedPages == 1) adapter.setList(citiesList);
                else adapter.addToList(citiesList);
            }
            searchString = "";
        }
        if(currentRequestId == Helper.REQUEST_INSERT_ADDRESS) {
            showAlert(AddAddressActivity.this, getString(R.string.address_added), getString(R.string.address_was_successfully_added));
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void makeCityRequest(){
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        emptyArrayMessage.setVisibility(View.INVISIBLE);
        Map<String, String> param = new HashMap<String, String>();
        param.put("expand", "state");
        param.put("sort", "name");
        param.put("per-page", String.valueOf(PAGE_SIZE));
        param.put("page", String.valueOf(loadedPages));
        if (search) {
            param.put("filter[name][like]", searchString);
        }
        new SetRequests(getApplicationContext(), AddAddressActivity.this, Helper.REQUEST_CITIES, param, null);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        this.menu = menu;
        menu.getItem(0).setEnabled(false);
        return true;
    }


    private void setupMenu(){
        if (!streetEditText.getText().toString().equals("") && !zipCodeEdiText.getText().toString().equals("") && currentCity != null)
            menu.getItem(0).setEnabled(true);
        else
            menu.getItem(0).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_save) {
            RetroAddress address = new RetroAddress(0, streetEditText.getText().toString() ,
                    zipCodeEdiText.getText().toString(), currentCity.getId(), currentCity);
            isLoading = true;
            new SetRequests(getApplicationContext(), AddAddressActivity.this, Helper.REQUEST_INSERT_ADDRESS, null, address);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlert(Context ctx, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    class ItemsAdapter extends RecyclerView.Adapter<AddAddressActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroCity> list;
        public ItemsAdapter(List<RetroCity> list){
            this.list = list;
        }

        public void setList(List<RetroCity> list){
            this.list = list;
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        public void addToList(List<RetroCity> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }


        @Override
        public AddAddressActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_city, parent, false);
            return new AddAddressActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddAddressActivity.ItemsAdapter.ItemViewHolder holder, int position) {
            holder.tvCity.setText(list.get(position).getName() + ", " + list.get(position).getState().getName());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvCity;

            public ItemViewHolder(View view){
                super(view);
                tvCity = view.findViewById(R.id.tvCity);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        currentCity = list.get(getAdapterPosition());
                        currentCityTextView.setText(currentCity.getName() + ", " + currentCity.getState().getName());
                        setupMenu();
//                        currentAddress = list.get(getAdapterPosition());
//                        showAlert(AddAddressActivity.this, "Address tapped", "Would you want to take over this address?", CHOOSE_ADDRESS);
                    }
                });

            }
        }
    }
}
