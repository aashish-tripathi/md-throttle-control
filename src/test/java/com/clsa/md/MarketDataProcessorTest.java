package com.clsa.md;

import com.clsa.md.throttlecontrol.IMarketDataProcessor;
import com.clsa.md.throttlecontrol.MarketData;
import com.clsa.md.throttlecontrol.MarketDataProcessor;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MarketDataProcessorTest {

    final List<String> symbols = Arrays.asList("A", "AAL", "AAP", "AAPL", "ABBV", "ABC", "ABMD", "ABT", "ACN", "ADBE", "ADI", "ADM", "ADP", "ADSK", "AEE", "AEP", "AES", "AFL", "AIG", "AIV", "AIZ", "AJG", "AKAM", "ALB", "ALGN", "ALK", "ALL", "ALLE", "ALXN", "AMAT", "AMCR", "AMD", "AME", "AMGN", "AMP", "AMT", "AMZN", "ANET", "ANSS", "ANTM", "AON", "AOS", "APA", "APD", "APH", "APTV", "ARE", "ATO", "ATVI", "AVB", "AVGO", "AVY", "AWK", "AXP", "AZO", "BA", "BAC", "BAX", "BBY", "BDX", "BEN", "BF", "BIIB", "BIO", "BK", "BKNG", "BKR", "BLK", "BLL", "BMY", "BR", "BRK", "BSX", "BWA", "BXP", "C", "CAG", "CAH", "CARR", "CAT", "CB", "CBOE", "CBRE", "CCI", "CCL", "CDNS", "CDW", "CE", "CERN", "CF", "CFG", "CHD", "CHRW", "CHTR", "CI", "CINF", "CL", "CLX", "CMA", "CMCSA");

    //  Ensure that the publishAggregatedMarketData method is not called any more than 100 times/sec where this period is a sliding window.
    @ParameterizedTest
    @CsvSource({"200,10,100", "101,10,100", "100,11,200", "101,11,200"})
    public void publishAggregatedMarketDataMethodIsNotCalledAnyMoreThan100TimesPerSec(int noOfSymbols, int noOfSymbolUpdates, int expectedTotalPublishing) throws InterruptedException {
        List<MarketData> publishedEntries = new ArrayList<>();
        IMarketDataProcessor marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishedEntries.add(data);
            }

        };
        final int msSleepTimeForEachSymbolRecord = 100;
        createNSendDataUpdate(marketDataProcessor, symbols.stream().limit(noOfSymbols).collect(Collectors.toList()), msSleepTimeForEachSymbolRecord, noOfSymbolUpdates);
        Assert.assertEquals(expectedTotalPublishing, publishedEntries.size());
    }

    // Ensure each symbol will not have more than one update per second
    @ParameterizedTest
    @CsvSource({"0,0", "7,1", "11,2"})
    public void eachSymbolWillNotHaveMoreThanOneUpdatePerSecond(int noOfUpdateEntriesForEachSymbol, int expectedNoOfPublishingForEachSymbol) throws InterruptedException {
        HashMap<String, Integer> publishCount = new HashMap<>();
        IMarketDataProcessor marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishCount.put(data.getSymbol(), publishCount.getOrDefault(data.getSymbol(), 0) + 1);
            }
        };
        final int msSleepTimeForEachSymbolRecord = 100;
        List<String> inputSymbols = symbols.stream().limit(3).collect(Collectors.toList());
        createNSendDataUpdate(marketDataProcessor, inputSymbols, msSleepTimeForEachSymbolRecord, noOfUpdateEntriesForEachSymbol);
        inputSymbols.forEach(s -> {
            Assert.assertEquals(expectedNoOfPublishingForEachSymbol, publishCount.getOrDefault(s, 0).intValue());
        });
    }

    // Ensure each symbol will always have the latest market data when it is published
    @Test
    public void eachSymbolWillAlwaysHaveTheLatestMarketDataWhenItIsPublished() throws InterruptedException {
        HashMap<String, MarketData> publishedEntry = new HashMap<>();
        IMarketDataProcessor marketDataProcessor = new MarketDataProcessor() {
            @Override
            public void publishAggregatedMarketData(MarketData data) {
                super.publishAggregatedMarketData(data);
                publishedEntry.put(data.getSymbol(), data);
            }
        };
        final int delayInBatchUpdate = 100;
        final int noOfUpdateEntriesForEachSymbol = 11;
        List<String> inputSymbols = symbols.stream().limit(3).collect(Collectors.toList());
        createNSendDataUpdate(marketDataProcessor, inputSymbols, delayInBatchUpdate, noOfUpdateEntriesForEachSymbol);
        final double delta = 0.000001;
        inputSymbols.forEach(s -> {
            Assert.assertEquals(11, publishedEntry.get(s).getPrice(), delta);
            Assert.assertEquals(11, publishedEntry.get(s).getPrice(), delta);
        });
    }

    private void createNSendDataUpdate(IMarketDataProcessor marketDataProcessor, List<String> symbols, int delayInBatchUpdate, int noOfSymbolUpdates) throws InterruptedException {
        for (int i = 1; i <= noOfSymbolUpdates; i++) {
            int finalI = i;
            for (String symbol : symbols) {
                marketDataProcessor.onMessage(new MarketData(symbol,finalI, LocalDateTime.now()));
            }
            Thread.sleep(delayInBatchUpdate);
        }
    }
}
