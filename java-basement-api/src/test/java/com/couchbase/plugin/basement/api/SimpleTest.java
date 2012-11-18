
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

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class SimpleTest extends TestCase {

    private final static String COUCHBASE_URI = "couchbase.uris";
    private final static String COUCHBASE_BUCKET = "couchbase.bucket";
    private final static String COUCHBASE_PASSWORD = "couchbase.password";


    private List uris = new ArrayList();
    private String bucket = null;
    private String password = null;


    @Before
    public void setUp() throws Exception {
        InputStream in = getClass().getClassLoader().getResourceAsStream("couchbase.properties");
        Properties p = new Properties();
        p.load(in);

        // load the list of URI
        String urisAsString = (String)p.get(COUCHBASE_URI);
        List<String> x = Arrays.asList(  urisAsString.split(",") );
        for ( String u : x ) {
            URI uri = new URI(u);
            uris.add( uri );

        }


        bucket = (String)p.get(COUCHBASE_BUCKET);
        password = (String)p.get(COUCHBASE_PASSWORD);
    }


    public void testConnection() throws Exception {

        Client client = new Client(uris, bucket, password);

        assertEquals( client.getVersions().isEmpty() , false );


        Document doc = new Document();
        doc.put("first","Tugdual");
        doc.put("last","Grall");
        doc.put("dob",new Date());

        client.save("tug", doc);


        Document newDoc = new Document();
        newDoc.put("first","Malo");
        newDoc.put("last","Grall");
        newDoc.put("dob",new Date());
        String key = client.save(newDoc);


        Document getDoc = client.getByKey(key);
        System.out.println( getDoc.get("first") );
        System.out.println( getDoc.get("last") );
        System.out.println( getDoc.get("dob") );
        System.out.println( getDoc.getKey() );



    }



    @After
    public void cleanUp() throws Exception {
    }


}
