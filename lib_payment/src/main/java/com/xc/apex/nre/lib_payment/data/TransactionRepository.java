package com.xc.apex.nre.lib_payment.data;

import androidx.lifecycle.LiveData;

import com.xc.apex.nre.lib_payment.utils.AppExecutors;
import com.xc.apex.nre.lib_payment.utils.GlobalData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionRepository {

    private static TransactionRepository instance;
    private TransactionDao transactionDao;

    private TransactionRepository(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public static TransactionRepository getInstance(TransactionDao transactionDao) {
        if (instance == null) {
            synchronized (TransactionRepository.class) {
                if (instance == null) {
                    instance = new TransactionRepository(transactionDao);
                }
            }
        }
        return instance;
    }

    public void createTransaction(final TransactionData trans) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String transId = GlobalData.getTransCounter();
                trans.setTransId(date + transId);
                transactionDao.insert(trans);
            }
        });
    }

    public LiveData<List<TransactionData>> getTransaction() {
        return this.transactionDao.getTransaction();
    }

    public TransactionData getTransaction(String id) {
        return this.transactionDao.getTransaction(id);
    }

    public void removeTransaction() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteAll();
            }
        });
    }

    public void removeTransaction(final String id) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                transactionDao.delete(id);
            }
        });
    }
}
