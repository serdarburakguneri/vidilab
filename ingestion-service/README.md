# Ingestion Service

This project is a Spring Boot service for asynchronously storing files using the FileStorageService. The service accepts file uploads via HTTP and stores them in a specified directory.

## Features

Asynchronous file storage
Basic authentication

## Requirements

Java 17+
Maven 3.x+
Spring Boot 3.3.2+

## Getting Started

### Clone the Repository
```bash
   git clone https://github.com/yourusername/ingestion-service.git
   cd ingestion-service
```

### Build the Project
   Make sure you have Maven installed. You can then build the project by running:

```bash
mvn clean install
```

### Run the Application
   To start the Spring Boot application:

```bash
mvn spring-boot:run
```

### Configuration
   File Storage Path: You can specify the directory where the files will be stored by modifying the application.properties or providing a parameter like filePath in the request.
   Port Configuration: You can also configure the port in src/main/resources/application.properties:
   properties
```yml
   server.port=8080
```
##  API Endpoints

1. Health Check Endpoint
   To check if the service is running:

```bash
curl -X GET http://localhost:8080/api/v1/health
```
Expected response:

```json
OK
```

2.File Upload Endpoint
   POST /api/v1/media/upload

This endpoint allows you to upload a file asynchronously. The file will be stored in the specified directory.

URL: http://localhost:8080/api/v1/media/upload
Method: POST
Headers:
Basic Auth required (username: admin, password: password).
Parameters:
file: The file to be uploaded.
filePath: The directory where the file will be stored.
Example curl Command:

```bash
curl -v -X POST "http://localhost:8080/api/v1/media/upload" \
-F "file=@/path/to/your/file.txt" \
-F "filePath=/uploads" \
-u admin:password
```
Response:

If the upload is successful, the service will return:

```json
{
"message": "File upload request received, processing."
}
```

## Authentication
   Basic authentication is required for the file upload endpoint. The default credentials are:

Username: admin
Password: password
You can modify these credentials in the SecurityConfig class.

## Running Tests

To run the unit tests, execute:

```bash
mvn test
```