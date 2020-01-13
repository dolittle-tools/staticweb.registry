# SPA(Single Page Application) Registry

This project provides a solution for an artifact registry. The underlying store is Azure Storage(Blobs)
The application was coded in Java using Spring Boot and Apache Camel frameworks.

### Usage:

##### Configuration
* Look at application.properties file under resources folder
  * Fill in the Azure properties with your credentials/keys

### Build:
```
mvn clean package
```

### Run:
```
java -jar target/spa-registry-1.0.0.jar
```

### Running as a docker container:
```
docker build -t <image_tag> . && docker run -p 8080:8080 <image_tag>
```

### REST-API:
* /upload/{ms-id}/{tag}

#### curl commands:
```
curl -i -X PUT -F 'data=@spa.zip' -H "Tenant-ID: 5680" http://localhost:8080/upload/1234/1.0
```
