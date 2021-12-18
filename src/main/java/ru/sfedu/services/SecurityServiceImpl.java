package ru.sfedu.services;

import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.api.DateProvider;
import ru.sfedu.model.*;

import java.util.List;
import java.util.Optional;

public class SecurityServiceImpl implements SecurityService {
        private final DateProvider data = new DataProviderJDBC();

        @Override
        public List<? extends Security> getActiveSecurities(MarketType marketType){
            return switch (marketType) {
                case BONDS -> data.getBonds().getBody();
                case SHARES -> data.getStocks().getBody();
            };
        }

        @Override
        public Optional<? extends Security> findSecurityByTicker(String ticker){
            Optional<Stock> security  = data.getStockByTicker(ticker);
            if (security.isEmpty())
                return data.getBondByTicker(ticker);
            return security;
        }

    @Override
    public List<SecurityHistory> showDetailedInfo(String ticker) {
        return null;
    }


}
