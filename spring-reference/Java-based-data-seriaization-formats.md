
# Java-based data seriaization formats


Below is a practical cookbook: each block shows (1) Maven deps, (2) a tiny schema (if needed), and (3) minimal **serialize → deserialize** code. Copy-paste into a small Maven project and run.

---

## 1) Avro (row-oriented, schema + binary)

**pom.xml**

```xml
<dependency>
  <groupId>org.apache.avro</groupId><artifactId>avro</artifactId><version>1.11.4</version>
</dependency>
```

**user.avsc**

```json
{"type":"record","name":"User","namespace":"demo.avro",
 "fields":[{"name":"name","type":"string"},{"name":"age","type":"int"}]}
```

**Java**

```java
var schema = new org.apache.avro.Schema.Parser().parse(
    new String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of("user.avsc")))
);
var user = new org.apache.avro.generic.GenericData.Record(schema);
user.put("name", "Ashfaq"); user.put("age", 28);

// Serialize
var file = new java.io.File("user.avro");
var writer = new org.apache.avro.file.DataFileWriter<org.apache.avro.generic.GenericRecord>(
        new org.apache.avro.generic.GenericDatumWriter<>(schema));
writer.create(schema, file); writer.append(user); writer.close();

// Deserialize
var reader = new org.apache.avro.file.DataFileReader<>(
        file, new org.apache.avro.generic.GenericDatumReader<org.apache.avro.generic.GenericRecord>(schema));
var read = reader.next();
System.out.println(read.get("name")+"-"+read.get("age"));
```

---

## 2) Parquet (columnar, analytics)

**pom.xml**

```xml
<dependency>
  <groupId>org.apache.parquet</groupId><artifactId>parquet-avro</artifactId><version>1.14.2</version>
</dependency>
```

**Java (Parquet via Avro schema)**

```java
var schema = new org.apache.avro.Schema.Parser().parse(
  "{\"type\":\"record\",\"name\":\"Row\",\"fields\":[{\"name\":\"id\",\"type\":\"int\"},{\"name\":\"city\",\"type\":\"string\"}]}"
);
var path = org.apache.hadoop.fs.Path.getPathWithoutSchemeAndAuthority(
  new org.apache.hadoop.fs.Path("data.parquet"));

try (var writer = org.apache.parquet.avro.AvroParquetWriter
        .<org.apache.avro.generic.GenericRecord>builder(path)
        .withSchema(schema).build()) {
    var r = new org.apache.avro.generic.GenericData.Record(schema);
    r.put("id", 1); r.put("city","Bengaluru"); writer.write(r);
}
// Read back
try (var reader = org.apache.parquet.hadoop.ParquetReader
        .<org.apache.avro.generic.GenericRecord>builder(
            new org.apache.parquet.avro.AvroReadSupport<>(), path).build()) {
    var rec = reader.read(); System.out.println(rec.get("city"));
}
```

> Tip: Parquet uses Hadoop Path classes; no Hadoop cluster required for local files.

---

## 3) ORC (columnar, Hive-optimized)

**pom.xml**

```xml
<dependency>
  <groupId>org.apache.orc</groupId><artifactId>orc-core</artifactId><version>1.9.4</version>
</dependency>
```

**Java (VectorizedRowBatch)**

```java
var schema = org.apache.orc.TypeDescription.fromString("struct<name:string,age:int>");
try (var writer = org.apache.orc.OrcFile.createWriter(
        new org.apache.hadoop.fs.Path("data.orc"),
        org.apache.orc.OrcFile.writerOptions(new org.apache.hadoop.conf.Configuration()).setSchema(schema))) {

    var batch = schema.createRowBatch();
    var nameCol = (org.apache.orc.impl.writer.StringColumnVector) batch.cols[0];
    var ageCol  = (org.apache.orc.impl.writer.LongColumnVector)   batch.cols[1];

    int r = batch.size++;
    nameCol.setVal(r, "Ashfaq".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    ageCol.vector[r] = 28;
    writer.addRowBatch(batch);
}
// Read
try (var reader = org.apache.orc.OrcFile.createReader(
        new org.apache.hadoop.fs.Path("data.orc"),
        org.apache.orc.OrcFile.readerOptions(new org.apache.hadoop.conf.Configuration()));
     var rows = reader.rows()) {
    var batch = reader.getSchema().createRowBatch();
    while (rows.nextBatch(batch)) {
        var nameCol = (org.apache.orc.impl.reader.StringColumnVector) batch.cols[0];
        var ageCol  = (org.apache.orc.impl.reader.LongColumnVector)   batch.cols[1];
        System.out.println(nameCol.toString(0)+"-"+ageCol.vector[0]);
    }
}
```

---

## 4) Protocol Buffers (compact RPC/gRPC)

**pom.xml**

```xml
<dependency>
  <groupId>com.google.protobuf</groupId><artifactId>protobuf-java</artifactId><version>3.25.3</version>
</dependency>
```

**user.proto**

```proto
syntax = "proto3";
package demo.pb;
message User { string name = 1; int32 age = 2; }
```

Generate Java with `protoc`, then:
**Java**

```java
var u = demo.pb.User.newBuilder().setName("Ashfaq").setAge(28).build();
byte[] bytes = u.toByteArray();                 // serialize
var back = demo.pb.User.parseFrom(bytes);       // deserialize
System.out.println(back.getName());
```

---

## 5) Thrift (IDL + RPC + cross-lang)

**pom.xml**

