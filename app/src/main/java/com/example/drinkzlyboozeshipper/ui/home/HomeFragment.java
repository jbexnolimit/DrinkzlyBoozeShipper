package com.example.drinkzlyboozeshipper.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drinkzlyboozeshipper.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import adapter.MyShippingOrderAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.Common;
import model.eventbus.UpdateShippingOrderEvent;

public class HomeFragment extends Fragment {

    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;

    Unbinder unbinder;
    LayoutAnimationController layoutAnimationController;
    MyShippingOrderAdapter adapter;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        homeViewModel.getMessageError().observe(getViewLifecycleOwner(),s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        });
      homeViewModel.getShippingOrderMutableData(Common.currentShipperUser.getPhone()).observe(getViewLifecycleOwner(), shippingOrderModelList -> {

            adapter = new MyShippingOrderAdapter(getContext(),shippingOrderModelList);
            recycler_order.setAdapter(adapter);
            recycler_order.setLayoutAnimation(layoutAnimationController);
      });
        return root;
    }

    private void initView(View root) {
        unbinder = ButterKnife.bind(this,root);

        recycler_order.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_order.setLayoutManager(layoutManager);
        recycler_order.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateShippingOrderEvent.class))
            EventBus.getDefault().removeStickyEvent(UpdateShippingOrderEvent.class);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onUpdateShippingOrder(UpdateShippingOrderEvent event)
    {
        homeViewModel.getShippingOrderMutableData(Common.currentShipperUser.getPhone());
    }
}