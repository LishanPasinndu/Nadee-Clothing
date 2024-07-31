package lk.jiat.eshop.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.jiat.eshop.CompletedOrdersFragment;
import lk.jiat.eshop.PendingOrdersFragment;
import lk.jiat.eshop.R;
import lk.jiat.eshop.SingleProductViewActivity;
import lk.jiat.eshop.model.Orders;

public class OrderAdapter2 extends RecyclerView.Adapter<OrderAdapter2.ViewHolder> {

    private FirebaseFirestore firestore;
    private ArrayList<Orders> items;
    private FirebaseStorage storage;
    private CompletedOrdersFragment context;
    private OrderAdapter2 orderAdapter;

    public OrderAdapter2(ArrayList<Orders> items, CompletedOrdersFragment context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public OrderAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_complete_orders,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter2.ViewHolder holder, int position) {

        Orders item = items.get(position);

        storage.getReference("product-images/"+item.getImagepath())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .fit()
                                .centerCrop()
                                .into(holder.image);
                    }
                });

        holder.delete.setTag(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();

                firestore = FirebaseFirestore.getInstance();

                if (position >= 0 && position < items.size()) {

                    showDeleteConfirmationDialog(position);

                }
            }
        });


        holder.textName.setText(item.getPname());
        holder.textPrice.setText("LKR."+String.valueOf(item.getTotal())+"0");
        holder.unitPrice.setText("LKR."+String.valueOf(item.getPrice())+"0");
        holder.textQty.setText(item.getQty());
        holder.date.setText(item.getDate().toString());
        holder.time.setText(item.getTime().toString());
        holder.id.setText(item.getOrderId().toString());


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Orders item = items.get(position);

                Intent intent = new Intent(context.getContext(), SingleProductViewActivity.class);
                intent.putExtra("productId", item.getPid());
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textPrice,unitPrice, textQty, date, time, id;
        ImageView image;
        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.cartPname);
            textPrice = itemView.findViewById(R.id.cartPsubTotal);
            unitPrice = itemView.findViewById(R.id.cartPprice);
            image = itemView.findViewById(R.id.cartImage);
            textQty = itemView.findViewById(R.id.cartPquantity);
            delete = itemView.findViewById(R.id.delete);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            id = itemView.findViewById(R.id.orderId);

        }
    }
    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this order from your Order History?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCartItem(position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void deleteCartItem(int position) {
        firestore = FirebaseFirestore.getInstance();

        if (position >= 0 && position < items.size()) {
            Orders selectedCart = items.get(position);
            String oid = selectedCart.getOrderId();

            firestore.collection("Orders")
                    .whereEqualTo("orderId", oid)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            document.getReference().delete();

                            if (context != null && context instanceof CompletedOrdersFragment) {
                                ((CompletedOrdersFragment) context).loadOrderData();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {

                    });
        }
    }

}

