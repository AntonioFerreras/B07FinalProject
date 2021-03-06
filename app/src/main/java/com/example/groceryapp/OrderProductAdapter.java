package com.example.groceryapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.MyViewHolder> {
    private ArrayList<HashMap<String,Integer>> productList;
    private String order_id;
    private String current_user_id;
    private String order_store_id;
    private String product_id;
    private int total;
    private TextView display_total;
    private Button sent_order;
    private HashMap<Integer,String> position_to_product_id;
    public OrderProductAdapter(ArrayList<HashMap<String, Integer>> productList, String order_id, TextView display_total, Button sent_order){
        this.order_id = order_id;
        this.productList = productList;
        this.display_total = display_total;
        this.sent_order = sent_order;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView product_name, product_price,product_brand;
        // private ImageView order_Comfirm_btn;
        final EditText edit_quantity;
        public MyViewHolder(final View view){
            super(view);
            product_name = view.findViewById(R.id.oproduct_name);
            product_brand = view.findViewById(R.id.oproduct_brand);
            product_price = view.findViewById(R.id.oproduct_price);
            edit_quantity = view.findViewById(R.id.oproduct_quantity);
            position_to_product_id = new HashMap<Integer,String>();
            //display_total = view.findViewById(R.id.edit_Total);
//            //define image action btn
            //order_Comfirm_btn = view.findViewById(R.id.order_Comfirm);
            //display_total.setText(DBConstants.getPriceAsString(total));
        }
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View Order_product_edit_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_order_product,parent,false);
        return new MyViewHolder(Order_product_edit_view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderProductAdapter.MyViewHolder holder, int position) {
        total = 0;
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).child("order_store_id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    order_store_id = task.getResult().getValue(String.class);
                    //
                    update_total(ref,holder,true);

                }

            }
        });


        sent_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).child(DBConstants.ORDER_PROCESS).setValue(true);
                ref.getReference(DBConstants.STORES_PATH).child(order_store_id).child(DBConstants.STORE_ORDERS).child(order_id).setValue(current_user_id);
                //wanna start activity here to refresh the page.
                v.getContext().startActivity(new Intent(v.getContext(), CartActivity.class));
                Toast.makeText(v.getContext(), "Order has been sent!", Toast.LENGTH_SHORT).show();
            }
        });
        //user update quantity:
        holder.edit_quantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String quantity_num=holder.edit_quantity.getText().toString();
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    if(Integer.parseInt(quantity_num)>=1){
                        ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).child("items_ids").child(position_to_product_id.get(holder.getAdapterPosition())).setValue(Integer.parseInt(quantity_num));

                        Toast.makeText(v.getContext(), "product quantity updated!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).child("items_ids").child(position_to_product_id.get(holder.getAdapterPosition())).removeValue();
                        Toast.makeText(v.getContext(), "product DELETED!", Toast.LENGTH_SHORT).show();
                    }
                    v.getContext().startActivity(new Intent(v.getContext(), CartActivity.class));




                    //update_total_2(ref,holder,false);
                    //notifyItemChanged(holder.getAdapterPosition());
                    ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.child("items_ids").exists()){
                                ref.getReference(DBConstants.USERS_PATH).child(current_user_id).child(DBConstants.USER_ORDERS).child(order_id).removeValue();
                                Toast.makeText(v.getContext(), "Order Deleted!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                return false;
            }
        });

    }

    private void update_total(FirebaseDatabase ref,OrderProductAdapter.MyViewHolder holder, boolean intialize_field) {
        //

        ref.getReference(DBConstants.STORES_PATH).child(order_store_id).child(DBConstants.STORE_PRODUCTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot each_product : snapshot.getChildren()) {
                    Product each_pro = each_product.getValue(Product.class);
                    each_pro.setId(each_product.getKey());
                    //Log.i("demo","Adapter Position: "+holder.getAdapterPosition()+ " Product name is: "+ each_pro.getName());
                    //Log.i("demo","hey "+each_pro.getId());
                    for (HashMap.Entry<String, Integer> entry : productList.get(holder.getAdapterPosition()).entrySet()) {
                       // Log.i("demo", "my holder position is: " + holder.getAdapterPosition());
                        if (entry.getKey().equals(each_pro.getId())) {
                            position_to_product_id.put(holder.getAdapterPosition(), each_pro.getId());
                            holder.product_name.setText(each_pro.getName());
                            holder.product_brand.setText(each_pro.getBrand());
                            holder.product_price.setText(each_pro.getPriceAsString(true));
                            //Log.i("demo", "my current product is: " + each_pro.getName());
                            if(intialize_field){
                                holder.edit_quantity.setText(entry.getValue().toString());
                            }
                            total += entry.getValue() * each_pro.getPrice();
                           // Log.i("demo", "my current total is: " + DBConstants.getPriceAsString(total));
                        }
                        //entry.getValue());
                    }
                }
                display_total.setText("Total: " + DBConstants.getPriceAsString(total));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public int getItemCount() {
        return productList.size();
    }
}

//                    ref.getReference(DBConstants.STORES_PATH).child(order_store_id).child(DBConstants.STORE_PRODUCTS).addListenerForSingleValueEvent(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot snapshot) {
//        for (DataSnapshot each_product : snapshot.getChildren()) {
//        Product each_pro = each_product.getValue(Product.class);
//        each_pro.setId(each_product.getKey());
//
//        //Log.i("demo","Adapter Position: "+holder.getAdapterPosition()+ " Product name is: "+ each_pro.getName());
//        //Log.i("demo","hey "+each_pro.getId());
//        for (HashMap.Entry<String, Integer> entry : productList.get(holder.getAdapterPosition()).entrySet()) {
//        if (entry.getKey().equals(each_pro.getId())) {
//        position_to_product_id.put(holder.getAdapterPosition(), each_pro.getId());
//        holder.product_name.setText(each_pro.getName());
//        holder.product_brand.setText(each_pro.getBrand());
//        holder.product_price.setText(each_pro.getPriceAsString(true));
//        holder.edit_quantity.setText(entry.getValue().toString());
//        total += entry.getValue() * each_pro.getPrice();
//        Log.i("demo", "hey " + DBConstants.getPriceAsString(total));
//        display_total.setText("Total: " + DBConstants.getPriceAsString(total));
//
//        }
//        //entry.getValue());
//        }
//        }
//
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//        });