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

---

## 💻 Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.x
- Spring MVC (REST API)
- Spring Data JPA
- H2 In-Memory Database

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
