package com.neko.monitor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NekoCallbackClientTracker {
    private static final Logger log = Logger.getLogger(NekoCallbackClientTracker.class.getName());

    private Map<String, List<NekoCallback>> remoteCallbacks;

    public NekoCallbackClientTracker() {
        this.remoteCallbacks = new HashMap<>();
    }

    /**
     * Register a callback to the entry (key==path) of remoteCallbacks
     */
    public void register(String path, NekoCallback callback) {
        List<NekoCallback> callbackList = getCallbackList(path, true);
        Iterator<NekoCallback> iter = callbackList.iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(callback)) {
                iter.remove();
            }
        }
        callbackList.add(callback);
    }

    /**
     * Deregister a callback from the entry (key==path) of remoteCallbacks
     */
    public void deregister(String path, NekoCallback callback) {
        List<NekoCallback> callbackList = getCallbackList(path, false);
        if (null == callbackList) {
            return ;
        }
        Iterator<NekoCallback> iter = callbackList.iterator();
        while (iter.hasNext()) {
            if (iter.next().equals(callback)) {
                iter.remove();
                return ;
            }
        }
        dropEntryIfEmpty(path);
    }

    /**
     * Deregister a callback from all entries of remoteCallbacks
     */
    public void deregister(NekoCallback callback) {
        for (Map.Entry<String, List<NekoCallback>> entry : remoteCallbacks.entrySet()) {
            deregister(entry.getKey(), callback);
        }
    }

    /**
     * Check whether  a callback already appears in the entry (key==path) of remoteCallbacks
     */
    public boolean isRegistered(String path, NekoCallback callback) {
        List<NekoCallback> callbackList = getCallbackList(path, false);
        if (null == callbackList) {
            return false;
        }
        for (NekoCallback cb : callbackList) {
            if (cb.equals(callback)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inform all clients listening to the update to path.
     */
    public void informClients(String path, String text, String error) {
        List<NekoCallback> callbackList = getCallbackList(path, false);
        if (null == callbackList) {
            return ;
        }
        Iterator<NekoCallback> iter = callbackList.iterator();
        while (iter.hasNext()) {
            NekoCallback callback = iter.next();
            log.log(Level.FINE, "checking " + callback.toString());
            if (!callback.isValid()) {
                log.log(Level.FINE, "not valid");
                iter.remove();
            } else {
                log.log(Level.FINE, "valid, invoking");
                callback.invoke(path, text, error);
            }
        }
        dropEntryIfEmpty(path);
    }

    /**
     * Drop the entry (key==path) of remoteCallbacks if the entry is empty.
     */
    private void dropEntryIfEmpty(String path) {
        List<NekoCallback> callbackList = getCallbackList(path, false);
        if (null != callbackList && callbackList.isEmpty()) {
            remoteCallbacks.remove(path);
        }
    }

    /**
     * Get list of callbacks listenging to the update to path.
     * Create entry if createIfNull is true and the entry doesn't exist.
     */
    private List<NekoCallback> getCallbackList(String path, boolean createIfNull) {
        if (remoteCallbacks.containsKey(path)) {
            return remoteCallbacks.get(path);
        } else if (createIfNull) {
            List<NekoCallback> callbackList = new LinkedList<>();
            remoteCallbacks.put(path, callbackList);
            return callbackList;
        } else {
            return null;
        }
    }
}
