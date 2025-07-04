# 🔗 URLShortner_API

A minimal, fast, and extensible Spring Boot REST API that allows you to shorten long URLs into compact links, generate QR codes, and track analytics like clicks, IP address, browser, OS, and location.

---

## 🚀 Features

✅ Shorten long URLs  
✅ Auto-generate short codes  
✅ Generate QR Code (Base64) for each short URL  
✅ Redirect to original long URL  
✅ Track click analytics (IP, browser, OS, country)  
✅ Store and fetch data via H2 database  
✅ Swagger UI for testing endpoints  
✅ Error handling for missing/invalid URLs  
✅ Docker support for easy deployment
---

## 💻 Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.x
- Spring MVC (REST API)
- Spring Data JPA
- H2 In-Memory Database
- Docker

**Libraries:**
- Lombok – for boilerplate-free models
- ZXing – for QR Code generation
- Apache Commons Lang – for random string utilities
- Springdoc OpenAPI – for Swagger UI integration

**Dev Tools:**
- Maven
- IntelliJ IDEA
- Postman (for testing)
- Git & GitHub
- **Docker**

## 📸 Demo

```json
POST /shorten
Request:
{
  "longUrl": "https://www.youtube.com/"
}

Response:
{
  "shortUrl": "http://localhost:8080/Ab12Xz",
  "qrCodeBase64": "data:image/png;base64,iVBORw0KGg..."
}
🗃️ Database Schema
URL_MAPPING
| id | long_url | short_code | created_at | click_count |

URL_CLICK
| id | short_code | clicked_at | ip | browser | os | country |

📜 License
This project is licensed under the MIT License.

🙌 Author
Balaji Vivek
GitHub: @balaji-vivek
⭐️ Show Some Love
If this project helped you, please consider giving it a ⭐️ on GitHub!

🐳 Docker Support
This project supports running via Docker for easy setup and deployment.

🔧 Build the Docker Image

Make sure your JAR is built first:
./mvnw clean package

Then build the Docker image:
docker build -t urlshortner-api .

 Run the Docker Container
docker run -p 8080:8080 urlshortner-api

This will expose the application on http://localhost:8080

📄 Dockerfile

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/urlshortner-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


