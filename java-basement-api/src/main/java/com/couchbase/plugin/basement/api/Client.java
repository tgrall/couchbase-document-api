/*
 * Copyright (C) 2009-2012 Couchbase, Inc.
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 *   IN THE SOFTWARE.
 */

package com.couchbase.plugin.basement.api;


import com.couchbase.client.CouchbaseClient;
import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * The Client class is the main entry point to work with a Couchbase Cluster
 */
public class Client extends CouchbaseClient {

    public Client(List<URI> baseList, String bucketName, String pwd) throws IOException {
        super(baseList, bucketName, pwd);
    }


    /**
     *
     * @param key
     * @param document
     */
    public void save(String key, Document document) {
        document.put("_id", key);

       String documentAsJson = this.getJson(document);
       this.set(key,0,documentAsJson);
    }

    public String save(Document document) {
        String key = null;
        if (document.getKey() == null) {
            key = UUID.randomUUID().toString() ;
            document.setKey(key);
        } else {
            key = document.getKey();
        }
        System.out.println( document );
        this.save(key, document);
        return key;
    }


    public Document getByKey(String key) {
        String documentAsJson = (String)this.get(key);
        return new Document( this.parse( documentAsJson ) )  ;
    }

    public HashMap<String, Object> parse(String json) {
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(json);


        Set<Map.Entry<String, JsonElement>> set = object.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            addJsonElement(key, value, map);

        }

        return map;
    }


    private void addJsonElement(String key, JsonElement value, HashMap<String, Object> map ) {


        if (value.isJsonArray()) {
            JsonArray jsonArray = value.getAsJsonArray();

            ArrayList listAsValue = new ArrayList<HashMap<String,Object>>();

            HashMap<String, Object> listElementMap = new HashMap<String, Object>();

            Iterator<JsonElement> jsonArrayIterator = jsonArray.iterator();
            while (jsonArrayIterator.hasNext()) {
                JsonElement jsonElement = jsonArrayIterator.next();

                listAsValue.add(parse(jsonElement.toString()));

            }


            map.put(key, listAsValue);


        } else if (value.isJsonPrimitive()) {
            // check the type using JsonPrimitive
            // TODO: check all types
            JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();
            if ( jsonPrimitive.isNumber() ) {
                map.put(key, jsonPrimitive.getAsInt());
            }  else {
                map.put(key, jsonPrimitive.getAsString());
            }
        } else {
            map.put(key, parse(value.toString()));
        }

    }


    /**
     * Create a JSON String from a map
     *
     * @param document
     * @return JSON String
     */
    public String getJson(Map<String,Object> document) {

        //TODO : Manage embeddable object as JSON embeddable
        //       Provide configuration option to select the type of JSON (embedded/properties with prefix)
        Gson gson = new Gson();
        String json = gson.toJson(document);
        return json;
    }
}
