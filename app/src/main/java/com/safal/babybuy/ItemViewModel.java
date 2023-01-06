package com.safal.babybuy;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private final ItemDAO dao;
    private Item item;

    private List<Item> itemList;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        Database database = Database.getDatabase(application);
        dao = database.itemDAO();
    }

    public List<Item> getItemList() {
        itemList = dao.getAllItems();
        return itemList;
    }
    public void addItem(Item item) {
        dao.addItem(item);
    }

    public void updateItem(Item item) {
        dao.updateItem(item);
    }

    public void markAsPurchased(int itemId) {
        dao.setItemAsPurchased(itemId);
    }

    public void deleteItem(int itemId) {
        dao.deleteItem(itemId);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void deleteAllItems() {dao.deleteAll();};
}
