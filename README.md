# Personal Finance Manager API

A Spring Boot REST API for personal finance management, built using Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA, and H2.

## Features

- User registration, login, and logout using session-based authentication.
- User-specific transactions and transaction filtering.
- Default and custom income/expense categories.
- Savings goals with progress tracking.
- Monthly and yearly income and expense reports.
- Request validation and JSON error responses.
- H2 database for local persistence.
- Automated unit and integration tests.

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot 3.5
- **Security:** Spring Security, session-based authentication
- **Database:** H2
- **Persistence:** Spring Data JPA
- **Build Tool:** Maven

## Run Locally

### Prerequisites

- JDK 21
- Git

### Steps

Clone the repository:

```bash
git clone https://github.com/Architjohri7/personal-finance-manager.git
cd personal-finance-manager
```

Run the application:

```bash
./mvnw spring-boot:run
```

The API runs at:

`http://localhost:8080`

### Health Check

Send a GET request to:

`http://localhost:8080/api/health`

### Run Tests

Run the automated test suite:

```bash
./mvnw clean verify
```

The latest local test run completed successfully with **57 tests and 0 failures**.

## Deployment

The application is deployed on Render.

**Live Health Endpoint:**  
https://personal-finance-manager-u1e8.onrender.com/api/health

## API Endpoints

All endpoints except the health check, registration, and login require an authenticated session.

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Check API health |
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Log in |
| POST | `/api/auth/logout` | Log out |
| GET | `/api/categories` | List categories |
| POST | `/api/categories` | Create a custom category |
| DELETE | `/api/categories/{id}` | Delete a category |
| POST | `/api/transactions` | Create a transaction |
| GET | `/api/transactions` | List and filter transactions |
| PUT | `/api/transactions/{id}` | Update a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| POST | `/api/goals` | Create a savings goal |
| GET | `/api/goals` | List savings goals |
| GET | `/api/reports/monthly/{year}/{month}` | Generate a monthly report |
| GET | `/api/reports/yearly/{year}` | Generate a yearly report |

## Example Requests

### Register

`POST /api/auth/register`

```json
{
  "username": "you@example.com",
  "password": "password123",
  "fullName": "Your Name",
  "phoneNumber": "+911234567890"
}
```

### Log In

`POST /api/auth/login`

```json
{
  "username": "you@example.com",
  "password": "password123"
}
```

Save the session cookie returned after login and include it in subsequent authenticated requests.

### Create a Transaction

`POST /api/transactions`

```json
{
  "amount": 250.00,
  "date": "2026-10-12",
  "category": "Food",
  "description": "Lunch"
}
```

Transaction listing supports optional filters such as `startDate`, `endDate`, `categoryId`, and `type`.

### Create a Custom Category

`POST /api/categories`

```json
{
  "name": "Freelance",
  "type": "INCOME"
}
```

### Create a Savings Goal

`POST /api/goals`

```json
{
  "goalName": "Emergency Fund",
  "targetAmount": 5000,
  "targetDate": "2027-12-31"
}
```

### Reports

Monthly report: `/api/reports/monthly/2026/10`

Yearly report: `/api/reports/yearly/2026`

## Testing

### Code Coverage Report

The JaCoCo report below shows instruction and branch coverage for the `ApiController` class.

<img width="1016" height="156" alt="Screenshot 2026-10-14 at 9 41 50 PM" src="https://github.com/user-attachments/assets/7f7fea80-00fc-40dc-90d4-b6604d5bc38c" />

<img width="1480" height="882" alt="Screenshot 2026-10-14 at 9 42 16 PM" src="https://github.com/user-attachments/assets/baf541b8-2a00-458c-8bea-f22cc0e9262c" />



The project has automated unit and integration tests covering its API functionality, validation, and error handling.

The latest local verification run completed with 57 tests, 0 failures, and 0 errors. JaCoCo reported approximately 92% instruction coverage and 89% branch coverage.

**Note:** These are local test results. They do not represent the result of the assignment's separate external 86-test script.

## Database and Deployment Notes

The H2 file database is configured for local persistence. Database persistence on the deployed service depends on its production configuration and storage setup.

The H2 console should remain disabled in production. Production deployments should use appropriately configured persistent storage and secure environment-specific settings.
