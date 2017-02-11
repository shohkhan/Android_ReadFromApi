/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.ilovezappos;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    //final static String GITHUB_BASE_URL = "https://api.zappos.com/Search";
    //final static String PARAM_QUERY = "term";
    //final static String PARAM_SORT = "key";
    //final static String sortBy = "b743e26728e16b81da139182bb2094357c31d331";
    /**
     * Builds the URL used to query GitHub.
     *
     * @param searchQuery The keyword that will be queried for.
     * @param baseUrl The base url string
     * @param paramQuery The parameter of the query
     * @param key The parameter named key
     * @param keyValue The value of the key
     * @return The URL to use to query the GitHub.
     */
    public static URL buildUrl(String searchQuery, String baseUrl, String paramQuery,
                               String key, String keyValue) {
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(paramQuery, searchQuery)
                .appendQueryParameter(key, keyValue)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}