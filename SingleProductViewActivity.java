package lk.jiat.eshop;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.util.HashMap;
import java.util.List;

import lk.jiat.eshop.model.Cart;
import lk.jiat.eshop.model.Orders;
import lk.jiat.eshop.model.WishList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class SingleProductViewActivity extends AppCompatActivity {

    private TextView singleName, singlePrice, singleDesc, singleWeight, singleSizes,productSizesTextView;
    private ImageView singleImage;
    private String productID, pname,pdesc,pweight,imagePath, imagename, uEmail;
    private Double pprice , total;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private View singleContainer;
    private ImageButton backBtn, Homebtn, CartBtn, WishBtn;
    private Button AddtoCartBtn,buyNowBtn;
    private Object sizes;
    private Long qty;
    private Stripe stripe;
    private String cardNumber = "";
    private int expMonth = 0;
    private int expYear = 0;
    private String cvc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        productID = getIntent().getStringExtra("productId");

        singleContainer = findViewById(R.id.SingleView);

         stripe = new Stripe(SingleProductViewActivity.this, "your_publishable_key");

        singleImage = findViewById(R.id.showImage);

        SharedPreferences prefs = getSharedPreferences("SIGNED_USER", MODE_PRIVATE);
        uEmail = prefs.getString("userGmail", null);

        singleName = findViewById(R.id.showname);
        singleDesc = findViewById(R.id.showDesc);
        singlePrice = findViewById(R.id.showPrice);
        singleWeight = findViewById(R.id.showWeight);
        singleSizes = findViewById(R.id.showSize);

        AddtoCartBtn = findViewById(R.id.addToCartBtn);
        buyNowBtn = findViewById(R.id.buy);

        if (productID != null) {
            searchByProduct(productID);
        }

        AddtoCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uEmail != null){
                    showAddToCartDialog();
                }else {
                    Toast.makeText(SingleProductViewActivity.this, "Please Login First", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), LogInActivity.class);
                    startActivity(intent);
                }
            }
        });

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uEmail != null){
                    showAddToBuyDialog();
                }else {
                    Toast.makeText(SingleProductViewActivity.this, "Please Login First", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), LogInActivity.class);
                    startActivity(intent);
                }
            }
        });

        backBtn = findViewById(R.id.backBtn);
        Homebtn = findViewById(R.id.Homebtn);
        CartBtn = findViewById(R.id.CartBtn);
        WishBtn = findViewById(R.id.WishlistBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("key", "LoadCart");
                view.getContext().startActivity(intent);

            }
        });

        WishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("key", "LoadWish");
                view.getContext().startActivity(intent);

            }
        });

    }

    public void searchByProduct(String pid){

        firestore.collection("Product")
                .whereEqualTo("pid", pid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {


                            pname = documentSnapshot.getString("name");
                            pprice = documentSnapshot.getDouble("price");
                            pdesc = documentSnapshot.getString("desc");
                            pweight = documentSnapshot.getString("weight");

                            qty = documentSnapshot.getLong("qty");

                            sizes = documentSnapshot.get("sizes");

                            if (sizes instanceof List<?>) {
                                generateSizeTextViews((List<?>) sizes);
                            }

                            imagename = documentSnapshot.getString("image");

                            loadImage(imagename);

                            singleName.setText(pname);
                            singleDesc.setText(pdesc);
                            singlePrice.setText("LKR "+String.valueOf(pprice)+"0");
                            singleWeight.setText(pweight);


                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void generateSizeTextViews(List<?> sizes) {
        if (sizes != null) {

            LinearLayout parentLayout = findViewById(R.id.sizelinear);

            // Clear existing views in the layout
            parentLayout.removeAllViews();

            for (Object sizeObject : sizes) {
                if (sizeObject instanceof String) {
                    String size = (String) sizeObject;

                    TextView textView = new TextView(this);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics())
                    ));

                    textView.setText("  "+size+"  ");
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    ));
                    textView.setBackgroundResource(R.drawable.size);
                    textView.setTextColor(Color.parseColor("#7E7E7E"));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setGravity(Gravity.CENTER);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMarginEnd(8);
                    textView.setLayoutParams(layoutParams);

                    parentLayout.addView(textView);
                }
            }
        }
    }



    private void loadImage(String imageName) {

        loadAnimation();

        imagePath = "product-images/" + imageName;

        storage.getReference(imagePath)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Picasso.get()
                            .load(uri)
                            .fit()
                            .centerCrop()
                            .into(singleImage);

                })
                .addOnFailureListener(e -> {

                });


    }

    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(singleContainer, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(singleContainer, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(singleContainer, "alpha", 1f, 0.6f);

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

    private void showAddToCartDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_to_cart, null);
        builder.setView(dialogView);


        ImageView productImageView = dialogView.findViewById(R.id.dialogProductImage);
        TextView productNameTextView = dialogView.findViewById(R.id.dialogProductName);
        TextView productPriceTextView = dialogView.findViewById(R.id.dialogProductPrice);
        TextView quantityEditText = dialogView.findViewById(R.id.cartPquantity);

        ImageButton addBtn = dialogView.findViewById(R.id.AddBtn);
        ImageButton subBtn = dialogView.findViewById(R.id.SubBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView qty = dialogView.findViewById(R.id.cartPquantity);
                String nowQty = qty.getText().toString();

                Double newQty = Double.parseDouble(nowQty) + 1;
                double doubleValue = newQty;
                String formattedValue = String.format("%.0f", doubleValue);
                qty.setText("  "+formattedValue+"  ");

            }
        });

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView qty = dialogView.findViewById(R.id.cartPquantity);
                String nowQty = qty.getText().toString();

                Double nowQtyD = Double.parseDouble(nowQty);

                if (nowQtyD == 1){
                    Toast.makeText(SingleProductViewActivity.this, "Minimum Quantity is 1", Toast.LENGTH_SHORT).show();
                } else if (nowQtyD > 1) {

                    Double newQty = Double.parseDouble(nowQty) - 1;
                    double doubleValue = newQty;
                    String formattedValue = String.format("%.0f", doubleValue);
                    qty.setText("  "+formattedValue+"  ");
                }

            }
        });

        productNameTextView.setText(pname);
        productPriceTextView.setText(String.valueOf("LKR " + pprice + "0"));

        LinearLayout parentLayout = dialogView.findViewById(R.id.sizelinear2);

        parentLayout.removeAllViews();

        if (sizes instanceof List<?>) {
            List<?> sizesList = (List<?>) sizes;

            if (!sizesList.isEmpty() && sizesList.get(0) instanceof String) {
                List<String> stringSizesList = (List<String>) sizesList;

                for (Object sizeObject : stringSizesList) {
                    if (sizeObject instanceof String) {
                        String size = (String) sizeObject;

                        TextView textView = new TextView(this);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics())
                        ));

                        textView.setText("  "+size+"  ");
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        ));
                        textView.setBackgroundResource(R.drawable.size);
                        textView.setTextColor(Color.parseColor("#7E7E7E"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMarginEnd(8);
                        textView.setLayoutParams(layoutParams);

                        parentLayout.addView(textView);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int myHexColor = Color.parseColor("#063970");

                                textView.setBackgroundColor(myHexColor);

                            }
                        });


                    }
                }

            }
        }

        loadImageIntoImageView(productImageView,imagename);

        builder.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantity = quantityEditText.getText().toString();


                for (int i = 0; i < parentLayout.getChildCount(); i++) {
                    View childView = parentLayout.getChildAt(i);
                    if (childView instanceof TextView) {
                        TextView textView = (TextView) childView;

                        Drawable backgroundDrawable = textView.getBackground();

                        if (backgroundDrawable instanceof ColorDrawable) {

                            int backgroundColor = ((ColorDrawable) backgroundDrawable).getColor();
                            if (backgroundColor == Color.parseColor("#063970")) {

                                String selectedSize = textView.getText().toString();

                                Double onePrice = pprice;
                                Double selectedQty = Double.parseDouble(quantity);

                                Double totalDouble = onePrice * selectedQty;

                                String addToCartUserEmail = uEmail;
                                String addToCartProductId = productID;
                                String addToCartQty = quantity;
                                String addToCartSize = selectedSize;
                                Double addToCartTotal = totalDouble;

//                                Toast.makeText(SingleProductViewActivity.this, addToCartUserEmail+" "+addToCartProductId+" "+addToCartQty+" "+addToCartSize+" "+String.valueOf(addToCartTotal), Toast.LENGTH_LONG).show();


                                Cart cart = new Cart(addToCartUserEmail,addToCartProductId,addToCartSize,addToCartQty,addToCartTotal);

                                firestore.collection("Cart").add(cart)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Added cart Successfull",Toast.LENGTH_LONG).show();
                                                showNotification("Product Added to Cart", "Go to Cart & show your saved products");

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }else {
                                Toast.makeText(getApplicationContext(),"Please select size you want!",Toast.LENGTH_LONG).show();
                            }

                        }

                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAddToBuyDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_add_to_buy, null);
        builder.setView(dialogView);

        ImageView productImageView = dialogView.findViewById(R.id.dialogProductImage);
        TextView productNameTextView = dialogView.findViewById(R.id.dialogProductName);
        TextView productPriceTextView = dialogView.findViewById(R.id.dialogProductPrice);
        TextView quantityEditText = dialogView.findViewById(R.id.cartPquantity);

        LinearLayout linear = dialogView.findViewById(R.id.linear);
        LinearLayout Mainlinear = dialogView.findViewById(R.id.mainLinear);

        ImageButton addBtn = dialogView.findViewById(R.id.AddBtn);
        ImageButton subBtn = dialogView.findViewById(R.id.SubBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView qty = dialogView.findViewById(R.id.cartPquantity);
                String nowQty = qty.getText().toString();

                Double newQty = Double.parseDouble(nowQty) + 1;
                double doubleValue = newQty;
                String formattedValue = String.format("%.0f", doubleValue);
                qty.setText("  "+formattedValue+"  ");

            }
        });

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView qty = dialogView.findViewById(R.id.cartPquantity);
                String nowQty = qty.getText().toString();

                Double nowQtyD = Double.parseDouble(nowQty);

                if (nowQtyD == 1){
                    Toast.makeText(SingleProductViewActivity.this, "Minimum Quantity is 1", Toast.LENGTH_SHORT).show();
                } else if (nowQtyD > 1) {

                    Double newQty = Double.parseDouble(nowQty) - 1;
                    double doubleValue = newQty;
                    String formattedValue = String.format("%.0f", doubleValue);
                    qty.setText("  "+formattedValue+"  ");
                }

            }
        });

        productNameTextView.setText(pname);
        productPriceTextView.setText(String.valueOf("LKR " + pprice + "0"));

        LinearLayout parentLayout = dialogView.findViewById(R.id.sizelinear2);

        parentLayout.removeAllViews();

        if (sizes instanceof List<?>) {
            List<?> sizesList = (List<?>) sizes;

            if (!sizesList.isEmpty() && sizesList.get(0) instanceof String) {
                List<String> stringSizesList = (List<String>) sizesList;

                for (Object sizeObject : stringSizesList) {
                    if (sizeObject instanceof String) {
                        String size = (String) sizeObject;

                        TextView textView = new TextView(this);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics())
                        ));

                        textView.setText("  "+size+"  ");
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        ));
                        textView.setBackgroundResource(R.drawable.size);
                        textView.setTextColor(Color.parseColor("#7E7E7E"));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMarginEnd(8);
                        textView.setLayoutParams(layoutParams);

                        parentLayout.addView(textView);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int myHexColor = Color.parseColor("#063970");

                                textView.setBackgroundColor(myHexColor);

                            }
                        });


                    }
                }

            }
        }

        loadImageIntoImageView(productImageView,imagename);

        if (qty <= 0){

            int myHexColor = Color.parseColor("#fcd69f");

            linear.setVisibility(View.GONE);
            Mainlinear.setBackgroundColor(myHexColor);

            builder.setPositiveButton("Add To Wishlist", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    for (int i = 0; i < parentLayout.getChildCount(); i++) {
                        View childView = parentLayout.getChildAt(i);
                        if (childView instanceof TextView) {
                            TextView textView = (TextView) childView;

                            Drawable backgroundDrawable = textView.getBackground();

                            if (backgroundDrawable instanceof ColorDrawable) {

                                int backgroundColor = ((ColorDrawable) backgroundDrawable).getColor();
                                if (backgroundColor == Color.parseColor("#063970")) {

                                    String selectedSize = textView.getText().toString();

                                    String addToCartUserEmail = uEmail;
                                    String addToCartProductId = productID;

                                    String addToCartSize = selectedSize;

                                    Calendar calendar = Calendar.getInstance();
                                    Date now = calendar.getTime();

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String formattedDate = dateFormat.format(now);

                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                    String formattedTime = timeFormat.format(now);

                                    WishList wishList = new WishList(addToCartUserEmail,addToCartProductId,addToCartSize,formattedDate,formattedTime);

                                    firestore.collection("Wishlist").add(wishList)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    dialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"WishList Added Successfull",Toast.LENGTH_LONG).show();
                                                    showNotification("Product added to wishlist", "Go to Wishlist and check product");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });

                                }else {
                                    Toast.makeText(getApplicationContext(),"Please select size you want!",Toast.LENGTH_LONG).show();
                                }

                            }

                        }
                    }

                }
            });

        }else {
            builder.setPositiveButton("Buy Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String quantity = quantityEditText.getText().toString();

                    Double qqty = Double.parseDouble(quantity);
                    total = qqty * pprice;

                    for (int i = 0; i < parentLayout.getChildCount(); i++) {
                        View childView = parentLayout.getChildAt(i);
                        if (childView instanceof TextView) {
                            TextView textView = (TextView) childView;
                            Drawable backgroundDrawable = textView.getBackground();

                            if (backgroundDrawable instanceof ColorDrawable) {

                                int backgroundColor = ((ColorDrawable) backgroundDrawable).getColor();
                                if (backgroundColor == Color.parseColor("#063970")) {

                                    View dialogView = getLayoutInflater().inflate(R.layout.payment_dialog_layout, null);

                                    ImageView pImage = dialogView.findViewById(R.id.pImage);
                                    TextView pPname = dialogView.findViewById(R.id.pName);
                                    TextView pPrice = dialogView.findViewById(R.id.pPrice);

                                    pPname.setText(pname);
                                    pPrice.setText(String.valueOf("LKR " + total + "0"));
                                    loadImageIntoImageView(pImage,imagename);

                                    // Find the EditText views in the layout
                                    EditText cardNumberEditText = dialogView.findViewById(R.id.editTextCardNumber);
                                    EditText expMonthEditText = dialogView.findViewById(R.id.editTextExpMonth);
                                    EditText expYearEditText = dialogView.findViewById(R.id.editTextExpYear);
                                    EditText cvcEditText = dialogView.findViewById(R.id.editTextCvc);

                                    // Set input type for card details
                                    cardNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    expMonthEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    expYearEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    cvcEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                                    // Create an AlertDialog to collect payment information
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SingleProductViewActivity.this);
                                    builder.setTitle("Enter Card Details");
                                    builder.setView(dialogView);

                                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Get user input from EditTexts
                                            String cardNumber = cardNumberEditText.getText().toString();
                                            int expMonth = Integer.parseInt(expMonthEditText.getText().toString());
                                            int expYear = Integer.parseInt(expYearEditText.getText().toString());
                                            String cvc = cvcEditText.getText().toString();



                                            String selectedSize = textView.getText().toString();

                                            Double onePrice = pprice;
                                            Double selectedQty = Double.parseDouble(quantity);

                                            Double totalDouble = onePrice * selectedQty;
                                            Double newTotalDouble = totalDouble + 350.0 ;

                                            String addToCartUserEmail = uEmail;
                                            String addToCartProductId = productID;
                                            String addToCartQty = quantity;
                                            String addToCartSize = selectedSize;
                                            Double addToCartTotal = newTotalDouble;

                                            Calendar calendar = Calendar.getInstance();
                                            Date now = calendar.getTime();

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            String formattedDate = dateFormat.format(now);

                                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                                            String formattedTime = timeFormat.format(now);

                                            String pending = "pending";
                                            String OrderId = UUID.randomUUID().toString();

                                            Orders orders = new Orders(addToCartUserEmail,addToCartProductId,addToCartSize,addToCartQty,addToCartTotal,formattedDate,formattedTime,pending,OrderId);

                                            firestore.collection("Orders").add(orders)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            //////////update qty

                                                            firestore.collection("Product")
                                                                    .whereEqualTo("pid", addToCartProductId)
                                                                    .get()
                                                                    .addOnSuccessListener(querySnapshot -> {
                                                                        if (!querySnapshot.isEmpty()) {
                                                                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                                                            Long oldQty = documentSnapshot.getLong("qty");

                                                                            if (oldQty != null) {
                                                                                // Assuming addToCartQty is a String representing the quantity to subtract
                                                                                Double buyQty = Double.parseDouble(addToCartQty);
                                                                                Double newQty = (oldQty - buyQty);

                                                                                Map<String, Object> updateData = new HashMap<>();
                                                                                updateData.put("qty", newQty);

                                                                                firestore.collection("Product")
                                                                                        .document(documentSnapshot.getId())
                                                                                        .update(updateData)
                                                                                        .addOnSuccessListener(aVoid -> {
                                                                                        })
                                                                                        .addOnFailureListener(e -> {
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

                                                            ///////////////

                                                            dialog.dismiss();
                                                            Toast.makeText(getApplicationContext(),"Order Added Successfull",Toast.LENGTH_LONG).show();

                                                            showNotification("Order Success", "Go to my Orders and check orders status");

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                            // Dismiss the dialog
                                            dialog.dismiss();
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User canceled, do something if needed
                                            dialog.dismiss();
                                        }
                                    });

                                    // Show the dialog
                                    builder.show();


                                }else {
                                    Toast.makeText(getApplicationContext(),"Please select size you want!",Toast.LENGTH_SHORT).show();
                                }

                            }

                        }
                    }

                }
            });
        }

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void loadImageIntoImageView(ImageView imageView, String imageName) {

        imagePath = "product-images/" + imageName;

        storage.getReference(imagePath)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {

                    Picasso.get()
                            .load(uri)
                            .fit()
                            .centerCrop()
                            .into(imageView);


                })
                .addOnFailureListener(e -> {

                });

    }

    private void loadPayment() {
        // Inflate the layout

    }


    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager)
                SingleProductViewActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(SingleProductViewActivity.this, "channel_id")
                .setSmallIcon(R.drawable.ic_notofication_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

}