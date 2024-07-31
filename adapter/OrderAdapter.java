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

import lk.jiat.eshop.CartFragment;
import lk.jiat.eshop.OrdersFragment;
import lk.jiat.eshop.PendingOrdersFragment;
import lk.jiat.eshop.R;
import lk.jiat.eshop.SingleProductViewActivity;
import lk.jiat.eshop.model.Cart;
import lk.jiat.eshop.model.Orders;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private FirebaseFirestore firestore;
    private ArrayList<Orders> items;
    private FirebaseStorage storage;
    private PendingOrdersFragment context;
    private OrderAdapter orderAdapter;

    public OrderAdapter(ArrayList<Orders> items, PendingOrdersFragment context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_pending_orders,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {

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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.cartPname);
            textPrice = itemView.findViewById(R.id.cartPsubTotal);
            unitPrice = itemView.findViewById(R.id.cartPprice);
            image = itemView.findViewById(R.id.cartImage);
            textQty = itemView.findViewById(R.id.cartPquantity);

            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            id = itemView.findViewById(R.id.orderId);

        }
    }


}

