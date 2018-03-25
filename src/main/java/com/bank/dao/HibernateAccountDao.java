package com.bank.dao;

import com.bank.domain.Account;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.persistence.EntityNotFoundException;
import javax.ws.rs.NotFoundException;
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

    /**
     * This method deletes an account by id
     * @param id Id of the account to delete
     * @throws NotFoundException when the account does not exist
     */
    @Override
    public void delete(long id) {
        try {
            Account accountToDelete = currentSession().load(Account.class, id);
            if (accountToDelete != null) {
                currentSession().delete(accountToDelete);
            }
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(String.format("Account %s not found", id));
        }
    }
}
