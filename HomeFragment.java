package lk.jiat.eshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import lk.jiat.eshop.adapter.ProductAdapter;
import lk.jiat.eshop.model.Product;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SnowfallView snowfallView;
    public final static String TAG = HomeFragment.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private ArrayList<Product> products;
    private View homeContainer;
    private RecyclerView itemview;
    private String pid;
    private EditText textSearchInput;
    private ImageView banner;
    private LinearLayout l1,l2,l3,l4;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeContainer = view.findViewById(R.id.homecontainer);

        loadAnimation();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        itemview = view.findViewById(R.id.itemView);

        textSearchInput = view.findViewById(R.id.messageInput);
        banner = view.findViewById(R.id.imageView3);
        l1 = view.findViewById(R.id.linear);
        l2 = view.findViewById(R.id.linear1);
        l3 = view.findViewById(R.id.linear3);
        l4 = view.findViewById(R.id.linear4);

        products = new ArrayList<>();
        productAdapter = new ProductAdapter(products, HomeFragment.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(),3);
        itemview.setLayoutManager(gridLayoutManager);
        itemview.setAdapter(productAdapter);

        loadAllProducts();


        textSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadAllProducts();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().isEmpty()) {

                    productAdapter.clear();
                    productAdapter.loadAllProducts(products);

                    banner.setVisibility(View.VISIBLE);
                    l1.setVisibility(View.VISIBLE);
                    l2.setVisibility(View.VISIBLE);
                    l3.setVisibility(View.VISIBLE);
                    l4.setVisibility(View.VISIBLE);

                } else {

                    productAdapter.filter(charSequence.toString());

                    banner.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                    l4.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
               loadAllProducts();
            }
        });

        snowfallView = view.findViewById(R.id.snowfallView);
        snowfallView.setVisibility(View.VISIBLE);

        return view;
    }


    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(homeContainer, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(homeContainer, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(homeContainer, "alpha", 1f, 0.6f);

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

    private void loadAllProducts() {

        firestore.collection("Product").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                products.clear();

                for (DocumentChange change : value.getDocumentChanges()) {
                    Product item = change.getDocument().toObject(Product.class);
                    switch (change.getType()) {
                        case ADDED:
                            products.add(item);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }

                productAdapter.notifyDataSetChanged();
            }
        });
    }



}
