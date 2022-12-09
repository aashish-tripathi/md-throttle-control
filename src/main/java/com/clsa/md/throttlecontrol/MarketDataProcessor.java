package com.clsa.md.throttlecontrol;

import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

public class MarketDataProcessor implements IMarketDataProcessor {

    private final PublishSubject<MarketData> marketFeed = PublishSubject.create();

    private int onMsgCount=0; // to keep track of onMsg count received
    private int onPubMsgCount=0; // to keep track of onPubMsgCount count published

    public MarketDataProcessor() {
        marketFeed
                // 1 second sliding window
                .window(1, TimeUnit.SECONDS)
                .subscribe(marketDataObservable -> marketDataObservable
                        // find the first unique stock update entries
                        .distinct(MarketData::getSymbol)
                        // take the first 100 entries
                        .take(100)
                        // update
                        .subscribe(this::publishAggregatedMarketData, Throwable::printStackTrace));
    }

    @Override
    public void onMessage(MarketData data) {
        marketFeed.onNext(data);
        onMsgCount++;

    }
    @Override
    public void publishAggregatedMarketData(MarketData data) {
        onPubMsgCount++;
    }

    @Override
    public int getOnMsgCount() {
        return onMsgCount;
    }

    @Override
    public int getOnPubMsgCount() {
        return onPubMsgCount;
    }


}
