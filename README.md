# innovatrix

## Team Members
- SabaJhim (Team Leader)
- Swastika27
- mstmetaly

## Mentor
- moonwarnishan

## Project Description
Add your project description here.

## Running the code
1. Clone the repository
2. Install java (Java 17 or later), if not already installed
3. Install maven (optional)
4. Install PostGreSQL
5. Install PostGIS
6. Create a database -> log into that database -> enable PostGIS
```psql shell
CREATE DATABASE your_database_name;
\c your_database_name
CREATE EXTENSION postgis;
```
7. create a dotenv file in the root directory. The file should have following format
```env
#DB
DEV_DB_URL=jdbc:postgresql://localhost:port/your_database_name # port should be 5432 if not changed
DEV_DB_USER=your_user_name
DEV_DB_PASSWORD=your_password

# REDIS server
DEV_REDIS_HOST=your_redis_host_name # localhost if not changed
DEV_REDIS_PORT=your_redis_port
```

## Development Guidelines
1. Create feature branches
2. Make small, focused commits
3. Write descriptive commit messages
4. Create pull requests for review

## Resources
- [Project Documentation](docs/)
- [Development Setup](docs/setup.md)
- [Contributing Guidelines](CONTRIBUTING.md)
