# Keycloak Admin Client App

This repository contains a Keycloak Admin Client application that implements Keycloak's administrative methods for managing users, groups, roles, and other Keycloak-related features programmatically.

## Features
- Manage users: create, update, delete, assign roles.
- Manage groups: create, update, delete, assign users.
- Manage roles: create, update, assign roles to users or groups.
- Programmatic access to Keycloak's admin REST API.

## Prerequisites
- Java 17 or later.
- Maven or Gradle installed.
- A running Keycloak instance.
- Docker (optional, for running Keycloak locally).

## Setup
### Clone the Repository
```bash
git clone https://github.com/your-username/keycloak-admin-client.git
cd keycloak-admin-client
```

### Configuration
Update the `application.yml` file with your Keycloak server details:

```yaml
keycloak:
  server-url: http://localhost:8080/auth
  realm: master
  client-id: admin-cli
  client-secret: YOUR_CLIENT_SECRET
  username: admin
  password: YOUR_PASSWORD
```

To avoid exposing sensitive credentials, follow the steps in [How to hide sensitive credentials](#how-to-hide-sensitive-credentials).

### Building the Application
Using Maven:
```bash
mvn clean install
```

Using Gradle:
```bash
gradle build
```

### Running the Application
```bash
java -jar target/keycloak-admin-client.jar
```

## API Usage
### Example: Create a User
Use the following code snippet to create a new user:

```java
UserRepresentation user = new UserRepresentation();
user.setUsername("newuser");
user.setEmail("newuser@example.com");
user.setEnabled(true);

keycloakAdminClient.createUser(user);
```

## How to Hide Sensitive Credentials
1. Use environment variables for sensitive configurations:
   ```yaml
   keycloak:
     server-url: ${KEYCLOAK_SERVER_URL}
     realm: ${KEYCLOAK_REALM}
     client-id: ${KEYCLOAK_CLIENT_ID}
     client-secret: ${KEYCLOAK_CLIENT_SECRET}
     username: ${KEYCLOAK_USERNAME}
     password: ${KEYCLOAK_PASSWORD}
   ```
2. Add `application.yml` to `.gitignore` and push a sample `application-sample.yml` file.

## Running Keycloak Locally (Optional)
Use Docker to run a local Keycloak instance:
```bash
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
```

## Contribution
1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Push to your fork and submit a pull request.

## License
This project is licensed under the [MIT License](LICENSE).

## Acknowledgments
- [Keycloak Documentation](https://www.keycloak.org/docs/latest/)
- [Keycloak Admin REST API](https://www.keycloak.org/docs-api/)
