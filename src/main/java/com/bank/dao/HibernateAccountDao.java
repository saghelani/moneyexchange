package com.bank.dao;

import com.bank.dao.AccountDao;
import com.bank.domain.Account;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateAccountDao extends AbstractDAO<Account> implements AccountDao {

    public HibernateAccountDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Account get(long id) {
        return super.get(id);
    }

    @Override
    public List<Account> getAll() {
        return list(namedQuery("com.bank.domain.Account.getAll"));
    }

    @Override
    public Account saveOrUpdate(Account account) {
        return persist(account);
    }

    @Override
    public void delete(long id) {
        Account accountToDelete = currentSession().load(Account.class, id);
        if (accountToDelete != null) {
            currentSession().delete(accountToDelete);
        }
    }
}
