# Personal Finance Manager API

A Spring Boot REST API for personal finance tracking, built with Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA and H2.

## Features
- Email/password registration and login using server-side sessions and cookies
- User-specific transactions, custom categories, and savings goals
- Monthly and yearly income/expense reports
- Request validation and JSON error responses
- H2 file database for local persistence

## Run locally
Prerequisites: JDK 21 and Maven (or use the included Maven Wrapper if provided by your IDE/project).

```bash
./mvnw spring-boot:run
```
If the wrapper is not present, use `mvn spring-boot:run`.

Health check: `GET http://localhost:8080/api/health`

## API quick start
All endpoints except health, registration and login require a session cookie.

1. `POST /api/auth/register` JSON: `{"username":"you@example.com","password":"password123","fullName":"Your Name","phoneNumber":"+911234567890"}`
2. `POST /api/auth/login` JSON: `{"username":"you@example.com","password":"password123"}`. Save/send the returned `JSESSIONID` cookie on later requests.
3. `GET /api/categories`
4. `POST /api/transactions` JSON: `{"amount":250.00,"date":"2026-10-12","category":"Food","description":"Lunch"}`
5. `GET /api/transactions`, optionally with `startDate`, `endDate`, `categoryId`, and `type=INCOME|EXPENSE`
6. `POST /api/goals` JSON: `{"goalName":"Emergency Fund","targetAmount":5000,"targetDate":"2027-12-31"}`
7. `GET /api/reports/monthly/2026/10` or `GET /api/reports/yearly/2026`
8. `POST /api/auth/logout`

Custom category JSON: `{"name":"Freelance","type":"INCOME"}`. Goal updates accept `targetAmount` and `targetDate`.

## Important notes before submission
- This ZIP is a starting implementation and has not been validated against the assignment's external 86-test script or deployed to a public URL.
- Run the app and exercise all endpoints locally; add comprehensive unit/integration tests and verify the required coverage target before submitting.
- The assignment also asks for a public GitHub repository, a live deployment URL, and a screenshot of the external test report. Those steps must be completed separately.
- The H2 console is enabled for local development only. Disable it before a public production deployment and configure a persistent production database.
