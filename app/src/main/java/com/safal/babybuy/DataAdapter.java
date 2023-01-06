package com.safal.babybuy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private final Context context;
    private List<Item> itemList;
    private final ItemViewModel model;
    private List<Item> filteredItems;

    public DataAdapter(Context context, ItemViewModel model) {
        this.context = context;
        this.model = model;
        this.itemList = model.getItemList();
        this.filteredItems=new ArrayList<>(itemList);
    }

    public void deleteAllData() {model.deleteAllItems();
    itemList.clear();
    filteredItems.clear();
    };

    @NonNull
    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
        Item item = filteredItems.get(position);
        holder.mtViewName.setText(item.getName());
        holder.mtViewPrice.setText(item.getPrice());
        holder.mtViewDescription.setText(item.getDescription());
        holder.mtViewDate.setText(item.getSaveDate());
        holder.mtViewStatus.setText(item.getStatus());
        Bitmap image = BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length);
        holder.imageItem.setImageBitmap(image);

        holder.cardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_addItemsFragment, bundle);
        });
        holder.cardView.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(context, holder.cardView);
            menu.inflate(R.menu.item_options);
            menu.setOnMenuItemClickListener(option -> {
                Map<Integer, Runnable> optionMap = new HashMap<>();
                optionMap.put(R.id.menuEdit, () -> {
                    model.setItem(item);
                    Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_addItemsFragment);
                });
                optionMap.put(R.id.menuPurchase, () -> {
                    model.markAsPurchased(item.getId());
                    item.setStatus(context.getString(R.string.purchased));
                    itemList.set(position, item);
                    notifyItemChanged(position);
                });
                optionMap.put(R.id.menuSend, () -> sendSMS(item));
                optionMap.put(R.id.menuDelete, () -> {
                    deleteItem(position);
                });
                Runnable runnable = optionMap.get(option.getItemId());
                if (runnable != null) {
                    runnable.run();
                }
                return false;
            });
            menu.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return filteredItems == null ? 0 : filteredItems.size();
    }

    private void sendSMS(Item item) {
        String message = "Hey, this is an item in my BabyBuy app. \n Name: " + item.getName() + " \n Price: " + item.getPrice() + " \n Description: " + item.getDescription() + " \n Added Date: " + item.getSaveDate() + " \n Purchased Status: " + item.getStatus();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        MaterialTextView mtViewName;
        MaterialTextView mtViewPrice;
        MaterialTextView mtViewDescription;
        MaterialTextView mtViewDate;
        MaterialTextView mtViewStatus;
        ImageView imageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardItem);
            mtViewName = itemView.findViewById(R.id.txtItemName);
            mtViewPrice = itemView.findViewById(R.id.txtItemPrice);
            mtViewDescription = itemView.findViewById(R.id.txtItemDescription);
            imageItem = itemView.findViewById(R.id.imageItem);
            mtViewDate = itemView.findViewById(R.id.txtItemDate);
            mtViewStatus = itemView.findViewById(R.id.txtItemStatus);

        }
    }

    public void search(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(itemList);
        } else {
            for (Item item : itemList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())||item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sort(String sortOption) {
        switch (sortOption) {
            case "Date":
                // Sort the data by date
                Collections.sort(filteredItems, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return o1.getSaveDate().compareTo(o2.getSaveDate());
                    }
                });
                break;
            case "Alphabetically":
                // Sort the data alphabetically
                Collections.sort(filteredItems, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
        }
        notifyDataSetChanged();
    }

    public void purchased(boolean status) {
        filteredItems.clear();
        if (!status) {
            for (Item item: itemList) {
                if (Objects.equals(item.getStatus(), context.getString(R.string.not_purchased))) {
                    filteredItems.add(item);
                }
            }
        }
        else {
            for (Item item: itemList) {
                if (Objects.equals(item.getStatus(), context.getString(R.string.purchased))) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void deleteItem(int position) {
        Dialog confirmDialog = new Dialog(context);
        confirmDialog.setContentView(R.layout.confirm_dialog);
        confirmDialog.setTitle("CONFIRM DELETE?");
        Button btnYes = confirmDialog.findViewById(R.id.btnYes);
        Button btnNo = confirmDialog.findViewById(R.id.btnNo);
        confirmDialog.show();
        btnYes.setOnClickListener(view -> {
            int id = filteredItems.get(position).getId();
            model.deleteItem(id);
            filteredItems.remove(position);
            notifyItemRemoved(position);
            confirmDialog.dismiss();
        });

        btnNo.setOnClickListener(view -> confirmDialog.dismiss());
    }

}

