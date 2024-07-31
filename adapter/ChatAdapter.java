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

import lk.jiat.eshop.ChatFragment;
import lk.jiat.eshop.OrdersFragment;
import lk.jiat.eshop.R;
import lk.jiat.eshop.SingleProductViewActivity;
import lk.jiat.eshop.model.Message;
import lk.jiat.eshop.model.Orders;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private FirebaseFirestore firestore;
    private ArrayList<Message> items;
    private FirebaseStorage storage;
    private ChatFragment context;
    private ChatAdapter orderAdapter;

    public ChatAdapter(ArrayList<Message> items, ChatFragment context) {
        this.items = items;
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        Message item = items.get(position);

        holder.textName.setText(item.getMessage());
        holder.date.setText(item.getDate().toString());
        holder.time.setText(item.getTime().toString());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textPrice,unitPrice, textQty, date, time;
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

        }
    }

}

