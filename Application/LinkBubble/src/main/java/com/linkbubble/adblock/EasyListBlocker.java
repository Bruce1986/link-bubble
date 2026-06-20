/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.linkbubble.adblock;

import android.content.Context;
import android.util.Log;

import com.linkbubble.util.CrashTracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Java-side ad blocker driven by EasyList domain rules bundled in assets.
 *
 * Phase 1 supports only EasyList "||domain.com^" / "@@||domain.com^" rules,
 * which cover the bulk of network-level ad blocking. Cosmetic (##) and
 * pattern (substring/regex) rules are intentionally ignored — they require
 * a content-script pipeline we don't have.
 *
 * Future work:
 *   - Periodic refresh from https://easylist.to/easylist/easylist.txt
 *   - Pattern-based rule matching (URL substrings, $options)
 */
public class EasyListBlocker {
    private static final String TAG = "EasyListBlocker";
    private static final String ASSET_BLOCK = "adblock_hosts.txt";
    private static final String ASSET_ALLOW = "adblock_whitelist.txt";

    private final Set<String> mBlockHosts;
    private final Set<String> mAllowHosts;

    public EasyListBlocker(Context context) {
        long startMs = System.currentTimeMillis();
        mBlockHosts = loadHosts(context, ASSET_BLOCK);
        mAllowHosts = loadHosts(context, ASSET_ALLOW);
        Log.d(TAG, "Loaded " + mBlockHosts.size() + " block + " + mAllowHosts.size()
                + " allow rules in " + (System.currentTimeMillis() - startMs) + "ms");
    }

    /**
     * Returns true if the request URL should be blocked.
     *
     * @param pageHost  host of the page that initiated the request (unused for now,
     *                  reserved for future $first-party / $third-party logic)
     * @param requestUrl full URL of the sub-resource request
     */
    public boolean shouldBlock(String pageHost, String requestUrl) {
        String requestHost = extractHost(requestUrl);
        if (requestHost == null) {
            return false;
        }
        if (matchesDomainOrParent(requestHost, mAllowHosts)) {
            return false;
        }
        return matchesDomainOrParent(requestHost, mBlockHosts);
    }

    /**
     * Walks dot-separated suffixes of {@code host} and returns true if any
     * is present in {@code domains}. Mirrors EasyList's "||domain^" semantics
     * which match the host or any subdomain.
     */
    private static boolean matchesDomainOrParent(String host, Set<String> domains) {
        if (domains.isEmpty()) {
            return false;
        }
        String h = host;
        while (h != null && !h.isEmpty()) {
            if (domains.contains(h)) {
                return true;
            }
            int dot = h.indexOf('.');
            h = (dot < 0) ? null : h.substring(dot + 1);
        }
        return false;
    }

    private static String extractHost(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        int schemeEnd = url.indexOf("://");
        int start = (schemeEnd >= 0) ? schemeEnd + 3 : 0;
        int end = url.length();
        for (int i = start; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c == '/' || c == '?' || c == '#' || c == ':') {
                end = i;
                break;
            }
        }
        if (end <= start) {
            return null;
        }
        return url.substring(start, end).toLowerCase();
    }

    private static Set<String> loadHosts(Context context, String assetName) {
        HashSet<String> hosts = new HashSet<>(64 * 1024);
        try (InputStream is = context.getAssets().open(assetName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                hosts.add(line.toLowerCase());
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to load " + assetName, e);
            CrashTracking.logHandledException(e);
        }
        return Collections.unmodifiableSet(hosts);
    }
}
