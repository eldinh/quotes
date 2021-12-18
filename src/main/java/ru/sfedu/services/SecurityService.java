package ru.sfedu.services;

import ru.sfedu.model.MarketType;
import ru.sfedu.model.Security;
import ru.sfedu.model.SecurityHistory;

import java.util.List;
import java.util.Optional;

public interface SecurityService {
    // service перенести  в dataProvider

    /**
     * Основной метод, отображающий котировки активных(которые продаются или покупаются чаще других) ценных бумаг в виде таблицы.
     * Имеет также расширяющий вариант ShowDetailedInfo, который вызывается, когда сработает обработка событий на ценной бумаги той или иной компании (т.е. когда пользователь кликнет на интересующую его компанию).
     * В качестве параметра передается название рынка и в зависимости от входного значения срабатывает либо функция getActiveBonds, либо getActiveStocks
     * @param marketType - Тип биржи
     * @return List<? extends Security> - Массив объектов классов ценных бумаг
     */
    public List<? extends Security> getActiveSecurities(MarketType marketType);


    /**
     * Метод для поиска ценной бумаги по его названию или тикеру(кодовое обозначение актива на бирже).
     * При успешном обнаружении компании будет выводиться котировка его акции, т.е. вызовется функция ShowDetailedInfo.
     * При наличии аргумента showActive вызывается функция getActiveSecurities
     * @param ticker - Тикер ценной бумаги
     * @return Optional<? extends Security> - Ценная бумага
     */
    public Optional<? extends Security> findSecurityByTicker(String ticker);


    /**
     * Функция, которая является включающей для findSecurityByTicker.
     * Она выводит информацию об стоимости акции за указанный период в виде графика. В качестве входного параметра принимает тикер ценной бумаги.
     * Имеет включающий вариант использования showInfo
     * Расширяется вариантом использования setNotification в случае, если пользователь решит задать оповещение.
     * @param ticker - тикер ценной бумаги
     * @return List<SecurityHistory> - котировка данной ценной бумаги за все время его торговли на рынке
     */
    public List<SecurityHistory> showDetailedInfo(String ticker);
}
