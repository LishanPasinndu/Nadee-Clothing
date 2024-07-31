package lk.jiat.eshop.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.jiat.eshop.HomeFragment;
import lk.jiat.eshop.R;
import lk.jiat.eshop.SingleProductViewActivity;
import lk.jiat.eshop.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {


    private ArrayList<Product> items;
    private ArrayList<Product> filteredProducts;
    private FirebaseStorage storage;
    private HomeFragment context;
    public ProductAdapter(ArrayList<Product> items, HomeFragment context) {
        this.items = items;
        this.context = context;
        this.filteredProducts = new ArrayList<>(items);
        this.storage = FirebaseStorage.getInstance();

    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_item_row,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {

        Product item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textPrice.setText("LKR."+String.valueOf(item.getPrice())+"0");

        storage.getReference("product-images/"+item.getImage())
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

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Product item = items.get(position);

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

        String pid;
        TextView textName, textDesc, textPrice;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textItemName);
            textPrice = itemView.findViewById(R.id.textItemPrice);
            image = itemView.findViewById(R.id.cartImage);

        }
    }

    public void filter(String text) {

        ArrayList<Product> filteredList = new ArrayList<>();

        if (text.isEmpty()) {

        }else {

            for (Product product : items) {
                if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(product);
                }
            }
            setFilteredList(filteredList);

        }
    }

    public void setFilteredList(ArrayList<Product> filteredList) {
        items = filteredList;
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void loadAllProducts(ArrayList<Product> productList) {
        items.addAll(productList);
        notifyDataSetChanged();
    }




}

