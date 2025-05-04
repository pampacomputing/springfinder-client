# SpringFinder Client

A Spring Boot application that connects to an external API to look up users by CPF, name, or date of birth and displays the matching records from the server database.

## Table of Contents

- [Features](#features)  
- [Prerequisites](#prerequisites)  
- [Getting Started](#getting-started)  
- [Configuration](#configuration)  
- [Usage](#usage)  
- [API Endpoints](#api-endpoints)  
- [Example Requests](#example-requests)  
- [Contributing](#contributing)  
- [License](#license)  

## Features

- Send lookup requests by CPF, name, or date of birth  
- Receive and display user records from an external service  
- Simple, responsive HTML front-end powered by Thymeleaf  
- Server-side pagination for browsing requests  

## Prerequisites

- Java 21 or higher  
- Maven 3.6+  
- Internet access to reach the external API  

## Getting Started

1. **Clone the repository**  
   ```bash
   git clone https://github.com/your-username/springfinder-client.git
   cd springfinder-client

2. **Build the project**

   ```bash
   mvn clean package
   ```

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

   By default, the server will start on `http://localhost:8080`.

## Configuration

All API connection settings are managed in `src/main/resources/application.properties`:

```properties
# External API base URL
external.api.url=https://api.example.com/user-info

```

Customize these values to point to your target service.

## Usage

1. Navigate to `http://localhost:8080/`.
2. Enter a CPF, name, or date of birth in the form.
3. Submit the form to send the lookup request.
4. Click **“Responses”** in the navigation to view past requests and results, with pagination controls.

## API Endpoints

| Method | Path         | Description                              |
| ------ | ------------ | ---------------------------------------- |
| GET    | `/`          | Main page with lookup form               |
| POST   | `/send`      | Send lookup request (form submission)    |
| GET    | `/responses` | View paginated list of past lookup calls |

## Example Requests

**Lookup by CPF**

```bash
curl -X POST http://localhost:8080/send \
  -d cpf=12345678900
```

**Lookup by Name**

```bash
curl -X POST http://localhost:8080/send \
  -d name="John Doe"
```

**View Responses (page 2)**

```bash
curl http://localhost:8080/responses?page=1
```

## Contributing

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m "Add some feature"`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

Please follow the existing code style and include tests where appropriate.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

```
```
****