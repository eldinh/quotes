package ru.sfedu.api;


import ru.sfedu.BaseTest;
import ru.sfedu.Constants;
import ru.sfedu.model.SecurityHistoryBuilder;
import ru.sfedu.model.*;


import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static ru.sfedu.Constants.*;


public class DataProviderJDBCTest extends BaseTest {
    DataProviderJDBC data = new DataProviderJDBC();
    private void deleteFile(String dbName) {
        data.dropTable(dbName);
    }

    @Override
    protected void setUp() {
        deleteFile(USER_TABLE_NAME);
        deleteFile(STOCK_TABLE_NAME);
        deleteFile(BOND_TABLE_NAME);
        deleteFile(MARKET_TABLE_NAME);
        data.deleteAllSecurityHistories("SBER");
        data.deleteAllSecurityHistories("SBERBOND");
    }


    public void testGetUsers()  {
        result = data.appendUsers(users);
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        result = data.appendUsers(users);
        result = data.getUsers();
        assert(result.getStatus().equals(Constants.SUCCESS));
        assertEquals(result.getStatus(), SUCCESS);
        assertEquals(result.getBody(), users);
    }

    public void testFailGetUsers()  {
        assert(data.getUsers().getStatus().equals(Constants.FAIL));
    }

    public void testAppendUsers()  {

    }

    public void testFailAppendUsers()  {
    }


    public void testDeleteAllUsers()  {
    }

    public void testFailDeleteAllUsers()  {
    }

    public void testGetUserById() {
    }

    public void testFailGetUserById()  {
    }

    public void testUpdateUsers() {
    }

    public void testFailUpdateUsers() {
    }

    public void testDeleteUserById() {
    }
    public void testAppendOrUpdateMarket(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assert data.appendOrUpdateMarket(MarketType.SHARES);
        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isPresent();
        assertEquals(market.get().getTickerList().size(), stocks.size());
    }

    public void testFailAppendOrUpdateMarket(){
        assertFalse(data.appendOrUpdateMarket(null));
    }

