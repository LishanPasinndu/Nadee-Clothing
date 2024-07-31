package lk.jiat.eshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.jiat.eshop.adapter.OrderAdapter;
import lk.jiat.eshop.model.Orders;

public class PendingOrdersFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView OrderView;
    private ArrayList<Orders> orders;
    private String pid, userGmail;
    private View viewContainer;
    private TextView subTotal,Total;
    private OrderAdapter itemAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_orders, container, false);

        viewContainer = view.findViewById(R.id.viewcontainer);

        loadAnimation();

        SharedPreferences prefs = getActivity().getSharedPreferences("SIGNED_USER", getContext().MODE_PRIVATE);
        userGmail = prefs.getString("userGmail", null);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        OrderView = view.findViewById(R.id.ordersView);lishan

        linearLayoutManager = new LinearLayoutManager(view.getContext());

        loadOrderData();

        return view;
    }

    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(viewContainer, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewContainer, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(viewContainer, "alpha", 1f, 0.6f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);

        animatorSet.setDuration(700);

        final boolean[] isReverseAnimationTriggered = {false};

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isReverseAnimationTriggered[0]) {
                    animatorSet.reverse();
                    isReverseAnimationTriggered[0] = true;
                }
            }
        });

        animatorSet.start();
    }

    public void loadOrderData() {

        orders = new ArrayList<>();

        itemAdapter = new OrderAdapter(orders, PendingOrdersFragment.this);

        OrderView.setLayoutManager(linearLayoutManager);
        OrderView.setAdapter(itemAdapter);

        firestore.collection("Orders")
                .whereEqualTo("status","pending")
                .whereEqualTo("user",userGmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange change: value.getDocumentChanges()) {
                            Orders item = change.getDocument().toObject(Orders.class);
                            switch (change.getType()) {
                                case ADDED:
                                    orders.add(item);
                                    String productId = item.getPid();
                                    String user = item.getUser();

                                    firestore.collection("Product")
                                            .whereEqualTo("pid", productId)
                                            .get()
                                            .addOnSuccessListener(querySnapshot -> {
                                                if (!querySnapshot.isEmpty()) {

                                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                                                    String pname = documentSnapshot.getString("name");
                                                    String imagepath = documentSnapshot.getString("image");
                                                    Double price = documentSnapshot.getDouble("price");

                                                    item.setPname(pname);
                                                    item.setImagepath(imagepath);
                                                    item.setPrice(price);

                                                    itemAdapter.notifyDataSetChanged();
                                                }
                                            });

                                    break;
                                case REMOVED:
                                    orders.remove(item);
                                    break;
                            }
                        }

                    }
                });


        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }
    }


}