package lk.jiat.eshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.jiat.eshop.adapter.CartAdapter;
import lk.jiat.eshop.adapter.OrderAdapter;
import lk.jiat.eshop.model.Cart;
import lk.jiat.eshop.model.Orders;
import lk.jiat.eshop.model.Product;

public class OrdersFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView OrderView;
    private ArrayList<Orders> orders;
    private String pid, userGmail;
    private View viewContainer;
    private TextView subTotal,Total;
    private OrderAdapter itemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Button pendingBtn, CompleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        pendingBtn = view.findViewById(R.id.pending);
        CompleteBtn = view.findViewById(R.id.completed);

        viewContainer = view.findViewById(R.id.viewcontainer);

        SharedPreferences prefs = getActivity().getSharedPreferences("SIGNED_USER", getContext().MODE_PRIVATE);
        userGmail = prefs.getString("userGmail", null);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        loadFragment(new PendingOrdersFragment());

        linearLayoutManager = new LinearLayoutManager(view.getContext());

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new PendingOrdersFragment());
            }
        });

        CompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new CompletedOrdersFragment());
            }
        });

        return view;

    }

    public void loadFragment(Fragment fragment){

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



}