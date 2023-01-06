package com.safal.babybuy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDAO {

    @Insert
    void addItem(Item item);

    @Query("SELECT * FROM item")
    List<Item> getAllItems();

    @Update
    void updateItem(Item item);

    @Query("DELETE FROM item WHERE id= :id")
    void deleteItem(int id);

    @Query("UPDATE item SET status='Purchased' WHERE id = :itemId")
    void setItemAsPurchased(int itemId);

    @Query("DELETE FROM item")
    void deleteAll();

}
