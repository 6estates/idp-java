6Estates idp-java
===================
A Java SDK for communicating with the 6Estates Intelligent Document Processing(IDP) Platform.

## Documentation

The documentation for the 6Estates IDP API can be found [here][apidocs].


### Supported Java Versions

This library supports the following Java implementations:

* OpenJDK 11
* OracleJDK 11


## Installation

### Adding idp-java with Maven.

Use the following dependency in your project to grab via Maven:

```
   <dependency>
      <groupId>com.sixestates</groupId>
      <artifactId>idp-sdk</artifactId>
      <version>0.0.1</version>
      <scope>compile</scope>
  </dependency>
```

### Obtaining the latest Jar. 

Please download the jars from [latest version](https://github.com/6estates/idp-java/releases/tag/0.0.1).

### Building from source code 

The following is how you can build the jar from source code,

    $ git clone https://github.com/6estates/idp-java.git
    $ cd idp-java
    $ mvn install       # Requires maven, download from https://maven.apache.org/download.html

If you want to build your own .jar, execute the following from within the cloned directory:

    $ mvn install

If you run into trouble with local tests, use:

    $ mvn install -Dmaven.test.skip=true

## Quickstart

### Initialize the 6Estates IDP Client

``` java

import com.sixestates.Idp;

// Please obtain your access token from 6Estates in advance
String yourAccessToken = "XXXXXX"; 

Idp.init(yourAccessToken);
```

### Submit a Task

#### Submit a Local File 
``` java
String fileName = "xxx.pdf";
String filePath = "/home/Documents/xxx.pdf" ;
String fileType = "CBKS";

try {
    TaskInfo taskInfo = TaskInfo.builder()
        .fileName(fileName)
        .filePath(filePath)
        .fileType(fileType)
        .build();
        
    TaskDTO taskDto = ExtractSubmitter.submit(taskInfo);
    
    System.out.println("taskId: " + taskDto.getData());
    }catch (final ApiException | ApiConnectionException e) {
        System.err.println(e);
}
```

#### Submit a File with InputStream 

``` java
FileInputStream fis = null;
try {
    fis = new FileInputStream(FILE_PATH);

    TaskInfo taskInfo = TaskInfo.builder()
            .fileName("xxx.pdf")
            .inputStream(fis)
            .fileType("CBKS")
            .build();

    TaskDTO taskDto = ExtractSubmitter.submit(taskInfo);
    
    System.out.println("taskId: " + taskDto.getData());
    fis.close();
}catch(Exception e) {
    System.out.println(e);
}finally {
    if(fis!=null) fis.close();
}
```

### Query The Extraction Result with the Task ID

``` java
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

