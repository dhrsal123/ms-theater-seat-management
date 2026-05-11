# Theater Seat Management API
A microservice designed to manage the physical infrastructure of the cinema ecosystem, 
including theater locations, operating hours, rooms, and seat configurations.

## Key features
* Theater Lifecycle Management: Create, update, and manage cinema theaters and their locations.
* Operating Hours: Configure schedules for every day of the week with bulk update support.
* Room & Seat Mapping: Organize theaters into rooms with different seat-level configurations (Standard, Premium, Wheelchair, etc.).
* Role-Based Security: Secured with JWT-based authentication, restricting administrative tasks to the `MANAGER` role.
* Standardized Error Handling: Unified error response format for consistent frontend integration.

## Prerequisites
* Java 21
* Docker & Docker Compose
* Keycloak

## Environment configuration
Create a `.env` file in the root directory based on the example below:

```bash
# --- Redis Config ---
REDIS_HOST=localhost
REDIS_PASSWORD=YOUR_REDIS_PASSWORD

# --- DB Config ---
POSTGRES_DB=cinema_db
POSTGRES_HOST=localhost
POSTGRES_PASSWORD=YOUR_POSTGRES_PASSWORD
POSTGRES_USER=postgres

# --- Keycloak ---
KEYCLOAK_HOST=localhost

# --- LOKI ---
LOKI_HOST=localhost
```

---

## API Documentation
Once the service is running, you can explore the full API specification:
* Swagger UI: `http://localhost:8082/swagger-ui.html`
* OpenAPI Spec: `http://localhost:8082/v3/api-docs`

---

## Usage
### Role-Based Access Control
This service distinguishes between public information and management operations. Management endpoints require a valid JWT with the `Manager` authority.

#### Endpoint Examples
| Category | Method | Path | Required Role |
| :--- | :--- | :--- | :--- |
| **Theaters** | `GET` | `/api/v1/theaters` | None |
| **Theaters** | `POST` | `/api/v1/theaters` | `MANAGER` |
| **Rooms** | `DELETE` | `/api/v1/theaters/{id}/rooms/{rid}` | `MANAGER` |
| **Seats** | `GET` | `/api/v1/theaters/{id}/seats` | None |

### Seat Types
The system supports specialized seat mapping to enhance the booking experience:
* `STANDARD`, `PREMIUM`, `COUPLE_SEAT`
* `WHEELCHAIR_ACCESSIBLE`, `COMPANION`, `EASY_ACCESS`

---

## Error Handling
The service returns a standardized `BaseErrorDto` for all business and technical exceptions:

```json
{
  "message": "The requested theater, room, or seat could not be found.",
  "code": "CINEMA-001",
  "status": "BAD_REQUEST"
}
```

### Common Error Codes
* `CINEMA-001`: Resource Not Found / Validation Fail
* `CINEMA-003`: Data Conflict
* `CINEMA-004`: Forbidden (Missing Roles)
* `CINEMA-005`: Unauthorized (Expired or Missing Token)