```xml
<dependency>
  <groupId>org.apache.thrift</groupId><artifactId>libthrift</artifactId><version>0.20.0</version>
</dependency>
```

**user.thrift**

```thrift
namespace java demo.thrift
struct User { 1:string name, 2:i32 age }
```

Generate Java with `thrift --gen java user.thrift`, then:

```java
var u = new demo.thrift.User("Ashfaq", 28);
var ms = new org.apache.thrift.transport.TMemoryBuffer(128);
var proto = new org.apache.thrift.protocol.TBinaryProtocol(ms);
u.write(proto); // serialize
var u2 = new demo.thrift.User(); u2.read(proto); // deserialize (rewind buffer in real use)
```

---

## 6) Kryo (very fast Java object bin)

**pom.xml**

```xml
<dependency>
  <groupId>com.esotericsoftware</groupId><artifactId>kryo</artifactId><version>5.6.0</version>
</dependency>
```

**Java**

```java
record User(String name, int age) {}
var kryo = new com.esotericsoftware.kryo.Kryo();
kryo.register(User.class);
var out = new com.esotericsoftware.kryo.io.Output(new java.io.FileOutputStream("u.kryo"));
kryo.writeObject(out, new User("Ashfaq", 28)); out.close();

var inp = new com.esotericsoftware.kryo.io.Input(new java.io.FileInputStream("u.kryo"));
var back = kryo.readObject(inp, User.class); inp.close();
System.out.println(back);
```

---

## 7) Jackson (JSON; readable APIs)

**pom.xml**

```xml
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId><version>2.17.2</version>
</dependency>
```

**Java**

```java
record User(String name, int age) {}
var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
var json = mapper.writeValueAsString(new User("Ashfaq", 28));
var back = mapper.readValue(json, User.class);
System.out.println(json); System.out.println(back.name());
```

---

## 8) BSON (MongoDB binary JSON)

**pom.xml**

```xml
<dependency>
  <groupId>org.mongodb</groupId><artifactId>bson</artifactId><version>5.1.0</version>
</dependency>
```

**Java**

```java
var doc = new org.bson.Document("name","Ashfaq").append("age",28);
byte[] bson = doc.toBson(); // serialize
var back = org.bson.Document.parse(doc.toJson()); // or decode with a codec
System.out.println(back.getString("name"));
```

---

## 9) Java Serializable (legacy; avoid for new systems)

*(zero extra deps)*

```java
import java.io.*;
class User implements Serializable { String name; int age; User(String n,int a){name=n;age=a;} }
var u = new User("Ashfaq", 28);
try (var oos = new ObjectOutputStream(new FileOutputStream("u.ser"))) { oos.writeObject(u); }
try (var ois = new ObjectInputStream(new FileInputStream("u.ser"))) {
    var back = (User) ois.readObject(); System.out.println(back.name);
}
```

---

## 10) FlatBuffers (zero-copy, ultra-low latency)

**pom.xml**

```xml
<dependency>
  <groupId>com.google.flatbuffers</groupId><artifactId>flatbuffers-java</artifactId><version>24.3.25</version>
</dependency>
```

**user.fbs**

```fbs
namespace demo.fb;
table User { name:string; age:int; }
root_type User;
```

Generate Java with `flatc --java user.fbs`, then:

```java
var b = new com.google.flatbuffers.FlatBufferBuilder(128);
int name = b.createString("Ashfaq");
demo.fb.User.startUser(b); demo.fb.User.addName(b, name); demo.fb.User.addAge(b, 28);
int end = demo.fb.User.endUser(b); b.finish(end);
java.nio.ByteBuffer buf = b.dataBuffer();          // serialized
var u = demo.fb.User.getRootAsUser(buf);           // "deserialize" (no copy)
System.out.println(u.name()+"-"+u.age());
```

---

## When to reach for which (quick chooser)

* **Streaming / Kafka** → Avro (schema evolution, compact)
* **Analytics / Data Lakes** → Parquet / ORC (columnar, compressed)
* **Service-to-Service RPC** → Protobuf (or Thrift)
* **In-memory / Spark checkpoint** → Kryo
* **Public APIs / Debuggable** → JSON (Jackson)
* **MongoDB** → BSON
* **Ultra-low latency** → FlatBuffers
* **Legacy only** → Java Serializable

---

## Minimal project structure (one-by-one testing)

```
/formats-lab
  /avro      (pom.xml, user.avsc, Main.java)
  /parquet   (pom.xml, Main.java)
  /orc       (pom.xml, Main.java)
  /protobuf  (pom.xml, user.proto, Main.java)
  /thrift    (pom.xml, user.thrift, Main.java)
  /kryo      (pom.xml, Main.java)
  /jackson   (pom.xml, Main.java)
  /bson      (pom.xml, Main.java)
  /java-ser  (pom.xml, Main.java)
  /flatbuf   (pom.xml, user.fbs, Main.java)
```

> Build each with `mvn -q -DskipTests package` and run the main class from your IDE or `exec-maven-plugin`. For Protobuf/Thrift/FlatBuffers, run their codegens once (`protoc`, `thrift`, `flatc`) before compiling.

---

**Pro tips (tradeoffs):**

* **Schema evolution**: Avro/Protobuf/Thrift handle additive changes well; JSON needs discipline; Java Serializable is brittle.
* **Speed/size**: FlatBuffers ≈ Protobuf > Kryo (fast in JVM) > Avro > JSON > Java Serializable.
* **Query efficiency**: Parquet/ORC win for analytics; they’re storage/query formats, not wire formats.

---
