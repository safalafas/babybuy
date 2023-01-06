package com.safal.babybuy;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class, Item.class}, version = 1)
public abstract class Database extends RoomDatabase {

    private static Database instance;

    public static synchronized Database getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "babybuy")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract UserDAO userDAO();

    public abstract ItemDAO itemDAO();

}
