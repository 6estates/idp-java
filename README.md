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

Please download the jars from [latest version](https://github.com/6estates/idp-java/releases).

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

### Recieve The Result from Callback

The IDP platform provides callback service. If the callback parameter is not empty, the IDP system will send a request containing the task status to the callback url, please see the [callback documentation][callbackdocs].

#### Submit a Task with Callback parameter
``` java
String fileName = "xxx.pdf";
String filePath = "/Documents/xxx.pdf" ;
String fileType = "CBKS";
String CALLBACK_URL = "http://xxx.com";
Int CALLBACK_MODE = 1;

TaskInfo taskInfo = TaskInfo.builder()
        .fileName(FILE_NAME)
        .filePath(FILE_PATH)
        .fileType(FILE_TYPE)
        .callback(CALLBACK_URL)
        .callbackMode(CALLBACK_MODE)
        .build();
taskDto = ExtractSubmitter.submit(taskInfo4);
System.out.println("taskId: " + taskDto.getData());
```

#### Start a CallBack SocketServer
``` java
import com.sixestates.rest.v1.CallBackSocketServer;

// Init a CallBackServer
CallBackSocketServer callBackServer = new CallBackSocketServer("localhost", 8080);
// Start the server asynchronously
callBackServer.asynStartServer();

// Wait a CallBack request 
ConcurrentHashMap<String, String> jsonMap = callBackServer.getJsonStrMap();
for (String taskId: jsonMap.keySet()) {
    System.out.println("taskId: " + taskId  + ": " + jsonMap.get(taskId));
}

ConcurrentHashMap<String, byte[]> fileBytesMap = callBackServer.getFileBytesMap();
for (String fileName: fileBytesMap.keySet()) {
    System.out.println("fileName: " + fileName);
    byte[] fileBytes =  fileBytesMap.get(fileName);
}

// Stop the server
callBackServer.stopServer();
```

#### CallBack Springboot Controller
``` java
@Controller
public class CallBackController {
  @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  @ResponseBody
  public ResponseEntity mode0or1CallBack(@RequestBody String jsonStr) {
      System.out.println("Receive a callback request!");
      System.out.println("Callback body: " + jsonStr); 
      return ResponseEntity.ok(200);
  }

  @RequestMapping(value = "/mode2")
  @ResponseBody
  public ResponseEntity mode2CallBack(@RequestParam("file") MultipartFile file, @RequestParam("result") MultipartFile json) {
      try {
          System.out.println("Receive a callback request!");
          // Receive the result json file
          String jsonStr = new String(json.getBytes());
          System.out.println("Callback body: " + jsonStr);

          // Receive the file bytes
          System.out.println("Callback fileName: " + file.getOriginalFilename());
          Bytes[] fileBytes = file.getBytes());
          return ResponseEntity.ok(200);
          } catch (IOException e) {
              e.printStackTrace();
              return ResponseEntity.ok(500);
          }
  }
}
```

[apidocs]: https://idp-sea.6estates.com/docs#/
[callbackdocs]: https://idp-sea.6estates.com/docs#/extract/extraction?id=_212-callback-process


