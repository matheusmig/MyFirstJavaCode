package com.company.PatternReactor;

import java.nio.channels.SelectionKey;
/**
 * Created by mmignoni on 2017-11-22.
 */
public interface EventHandler {
    public void handleEvent(SelectionKey handle) throws Exception;
}