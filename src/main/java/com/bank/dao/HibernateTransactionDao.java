package com.bank.dao;

import com.bank.domain.Transaction;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateTransactionDao extends AbstractDAO<Transaction> implements TransactionDao {

    public HibernateTransactionDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Transaction get(long id) {
        return super.get(id);
    }

    @Override
    public List<Transaction> getAll() {
        return list(namedQuery("com.bank.domain.Transaction.getAll"));
    }

    @Override
    public Transaction saveOrUpdate(Transaction transaction) {
        return persist(transaction);
    }

}
