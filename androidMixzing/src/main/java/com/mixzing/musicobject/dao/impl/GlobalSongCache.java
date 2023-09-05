package com.mixzing.musicobject.dao.impl;

import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalSongCache<Long,GlobalSong> extends LinkedHashMap<Long,GlobalSong> {

    protected int maxSize;
    
    public GlobalSongCache(int size) {
        super(size, 0.75f, true);
        maxSize = size;
    }

    protected boolean removeEldestEntry(final Map.Entry eldest) {
        return size() > maxSize;
    }

}
