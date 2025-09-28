@echo off
echo Building the project...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo Maven build failed. Exiting.
    exit /b %errorlevel%
)

echo Checking for .env file...
if not exist .env (
    echo .env file not found. Please create one based on example.env
    echo Exiting.
    exit /b 1
)

echo Starting the application...
java -jar target\quan-ly-cua-hang-xe-1.0-SNAPSHOT.jar
