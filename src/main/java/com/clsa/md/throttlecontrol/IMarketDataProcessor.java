package com.clsa.md.throttlecontrol;

public interface IMarketDataProcessor {
    void onMessage(MarketData data);
    void publishAggregatedMarketData(MarketData data);

    int getOnMsgCount();
    int getOnPubMsgCount();
}
