package com.bank;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MoneyExchangeApplication extends Application<MoneyExchangeConfiguration> {

    public static void main(final String[] args) throws Exception {
        new MoneyExchangeApplication().run(args);
    }

    @Override
    public String getName() {
        return "MoneyExchange";
    }

    @Override
    public void initialize(final Bootstrap<MoneyExchangeConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final MoneyExchangeConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
