# Idp-java


## Documentation

The documentation for the Idp API can be found [here][apidocs].



### Supported Java Versions

This library supports the following Java implementations:

* OpenJDK 11
* OracleJDK 11


## Installation

Idp-java uses Maven. 

Use the following dependency in your project to grab via Maven:

```
         <dependency>
            <groupId>com.sixestates.sdk</groupId>
            <artifactId>sixestates-sdk</artifactId>
            <version>1.0</version>
            <scope>compile</scope>
        </dependency>
```


If you want to compile it yourself, here's how:

    $ git clone https://github.com/6estates/idp-java.git
    $ cd idp-java
    $ mvn install       # Requires maven, download from https://maven.apache.org/download.html

If you want to build your own .jar, execute the following from within the cloned directory:

    $ mvn package

If you run into trouble with local tests, use:

    $ mvn package -Dmaven.test.skip=true

## Quickstart

### Initialize the Client

```java
// Find your Token at https://idp-sea.6estates.com/

String Token = "XXXXXX";

Idp.init(Token);
```

### Submit a Task

```java
String FILE_NAME = "acount_statement_mandiri.pdf";
String FILE_PATH = "/home//lay/Documents/acount_statement_mandiri.pdf" ;
String FILE_TYPE = "CBKS";

try {
    TaskInfo taskInfo = TaskInfo.builder()
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .fileType(FILE_TYPE)
        .build();
    taskDto = ExtractSubmitter.submit(taskInfo);
    
    System.out.println("taskId: " + taskDto.getData());
    }catch (final ApiException | ApiConnectionException e) {
        System.err.println(e);
}
```

### Extract the result

```java
if(taskDto.getStatus() == 200){
    try{
        String respJson=ResultExtractor.extractResultByTaskid(taskDto.getData());
        System.out.println(respJson);
    }catch(ApiException e){
        System.err.println(e);
    }
}
```

[apidocs]: https://idp-sea.6estates.com/docs#/

