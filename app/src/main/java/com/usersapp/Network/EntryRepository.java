package com.usersapp.Network;

import com.usersapp.Network.Entry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EntryRepository {
    private final HashMap<Integer, Entry> entries = new HashMap<>();
    public Entry findEntryById(int id) {
        return entries.get(id);
    }

    public void add(Entry entry) {
        entries.put(entry.getId(), entry);
    }

    public void remove(int id) {
        entries.remove(id);
    }

    public void clearEntries() {
        entries.clear();
    }

    public Entry[] getEntries() {
        Entry[] myEntries = new Entry[entries.size()];
        return entries.values().toArray(myEntries);
    }
}
