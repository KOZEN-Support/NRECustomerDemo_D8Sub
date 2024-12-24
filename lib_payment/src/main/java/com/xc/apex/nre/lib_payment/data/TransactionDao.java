package com.xc.apex.nre.lib_payment.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM trans")
    LiveData<List<TransactionData>> getTransaction();

    @Query("SELECT * FROM trans WHERE id = :id")
    TransactionData getTransaction(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TransactionData trans);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    int update(TransactionData trans);

    @Query("DELETE FROM trans WHERE id = :id")
    void delete(String id);

    @Query("DELETE FROM trans")
    void deleteAll();
}
