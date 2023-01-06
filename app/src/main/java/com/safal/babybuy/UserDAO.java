package com.safal.babybuy;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void addUser(User user);

    @Query("SELECT * FROM user WHERE email = :userEmail and password = :userPassword LIMIT 1")
    User getUser(String userEmail, String userPassword);

    @Query("SELECT * FROM user WHERE email=:userEmail")
    List<User> doesEmailExist(String userEmail);

}
