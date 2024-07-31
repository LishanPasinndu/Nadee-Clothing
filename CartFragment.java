package lk.jiat.eshop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import lk.jiat.eshop.adapter.CartAdapter;
import lk.jiat.eshop.adapter.ProductAdapter;
import lk.jiat.eshop.model.Cart;
import lk.jiat.eshop.model.Orders;
import lk.jiat.eshop.model.Product;

public class CartFragment extends Fragment {

    public final static String TAG = HomeFragment.class.getName();
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private RecyclerView cartView;
    private ArrayList<Cart> carts;
    private String pid, userGmail;
    private View viewContainer;
    private TextView subTotal,Total;
    private Double total,deliver,lastTotal,nowTotal;
    private CartAdapter itemAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Button checkOutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        viewContainer = view.findViewById(R.id.viewcontainer);

        loadAnimation();

        SharedPreferences prefs = getActivity().getSharedPreferences("SIGNED_USER", getContext().MODE_PRIVATE);
        userGmail = prefs.getString("userGmail", null);



        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        cartView = view.findViewById(R.id.cartView);

        subTotal = view.findViewById(R.id.subtotal);
        Total = view.findViewById(R.id.total);

        checkOutBtn = view.findViewById(R.id.chechoutBtn);

        linearLayoutManager = new LinearLayoutManager(view.getContext());

        loadCartData();

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (Cart cartItem : carts) {
                    addOrderToFirebase(cartItem);
                }

                carts.clear();
                itemAdapter.notifyDataSetChanged();

                subTotal.setText("LKR. 0.00");
                Total.setText("LKR. 0.00");

            }
        });

        return view;

    }

    private void addOrderToFirebase(Cart cartItem) {

        String pending = "pending";
        String orderId = generateRandomOrderId();
        String pId = cartItem.getPid();
        String quantity = cartItem.getQty();
        Double price = cartItem.getPrice();
        String date = getCurrentDate();
        String time = getCurrentTime();
        String sizes = cartItem.getSizes();


        Orders order = new Orders(userGmail,pId,sizes,quantity,price,date,time,pending,orderId);

        firestore.collection("Orders")
                .add(order)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(getContext(),"Order Added Successfull",Toast.LENGTH_LONG).show();

                    showNotification("Order Success", "Go to my Orders and check orders status");

                    deleteAllCartItems();

                })
                .addOnFailureListener(e -> {

                });
    }

    private String generateRandomOrderId() {

        String OrderId = UUID.randomUUID().toString();
        return OrderId;

    }

    private String getCurrentDate() {

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(now);

        return formattedDate;

    }

    private String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = timeFormat.format(now);

        return formattedTime;

    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "channel_id")
                .setSmallIcon(R.drawable.ic_notofication_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    private void deleteAllCartItems() {
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Cart")
                .whereEqualTo("user", userGmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = firestore.batch();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        batch.delete(document.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                })
                .addOnFailureListener(e -> {

                });
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

    public void loadCartData() {

        carts = new ArrayList<>();

        itemAdapter = new CartAdapter(carts, CartFragment.this);

        cartView.setLayoutManager(linearLayoutManager);
        cartView.setAdapter(itemAdapter);

        firestore.collection("Cart")
                .whereEqualTo("user",userGmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change: value.getDocumentChanges()) {
                    Cart item = change.getDocument().toObject(Cart.class);
                    switch (change.getType()) {
                        case ADDED:
                            carts.add(item);
                            String productId = item.getPid();

                            firestore.collection("Product")
                                    .whereEqualTo("pid", productId)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {

                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                                            String pname = documentSnapshot.getString("name");
                                            String imagepath = documentSnapshot.getString("image");
                                            Double price = documentSnapshot.getDouble("price");

                                            total = 0.0;
                                            lastTotal = 0.0;
                                            deliver = 350.0;
                                            nowTotal = 0.0;

                                            for (Cart cartItem : carts) {
                                                total += cartItem.getTotal();
                                            }

                                            String totalFormatted = String.format("LKR %.2f", total);

                                            nowTotal = total+deliver;
                                            String totalFormatted2 = String.format("LKR %.2f", nowTotal);

                                            subTotal.setText(totalFormatted);
                                            Total.setText(totalFormatted2);

                                            item.setPname(pname);
                                            item.setImagepath(imagepath);
                                            item.setPrice(price);

                                            itemAdapter.notifyDataSetChanged();
                                        }
                                    });
                            break;
                        case REMOVED:
                            carts.remove(item);
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