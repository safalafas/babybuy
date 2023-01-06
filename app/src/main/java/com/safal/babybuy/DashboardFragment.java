package com.safal.babybuy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.safal.babybuy.databinding.FragmentDashboardBinding;

import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DashboardFragment extends Fragment {
    //to use the data binding to make app smoother
    FragmentDashboardBinding binding;
    //initialize item view model
    ItemViewModel model;
    //initialize recycler view to load data
    RecyclerView recyclerView;
    //initialize adapter for the recycler view
    DataAdapter adapter;
    //to sense the shake motion
    SensorManager mSensorManager;
    //parameters to sense shaking
    float mAccel;
    float mAccelCurrent;
    float mAccelLast;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //assigning the model to pass data from/to database
        model = new ViewModelProvider(this).get(ItemViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialize views and filters
        initRecyclerView(view);
        initSearchAndFilter(view);
        //initialize shake sensors
        initShake();
        //when user clicks on add item floating button
        binding.fabAddItems.setOnClickListener(v -> {
            //send to add item fragment
            Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_addItemsFragment);
        });

    }

    //initialize recycler view
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rvwItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DataAdapter(getContext(), model);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initSearchAndFilter(View view) {
        SearchView searchItem = view.findViewById(R.id.searchItems);
        Chip purchasedChip = view.findViewById(R.id.chpPurchased);
        Chip notPurchasedChip = view.findViewById(R.id.chpNotPurchased);
        Chip sortChip = view.findViewById(R.id.chpSort);

        purchasedChip.setChipBackgroundColor(ContextCompat.getColorStateList(getContext(), R.color.chip_colors));
        notPurchasedChip.setChipBackgroundColor(ContextCompat.getColorStateList(getContext(), R.color.chip_colors));
        sortChip.setChipBackgroundColor(ContextCompat.getColorStateList(getContext(), R.color.chip_colors));

        purchasedChip.setOnClickListener(v -> {
            adapter.purchased(true);
        });
        notPurchasedChip.setOnClickListener(v -> {
            adapter.purchased(false);
        });

        sortChip.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.inflate(R.menu.sort_options);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_sort_date:
                        adapter.sort("Date");
                        return true;
                    case R.id.menu_sort_alphabetically:
                        adapter.sort("Alphabetically");
                        return true;
                }
                return false;
            });
            popupMenu.show();
        });
        searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.search(newText);
                return true;
            }
        });
    }

    public class TouchHelper extends ItemTouchHelper.SimpleCallback {

        public TouchHelper(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            //Not needed but have to override
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_200))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.green_200))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_edit)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            List<Item> allItems = model.getItemList();
            // handle the swipe event
            if (direction==ItemTouchHelper.RIGHT) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("item", allItems.get(position));
                Navigation.findNavController(viewHolder.itemView).navigate(R.id.action_dashboardFragment_to_addItemsFragment, bundle);
            }
            else if (direction==ItemTouchHelper.LEFT){
                adapter.deleteItem(position);
                adapter.notifyDataSetChanged();
            }
        }

    }
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                adapter.deleteAllData();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not needed but have to override
        }


    };

    private void initShake() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    TouchHelper callback =  new TouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

}