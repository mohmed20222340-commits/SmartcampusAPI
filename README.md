SmartCampusAPI - JAX-RS RESTful Service

# Overview
A RESTful API built with JAX-RS (Jersey) for managing Rooms and Sensors in a Smart Campus environment. The API supports full CRUD operations, sensor reading history, and robust error handling.

# Technology Stack
- Java 11
- JAX-RS (Jersey 2.41)
- Apache Tomcat 9
- Jackson (JSON)
- Maven

# How to Build and Run

## Prerequisites
- Java JDK 11
- Apache Maven
- Apache Tomcat 9
## Steps
1. Clone the repository:
git clone https://github.com/YOURUSERNAME/SmartCampusAPl.git
2. Open in NetBeans as Maven Web Application
3. Right-click project → Clean and Build
4. Right-click project → Run (deploys to Tomcat)
5. API runs at:
http://localhost:8080/SmartCampusAPl/api/v1/

## API Endpoints
| Method | URL | Description |

GET |/api/v1/ | Discovery endpoint |
GET |/api/v1/rooms | Get all rooms |
POST | /api/v1/rooms | Create a room / GET | /api/v1/rooms/{id} | Get room by ID |
DELETE | /api/v1/rooms/fid Delete a room
| GET | /api/v1/sensors | Get all sensors
 | POST |/api/v1/sensors | Create a sensor |
| GET |/api/v1/sensors/[id} | Get sensor by ID |
| GET |/api/v1/sensors/{id)/readings | Get reading history |
| POST |/api/v1/sensors/{id)/readings | Add new reading |

## Sample curl Commands

### 1. Discovery
curl http://localhost:8080/SmartCampusAPl/api/v1/

### 2. Create a Room curl -X POST http:///ocalhost:8080/SmartCampusAPl/api/v1/rooms \
-H "Content-Type: application/json" \
-d '"id": "HALL-01","name":"Main Hall", "capacity": 100}

### 3. Create a Sensor curl -X POST http://localhost:8080/SmartCampusAPl/api/v1/sensors |
-H "Content-Type: application/json" \
-d '"id": "CO2-001" "type" "CO2", "status": "ACTIVE", "currentValue": 400.0, "roomld": "ENG-201"}'

### 4. Filter Sensors by Type
curi http://ocalhost:8080/SmartCampusAPl/api/v1/sensors?type=CO2

### 5. Add a Reading curl -X POST http://localhost:8080/SmartCampusAPl/api/v1/sensors/CO2-001/readings |
-H "Content-Type: application/json" |
-d '["value":520.5}'

### 6. Get Reading History
curl http://ocalhost:8080/SmartCampusAPl/api/v1/sensors/CO2-001/readings

### 7. Delete Room with Sensors (409 Error)
curl -X DELETE http://localhost:8080/SmartCampusAPl/api/v1/rooms/ENG-201
### 8. Create Sensor with Invalid roomld (422 Error)
curl -X POST http://localhost:8080/SmartCampusAPl/api/v1/sensors \
-H "Content-Type: application/json" |
-d'{"id":"CO2-999", "type":"CO2", "status": "ACTIVE", "currentValue": 400.0,"roomld". "FAKE-999"y


## Report Answers

### Part 1.1 - JAX-RS Resource Lifecycle
By default, JAX-RS creates a new instance of each Resource class for every incoming HTTP request (request-scoped). This means instance variables cannot hold shared state between requests. To manage in-memory data across requests, a Singleton pattern is used via DataStore.getinstance(). Thread-safe ConcurrentHashMap is used instead of regular HashMap to prevent race conditions when multiple requests arrive simultaneously, ensuring no data is lost or corrupted

### Part 1.2 - HATEOAS
HATEOAS (Hypermedia As The Engine Of Application State) means API responses include links to related resources and actions. For example, a room response includes a link to its sensors. This allows client developers to discover available actions dynamically from the response itself, rather than relying on static documentation. It reduces coupling between client and server - if URLs change, clients following links automatically adapt without breaking.

### Part 2.1 - IDs vs Full Objects
Returning only IDs is bandwidth-efficient but forces clients to make additional requests for each room's details, creating the N+1 problem.
Returning full objects increases payload size but allows clients to render data immediately without extra round-trips. For large campuses with thousands of rooms, returning full objects with pagination is the best approach, reducing both latency and the total number of HTTP calls.

### Part 2.2 - DELETE Idempotency
Yes, DELETE is idempotent by HTTP specification. In this implementation, the first DELETE on an existing room returns 204 No Content. A second DELETE on the same room returns 404 Not Found. The server state is

### Part 2.1 - IDs vs Full Objects
Returning only IDs is bandwidth-efficient but forces clients to make additional requests for each room's details, creating the N+1 problem.
Returning full objects increases payload size but allows clients to render data immediately without extra round-trips. For large campuses with thousands of rooms, returning full objects with pagination is the best approach, reducing both latency and the total number of HTTP calls.

### Part 2.2 - DELETE Idempotency
Yes, DELETE is idempotent by HTTP specification. In this implementation, the first DELETE on an existing room returns 204 No Content. A second DELETE on the same room returns 404 Not Found. The server state is identical after both calls - the room is gone - so it is effectively idempotent in terms of outcome. The different status codes are acceptable per RFC 7231 and still considered idempotent behaviour.

### Part 3.1 - @Consumes and Content-Type Mismatch
The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS this endpoint only accepts application/json content. If a client sends Content-Type: text/plain or application/ml, JAX-RS automatically returns HTTP 415 Unsupported Media Type before the method is even called. This protects endpoints from malformed data without any manual checking in the code.

### Part 3.2 - @QueryParam vs Path Parameter
Using @QueryParam for filtering is superior because query parameters are optional by nature - the same endpoint handles both filtered andunfiltered requests gracefully. A path-based approach like /sensors/type/CO2 implies the type is a required structural part of the resource identity, which is semantically wrong when filtering a collection. Query params also allow combining multiple filters easily such as ?lype=CO2&status=ACTIVE.

### Part 4.1 - Sub-Resource Locator Benefits
The Sub-Resource Locator pattern delegates responsibility to dedicated classes. Instead of one massive SensorResource handling both sensors and readings, SensorReading Resource has a single focused concern. This improves maintainability, testability and readability. In large APls with deep nesting, this pattern prevents God Classes and keeps each class focused on one responsibility, following the Single Responsibility Principle.

### Part 5.2 - Why 422 over 404
A 404 Not Found is for when the requested URL does not exist. When a client POSTs a sensor with a roomid that does not exist, the URL /api/v1/sensors is perfectly valid. HTTP 422 Unprocessable Entity is more accurate because it signals the request was syntactically correct JSON, but the semantic content - the referenced roomid - cannot be processed. Using 404 would confuse clients into thinking the sensors endpoint itself is missing.

### Part 5.4 - Stack Trace Security Risks
Exposing Java stack traces is a serious security vulnerability for three reasons. First, internal file paths reveal the server directory structure helping attackers map the system. Second, library names and versions stich as lersev 2 41 allow attackers to search CVF databases
