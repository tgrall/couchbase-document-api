Basement: The simple ODM for Couchbase on Java
=============================================

Easy way to create an save a document:

```java
Document doc = new Document();
doc.put("first","Tugdual");
doc.put("last","Grall");
doc.put("dob",new Date());

client.save("tug", doc);
```

This will save a new document associated with the key "tug" : 
```js
{
  "_id": "tug",
  "last": "Grall",
  "dob": "Nov 18, 2012 11:45:16 AM",
  "first": "Tugdual"
}
```

Document with generated ID
```java


Document doc = new Document();
doc.put("first","Tugdual");
doc.put("last","Grall");
doc.put("dob",new Date());

String key = client.save( doc);

Document getDoc = client.getByKey(key);
System.out.println( getDoc.get("first") );
System.out.println( getDoc.get("last") );
System.out.println( getDoc.get("dob") );
System.out.println( getDoc.getKey() );


```

The stored document looks like :

```js
{
  "_id": "4d0bfa18-040f-4859-8cd8-e904cf8da9b9",
  "last": "Grall",
  "dob": "Nov 18, 2012 11:48:56 AM",
  "first": "Tugdual"
}
```
