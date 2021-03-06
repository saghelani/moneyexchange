package com.bank;

import com.bank.config.NotFoundExceptionMapper;
import com.bank.controllers.AccountController;
import com.bank.controllers.TransactionController;
import com.bank.dao.HibernateAccountDao;
import com.bank.dao.HibernateTransactionDao;
import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.services.AccountServiceImpl;
import com.bank.services.TransactionServiceImpl;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MoneyExchangeApplication extends Application<MoneyExchangeConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MoneyExchangeApplication().run(args);
    }

    private final HibernateBundle<MoneyExchangeConfiguration> hibernate = new HibernateBundle<MoneyExchangeConfiguration>(Account.class, Transaction.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(MoneyExchangeConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public String getName() {
        return "MoneyExchange";
    }

    @Override
    public void initialize(final Bootstrap<MoneyExchangeConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final MoneyExchangeConfiguration configuration,
                    final Environment environment) {
       final AccountController accountController = new AccountController(new AccountServiceImpl(new HibernateAccountDao(hibernate.getSessionFactory())));
       final TransactionController transactionController = new TransactionController(
               new TransactionServiceImpl(new HibernateTransactionDao(hibernate.getSessionFactory()),
                       new AccountServiceImpl(new HibernateAccountDao(hibernate.getSessionFactory()))));
       environment.jersey().register(accountController);
       environment.jersey().register(transactionController);
       environment.jersey().register(new NotFoundExceptionMapper());
    }

}
