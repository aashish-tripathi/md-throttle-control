package com.clsa.md.throttlecontrol;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DataPublisher implements Runnable{

    final List<String> symbols = Arrays.asList("A", "AAL", "AAP", "AAPL", "ABBV", "ABC", "ABMD", "ABT", "ACN", "ADBE",
            "ADI", "ADM", "ADP", "ADSK", "AEE", "AEP", "AES", "AFL", "AIG", "AIV", "AIZ", "AJG", "AKAM", "ALB",
            "ALGN", "ALK", "ALL", "ALLE", "ALXN", "AMAT", "AMCR", "AMD", "AME", "AMGN", "AMP", "AMT", "AMZN",
            "ANET", "ANSS", "ANTM", "AON", "AOS", "APA", "APD", "APH", "APTV", "ARE", "ATO", "ATVI", "AVB",
            "AVGO", "AVY", "AWK", "AXP", "AZO", "BA", "BAC", "BAX", "BBY", "BDX", "BEN", "BF", "BIIB", "BIO",
            "BK", "BKNG", "BKR", "BLK", "BLL", "BMY", "BR", "BRK", "BSX", "BWA", "BXP", "C", "CAG", "CAH", "CARR",
            "CAT", "CB", "CBOE", "CBRE", "CCI", "CCL", "CDNS", "CDW", "CE", "CERN", "CF", "CFG", "CHD", "CHRW",
            "CHTR", "CI", "CINF", "CL", "CLX", "CMA", "CMCSA");

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            IMarketDataProcessor dataProcessor = new MarketDataProcessor();
            generateData(dataProcessor, symbols, 10);
        }

    }

    private void generateData(IMarketDataProcessor marketDataProcessor, List<String> symbols, int noOfRecords)  {
        for (int i = 1; i <= noOfRecords; i++) {
            int finalI = i;
            for (String symbol : symbols) {
                marketDataProcessor.onMessage(new MarketData(symbol,finalI,LocalDateTime.now()));
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new DataPublisher()).start();
    }

}
