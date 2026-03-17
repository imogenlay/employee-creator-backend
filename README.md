# Employee Creator Spring Application

- [![Backend Tests](https://github.com/imogenlay/employee-creator-backend/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/imogenlay/employee-creator-backend/actions/workflows/test.yml)

- [Frontend Repository](https://github.com/imogenlay/employee-creator-frontend)
- [Backend Repository](https://github.com/imogenlay/employee-creator-backend)

## Purpose

This application is a Spring Boot (Java) backend for a full-stack employee management tool. It exposes a RESTful API that enables full CRUD (Create, Read, Update, Delete) interactions with a MySQL database, serving as the data layer for a frontend client.

Employees and their associated contracts can be created, retrieved, updated, and deleted through clearly defined API endpoints. The application also features an integrated AI chat interface powered by Claude, allowing users to query and interact with employee data conversationally through an intelligent agent.

---

## Build Steps

### Prerequisites

- Java 17+
- Maven or Gradle
- MySQL instance running locally or remotely

### Running the Application

1. Clone the repository:

```bash
   git clone https://github.com/imogenlay/employee-creator-backend.git
   cd employee-creator-backend
```

2. Configure your database connection by making a .env file:

```bash
   cd touch .env
```

```env
DB_PORT=
DB_HOST=
DB_NAME=
DB_USER=
DB_PASSWORD=
CLAUDE_API_NAME=
CLAUDE_API_KEY=
```

3. Build and run:

```bash
   ./mvnw spring-boot:run
```

Or with Gradle:

```bash
   ./gradlew bootRun
```

The application will start on `http://localhost:8080` by default, or whatever port was set in the environment variables.

---

## Features

### CRUD Operations

Full create, read, update, and delete support for:

- Employees - manage personal details, roles, and employment status
- Contracts - create and edit contracts associated with individual employees

### AI Chat Agent

An integrated Claude-powered agent allows users to interact with the database through natural language. The agent has access to a set of tools that map to the underlying API, enabling it to fetch employee records, look up contract details, and assist with data queries conversationally.

### End-to-End Testing

The project includes a suite of end-to-end tests built with JUnit and RestAssured, covering key API flows to ensure reliability across create, read, update, and delete operations.

---

## Future Goals

- Employee Calendar: Introduce a calendar view where employee assignments and scheduling can be tracked and managed over time.
- AI database edits: Currently, the AI can only read from the database, allowing it to make edits would be useful.
