package com.dotcode.duoline.axdlockers.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dotcode.duoline.axdlockers.Models.RetroLocker;
import com.dotcode.duoline.axdlockers.Models.RetroLockerList;
import com.dotcode.duoline.axdlockers.Network.SetRequests;
import com.dotcode.duoline.axdlockers.R;
import com.dotcode.duoline.axdlockers.Utils.Helper;
import com.dotcode.duoline.axdlockers.Utils.SaveSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockersListActivity extends AppCompatActivity implements SetRequests.GetDataResponse{

    private static final int PAGE_SIZE = 20;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int loadedPages = 0;
    private ProgressBar progressBar;
    private TextView emptyArrayMessage;
    private List<RetroLocker> lockersList = new ArrayList<RetroLocker>();
    private LockersListActivity.ItemsAdapter adapter;
    private RecyclerView recyclerView;
    private String lockerNumber, lockerZip, lockerStreet;
    private RetroLocker currentLocker;
    private CardView bAddTemporary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockers_list);
        lockerNumber = getIntent().getStringExtra("lockerNumber");
        lockerZip = getIntent().getStringExtra("lockerZip");
        lockerStreet = getIntent().getStringExtra("lockerStreet");
        recyclerView = (RecyclerView) findViewById(R.id.rvLockers);
        progressBar = (ProgressBar) findViewById(R.id.progressBarLockers);
        emptyArrayMessage = (TextView) findViewById(R.id.emptyMessage);
        bAddTemporary = (CardView) findViewById(R.id.bCArdView);

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
                        makeLockersRequest();
                    }
                }
            }
        });
        bAddTemporary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LockersListActivity.this, AddLockerActivity.class);
                i.putExtra("virtualLocker", true);
                startActivity(i);
            }
        });


        adapter = new ItemsAdapter(lockersList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        makeLockersRequest();
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

    private void makeLockersRequest(){
        progressBar.setVisibility(View.VISIBLE);
        emptyArrayMessage.setVisibility(View.INVISIBLE);
        loadedPages += 1;
        Map<String, String> param = new HashMap<String, String>();
        if(lockerNumber != null)
            param.put("number", lockerNumber);
        if(lockerZip != null)
            param.put("zipCode", lockerZip);
        if(lockerStreet != null)
            param.put("streetName", lockerStreet);
        param.put("expand", "address.city.state.country,lockerXBuildingXResidents");
        new SetRequests(getApplicationContext(), LockersListActivity.this, Helper.REQUEST_MANUAL_LOCKERS, param, null);
    }

    @Override
    public void onResponse(int currentRequestId, Object result) {
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        if(result instanceof RetroLockerList){
            lockersList = ((RetroLockerList) result).getLockers();
            isLastPage = ((RetroLockerList) result).isPastPage();
            loadedPages = ((RetroLockerList) result).getCurrentPage();
            if (loadedPages == 1) adapter.setList(lockersList);
            else adapter.addToList(lockersList);
        }
    }

    @Override
    public void onFailed(int currentRequestId, boolean mustLogOut) {

    }

    private void showAlert(Context ctx, String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    class ItemsAdapter extends RecyclerView.Adapter<LockersListActivity.ItemsAdapter.ItemViewHolder>{

        private List<RetroLocker> list;
        public ItemsAdapter(List<RetroLocker> list){
            this.list = list;
        }

        public void setList(List<RetroLocker> list){
            this.list = list;
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        public void addToList(List<RetroLocker> list) {
            for (int i = 0; i < list.size(); i++) {
                this.list.add(list.get(i));
            }
            if (list.size() == 0)
                emptyArrayMessage.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }


        @Override
        public LockersListActivity.ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_locker, parent, false);
            return new LockersListActivity.ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LockersListActivity.ItemsAdapter.ItemViewHolder holder, int position) {
            holder.tvNumber.setText(list.get(position).getNumber());
            holder.tvSize.setText(list.get(position).getSize());
            holder.tvAddress.setText(list.get(position).getAddress().getFormatedAddress());
            holder.lockedIcon.setVisibility(list.get(position).isLockerFree() ? View.INVISIBLE : View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvNumber, tvSize, tvAddress;
            ImageView lockedIcon;

            public ItemViewHolder(View view){
                super(view);
                tvNumber = view.findViewById(R.id.lockerNumber);
                tvSize = view.findViewById(R.id.lockerSize);
                tvAddress = view.findViewById(R.id.lockerAddress);
                lockedIcon = view.findViewById(R.id.lockedIcon);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentLocker = list.get(getAdapterPosition());
                        if(!currentLocker.isLockerFree())
                            showAlert(LockersListActivity.this, "Locker occupied", "This locker appears in the system as not being free. So you can't choose this locker. Please choose another one.");
                        else {
                            Intent i = new Intent(LockersListActivity.this, SecurityCodeActivity.class);
                            SaveSharedPreferences.setLocker(getApplicationContext(), currentLocker);
                            startActivity(i);
                        }
                    }
                });

            }
        }
    }
}