    public void testGetMarkets(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);

        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        marketResult = data.getMarkets();
        assertEquals(marketResult.getStatus(), SUCCESS);
        assertEquals(marketResult.getBody().size(), 2);
    }
    public void testFailGetMarkets(){
        marketResult = data.getMarkets();
        assertEquals(marketResult.getStatus(), FAIL);
    }

    public void testGetMarket(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);

        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isPresent();
        assertEquals(market.get().getTickerList().size(), stocks.size());

        market = data.getMarket(MarketType.BONDS);
        assert market.isPresent();
        assertEquals(market.get().getTickerList().size(), bonds.size());
    }

    public void testFailGetMarket(){
        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isEmpty();

        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        market = data.getMarket(MarketType.SHARES);
        assert market.isEmpty();
    }

    public void testAppendStocks()  {
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        System.out.println(data.getStocks());
        assertEquals(stockResult.getStatus(), SUCCESS);
        Stock stock = new StockBuilder()
                .withCapitalization(0).withDividendSum(0).withTicker("WWWWWWWWW")
                .withName("").withShortName("").withLatName("")
                .withNominal(100).withSecurityHistory(getHistories("WWWWWWWWW"))
                .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                .build();
        data.appendStocks(new ArrayList<>(List.of(stock)));
    }

    public void testFailAppendStocks()  {
        List<Stock> otherStocks = new ArrayList<>(stocks);
        otherStocks.add(stocks.get(0));
        stockResult = data.appendStocks(otherStocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.appendStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.appendStocks(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        assertEquals(stockResult.getBody().size(), stocks.size());


    }

    public void testGetStocks()  {
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getBody().size(), stocks.size());
    }

    public void testFailGetStocks()  {
        stockResult = data.getStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }

    public void testUpdateStocks(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());
        Stock stock = new StockBuilder().withTicker("SBER").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBER"))
                .withType(StockType.COMMON).withDividendSum(0).withCapitalization(0)
                .build();
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        Optional<Stock> stockFromDB = stockResult.getBody().stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (stockFromDB.isPresent())
            assertEquals(stockFromDB.get(), stock);
        else
            assert(false);
    }

    public void testFailUpdateStocks(){
        Stock stock = new StockBuilder().withTicker("SHUSHU").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SHUSHU"))
                .withType(StockType.COMMON).withDividendSum(0).withCapitalization(0)
                .build();
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        assertEquals(stockResult.getStatus(), FAIL);
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        stockResult = data.updateStocks(new ArrayList<>());
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.updateStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }
    public void testDeleteStockByTicker()  {
        data.deleteAllSecurityHistories("SBER");
        data.appendSecurityHistory(histories, "SBER");
        securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        Optional<Stock> deletedStock = data.deleteStockByTicker("SBER");
        securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        Optional<Stock> stock= stocks.stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (deletedStock.isPresent())
            assertEquals(deletedStock, stock);
        else
            assert(false);
        stockResult = data.getStocks();
        List<Stock> updatedStocks = new ArrayList<>(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        assertEquals( stockResult.getBody().size(), stocks.size() -1);
        updatedStocks.removeAll(stockResult.getBody());
        assertEquals(updatedStocks.size(), 1);
        assertEquals(updatedStocks.get(0), deletedStock.get());


    }

    public void testFailDeleteStockByTicker(){
        try{
            data.deleteStockByTicker("SBABABABQQWEQR");
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert(true);
        }
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        try{
            Optional<Stock> deletedStock = data.deleteStockByTicker("SBABABABQQWEQR");
            assertEquals(deletedStock, Optional.empty());
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert(false);
        }


    }
    public void testGetStockByTicker()  {
        data.appendSecurityHistory(histories, "SBER");
        data.appendStocks(stocks);
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        if (stock.isPresent())
            assert(true);
        else
            assert(false);
        assertEquals(stock.get().getHistory(), data.getSecurityHistoryByDate("SBER"));
    }

    public void testFailGetStockByTicker(){
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        if (stock.isEmpty())
            assert(true);
        else
            assert(false);
        data.appendStocks(stocks);
        stock = data.getStockByTicker("SBER1231421");
        System.out.println(stock);
        if (stock.isEmpty())
            assert(true);
        else
            assert(false);
    }


    public void testDeleteAllStocks(){
        stockResult = data.appendStocks(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.deleteAllStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        assertEquals(stockResult.getBody().size(), stocks.size());
        for (String ticker : stocks.stream().map(Security::getTicker).toList()){
            securityHistoryResult = data.getSecurityHistories(ticker);
            assertEquals(securityHistoryResult.getStatus(), FAIL);
        }
    }

    public void testFailDeleteAllStocks(){
        stockResult = data.deleteAllStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        assertEquals(stockResult.getBody().size(), 0);
    }

    public void testAppendSecurityHistory(){
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);

    }

    public void testFailAppendSecurityHistory(){
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(histories, null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(null, "");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getStatus(), WARN);
        assertEquals(securityHistoryResult.getBody() ,histories);
    }

    public void testGetSecurityHistories(){
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        securityHistoryResult = data.getSecurityHistories("SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getBody(), histories);
    }

    public void testFailGetSecurityHistories(){
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);

        securityHistoryResult = data.getSecurityHistories("SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        securityHistoryResult = data.getSecurityHistories(null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
    }

    public void testGetSecurityHistoryByDate(){
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        for (String date : dateList){
            SecurityHistory securityHistory = data.getSecurityHistoryByDate("SBER", date);
            Optional<SecurityHistory> securityHistory1 = histories.stream().filter(x -> x.getDate().equals(date)).findFirst();
            assert securityHistory1.isPresent();
            assertEquals(securityHistory1.get(), securityHistory);
        }
    }

    public void testFailGetSecurityHistoryByDate(){
        SecurityHistory securityHistory = new SecurityHistoryBuilder().empty("SBER1");
        for (String date : dateList){
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER",date);
            assertNotEquals(securityHistory1, securityHistory);
        }
        for (String date : dateList){
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER", date);
            assertEquals(securityHistory1, new SecurityHistoryBuilder().empty(date, "SBER"));
        }
    }

    public void testAppendOrUpdate(){
        assertEquals(data.getSecurityHistories("SBER").getStatus(), FAIL);
        assert(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), 1);
        SecurityHistory securityHistory = new SecurityHistoryBuilder().withTicker("SBER").withAveragePerDay(12314)
                .withClosePrice(10).withOpenPrice(9).withVolume(1000).withDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))
                .build();
        assert(data.appendOrUpdate(securityHistory, "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), 1);
        assertEquals(securityHistoryResult.getBody().get(0), securityHistory);
    }

    public void testFailAppendOrUpdate(){
        assertFalse(data.appendOrUpdate(null, "SBER"));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), null));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), "SBERE!"));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBE1R"), "SBERE!"));
    }

    public void testDeleteAllSecurityHistories(){
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        Result<SecurityHistory> securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size());
    }


    public void testFailDeleteAllSecurityHistories(){
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.deleteAllSecurityHistories("SBER").getStatus(), FAIL);
    }

    public void testAppendBonds(){
        data.deleteAllSecurityHistories(SBERBOND);
        securityHistoryResult = data.appendSecurityHistory(historiesBond, SBERBOND);
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(data.getSecurityHistoryByDate(SBERBOND), getHistories(SBERBOND));
        bondResult = data.appendBonds(bonds);
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), SUCCESS);

    }

    public void testFailAppendBonds(){
        deleteFile(BOND_TABLE_NAME);
        data.deleteAllSecurityHistories(SBERBOND);
        bondResult = data.appendBonds(bonds);
        bondResult = data.appendBonds(bonds);
        assertEquals(bondResult.getStatus(), WARN);
        assertEquals(bondResult.getBody().size(), bonds.size());
        Bond bond = new BondBuilder().withTicker("SBERBOND").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();

        bondResult = data.appendBonds(new ArrayList<>(List.of(bond, bond)));
        assertEquals(bondResult.getStatus(), FAIL);
        assertEquals(data.appendBonds(null).getStatus(), FAIL);
        assertEquals(data.appendBonds(new ArrayList<>()).getStatus(), FAIL);
    }

    public void testGetBonds(){
        data.deleteAllSecurityHistories(SBERBOND);
        bondResult = data.appendBonds(bonds);
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testFailGetBonds(){
        data.deleteAllSecurityHistories(SBERBOND);
        bondResult = data.getBonds();
        assertEquals(bondResult.getStatus(), FAIL);
    }

    public void testUpdateBonds(){
        bondResult = data.appendBonds(bonds);
        assertEquals(bondResult.getStatus(), SUCCESS);
        Bond bond = new BondBuilder().withTicker("SBERBOND").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        assertEquals(bondResult.getStatus(), SUCCESS);
        securityHistoryResult = data.getSecurityHistories(SBERBOND);
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        System.out.println();
        assert(securityHistoryResult.getBody().contains(getHistories(SBERBOND)));
        Optional<Bond> bond1 = data.getBondByTicker(SBERBOND);
        assert(bond1.isPresent());
        assertEquals(bond1.get(), bond);
    }

    public void testFailUpdateBonds(){
        Bond bond = new BondBuilder().withTicker("SQFWQDQ").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();

        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        assertEquals(bondResult.getStatus(), FAIL);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        assertEquals(bondResult.getStatus(), WARN);

        bondResult = data.updateBonds(null);
        assertEquals(bondResult.getStatus(), FAIL);
        bondResult = data.updateBonds(new ArrayList<>());
        assertEquals(bondResult.getStatus(), FAIL);
        Bond bond1 = new BondBuilder().withTicker(null).withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond1)));
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), FAIL);
    }

    public void testDeleteBondByTicker(){
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        Optional<Bond> bond = data.deleteBondByTicker(SBERBOND);
        assert(bond.isPresent());
        assertEquals(bond, bonds.stream().filter(x -> x.getTicker().equals(SBERBOND)).findFirst());
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), bonds.size() - 1);
    }

    public void testFailDeleteBondByTicker(){
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        Optional<Bond> bond = data.deleteBondByTicker("FFFFFFFFFFFFFFFFFFF");
        assert(bond.isEmpty());
        assertEquals(data.getBonds().getBody().size(), bonds.size());
    }

    public void testDeleteAllBonds(){
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        bondResult = data.deleteAllBonds();
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), SUCCESS);
        assertEquals(bondResult.getBody().size(), bonds.size());
        for (String ticker : bonds.stream().map(Security::getTicker).toList()){
            securityHistoryResult = data.getSecurityHistories(ticker);
            assertEquals(securityHistoryResult.getStatus(), FAIL);
        }

        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), 0);
    }

    public void testFailDeleteAllBonds(){
        assertEquals(data.deleteAllBonds().getStatus(), FAIL);
    }

    public void testGetBondByTicker(){
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        Optional<Bond> bond = data.getBondByTicker(SBERBOND);
        assert(bond.isPresent());
        assertEquals(bond, bonds.stream().filter(x -> x.getTicker().equals(SBERBOND)).findFirst());
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testFailGetBondByTicker(){
        Optional<Bond> bond = data.getBondByTicker(SBERBOND);
        assert bond.isEmpty();
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        bond = data.getBondByTicker("QWEFASWFASF");
        assert bond.isEmpty();
    }


}