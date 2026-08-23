# Spring Boot with Flyway - Database Migration Demo

A practical example of using Flyway with Spring Boot 3 for database version control and migration management.

## What is Flyway?

Flyway is an open-source database migration tool that brings version control to your database schema. Created by Axel Fontaine in 2010, it lets you manage database changes as code, similar to how you manage application code with Git.

The tool was acquired by Redgate in 2013 and has become one of the most popular database migration tools in use today.

### The Problem Flyway Solves

Managing database schema changes used to be messy. Here are the main issues developers faced:

#### Problem 1: Manual DDL Scripts

The old way of doing things:
- Developers manually ran SQL scripts in the database
- Scripts lived in shared folders, emails, or random documentation
- No version history - you had no idea what changed, when, or who did it
- No tracking - couldn't tell which scripts ran in which environment
- Team sync issues - everyone's database looked different
- Production risks - manual execution was error-prone
- No rollback - undoing changes was a nightmare

Example:
```sql
-- Someone runs this manually
CREATE TABLE users (...);
-- No record of this change!
-- Other devs don't know about it
-- Production might be missing it entirely
```

#### Problem 2: JPA/Hibernate Auto-DDL

Using `spring.jpa.hibernate.ddl-auto=update` or `create-drop`:

Problems:
- No version control - changes happen automatically, no tracking
- Too volatile - schema changes based on entity changes, no review
- No migration history - can't see what changed or when
- Production unsafe - auto-DDL is dangerous in production
- Team issues - different devs have different entity states
- Data loss risk - `create-drop` wipes everything on restart
- No control - changes happen automatically, can't review first
- Environment drift - dev, staging, and production diverge

Example:
```java
@Entity
public class User {
    private String email;  // Added new field
}
// Hibernate adds column automatically - no record, no control!
```

### How Flyway Solves These Problems

Flyway treats your database schema as code with proper versioning:

- Every change is a versioned migration file
- All migrations are tracked in the `flyway_schema_history` table
- Migrations run on application startup
- Everyone gets the same database schema
- Environments stay in sync
- Changes are code-reviewed before deployment
- Complete audit trail of who changed what and when
- Safe production deployments with tested migrations

**Important: No manual SQL execution needed.** Just put your migration files in `src/main/resources/db/migration/` and start the app. Flyway handles the rest.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (for PostgreSQL)

## Quick Start

### 1. Start PostgreSQL Database

Make sure your Docker container is running:

```bash
docker run -d --name payment-postgres \
  -e POSTGRES_DB=sampledb \
  -e POSTGRES_USER=sampleuser \
  -e POSTGRES_HOST_AUTH_METHOD=trust \
  -p 5432:5432 \
  postgres:latest
```

### 2. Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Verify Flyway Migrations

You don't need to manually run any SQL scripts. When the application starts, Flyway will:

1. Create the `flyway_schema_history` table (if it doesn't exist)
2. Scan for migration files in `src/main/resources/db/migration/`
3. Execute any pending migrations in version order (V1 → V2 → V3 → V4)
4. Record each migration in `flyway_schema_history`
5. Log the migration status in the console

You'll see output like:
```
Flyway Community Edition 9.x.x by Redgate
Database: jdbc:postgresql://localhost:5432/sampledb
Successfully validated 4 migrations (execution time 00:00.012s)
Current version of schema "temp": 4
Migrating schema "temp" to version "1 - Create users table"
Migrating schema "temp" to version "2 - Create payments table"
...
```

That's it. Your database schema is ready.

## Project Structure

```
flyway/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/flyway/
│   │   │       ├── FlywayDemoApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── UserController.java
│   │   │       │   └── PaymentController.java
│   │   │       ├── model/
│   │   │       │   ├── User.java
│   │   │       │   └── Payment.java
│   │   │       └── repository/
│   │   │           ├── UserRepository.java
│   │   │           └── PaymentRepository.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__Create_users_table.sql
│   │               ├── V2__Create_payments_table.sql
│   │               ├── V3__Add_user_status_column.sql
│   │               └── V4__Insert_initial_data.sql
│   └── test/
└── pom.xml
```

## What We've Built

This project demonstrates a payment platform database schema managed with Flyway. Here's what's included:

### **Database Schema Overview**

We've created a payment platform with the following structure:

1. **Users Table** (`temp.users`)
   - Stores user account information
   - Fields: id, username, email, first_name, last_name, status, phone_number, timestamps
   - Indexes on email, phone_number, username, name (composite), created_at, updated_at, status+created_at

2. **Payments Table** (`temp.payments`)
   - Stores payment transaction records
   - Foreign key relationship to users table
   - Fields: id, user_id, amount, currency, status, payment_method, transaction_id, description, fee_amount, timestamps
   - Multiple indexes for query optimization: user_id, status, created_at, payment_method, currency, amount, updated_at, and composite indexes

### **Migration History**

Our migration journey demonstrates incremental database evolution:

| Version | Migration | Description |
|---------|-----------|-------------|
| **V1** | `V1__Create_users_table.sql` | Initial schema - creates users table with basic fields |
| **V2** | `V2__Create_payments_table.sql` | Adds payments table with foreign key to users |
| **V3** | `V3__Add_user_status_column.sql` | **Incremental change** - adds status column to existing users table |
| **V4** | `V4__Insert_initial_data.sql` | Seeds database with sample users and payment records |
| **V5** | `V5__Add_phone_number_to_users.sql` | **Incremental change** - adds phone_number column to users table |
| **V6** | `V6__Add_description_and_fee_to_payments.sql` | **Incremental change** - adds description and fee_amount columns to payments table |
| **V7** | `V7__Add_additional_indexes.sql` | **Performance optimization** - adds multiple indexes for better query performance |

### Key Features

- **Schema Versioning**: Each change is a numbered migration (V1 through V7)
- **Incremental Changes**: V3, V5, V6 show how to modify existing tables
- **Data Migrations**: V4 demonstrates inserting initial data
- **Performance Optimization**: V7 adds indexes for better query performance
- **Schema Isolation**: All tables in `temp` schema (not default `public`)
- **Foreign Keys**: Payments table references users table
- **Indexes**: Strategic indexes on commonly queried columns
- **Explicit Schema**: Each migration sets `SET search_path TO temp;`

### Application Layer

The project includes a complete Spring Boot application:

- JPA entities (`User` and `Payment`) mapped to database tables
- Spring Data JPA repositories for data access
- REST controllers for users and payments
- JPA configured to validate schema (not generate it)

This demonstrates proper database versioning, team collaboration, production-ready migrations, and incremental schema evolution.

## Flyway Configuration

Key configuration in `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/sampledb?currentSchema=temp
spring.datasource.username=sampleuser
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.properties.hibernate.default_schema=temp

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.schemas=temp
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
spring.flyway.validate-on-migrate=true
spring.flyway.clean-disabled=true
```

**Configuration notes:**
- `spring.datasource.url` - Includes `?currentSchema=temp` to set default schema
- `spring.jpa.properties.hibernate.default_schema=temp` - JPA uses `temp` schema
- `spring.flyway.enabled=true` - Enables Flyway migrations
- `spring.flyway.locations` - Where to find migration scripts
- `spring.flyway.schemas=temp` - Flyway uses `temp` schema instead of `public`
  - Creates the schema if it doesn't exist
  - All migrations run in the `temp` schema context
  - The `flyway_schema_history` table is created in `temp`
- `spring.flyway.baseline-on-migrate=true` - Creates baseline if database is not empty
- `spring.flyway.validate-on-migrate=true` - Validates migrations haven't been modified
- `spring.flyway.clean-disabled=true` - Prevents accidental database drops

## Migration Scripts

Flyway uses a **naming convention** for migration files:

```
V{version}__{description}.sql
```

Examples in this project:
- `V1__Create_users_table.sql` - Version 1, creates users table
- `V2__Create_payments_table.sql` - Version 2, creates payments table
- `V3__Add_user_status_column.sql` - Version 3, adds status column
- `V4__Insert_initial_data.sql` - Version 4, inserts sample data
- `V5__Add_phone_number_to_users.sql` - Version 5, adds phone_number column
- `V6__Add_description_and_fee_to_payments.sql` - Version 6, adds description and fee_amount columns
- `V7__Add_additional_indexes.sql` - Version 7, adds performance indexes

**Important rules:**
- Version numbers must be sequential and unique
- Use double underscore (`__`) between version and description
- Migrations are immutable - never modify existing migration files
- Create new migrations for schema changes
- Explicitly set schema: Each migration should include `SET search_path TO temp;` at the beginning

## How Flyway Works

You don't need to manually run SQL scripts. Flyway executes all migration scripts when your Spring Boot application starts. Here's what happens:

### On Application Startup

When you run `mvn spring-boot:run`, Flyway:

1. Connects to your database using settings from `application.properties`

2. Checks for the `flyway_schema_history` table
   - Creates it if it doesn't exist (first time only)
   - This table tracks all applied migrations

3. Scans `src/main/resources/db/migration/` for migration files
   - Finds files matching `V{version}__{description}.sql`
   - Sorts them by version number (V1, V2, V3, V4...)

4. Compares migration files with `flyway_schema_history`
   - Identifies which migrations haven't been applied yet
   - Executes only pending migrations in sequential order
   - Each migration runs in its own transaction

5. Updates history
   - After each successful migration, inserts a record into `flyway_schema_history`
   - Records metadata: version, description, checksum, timestamp, etc.

6. Application continues
   - Once all migrations are complete, Spring Boot continues startup
   - Your application is ready to use the database

### Migration Execution Details

- Migrations run before your application code starts
- Execute in version order (V1 → V2 → V3 → V4)
- Each migration runs in a transaction
- Already-applied migrations are skipped (won't run twice)
- If a migration fails, it's rolled back and the application won't start

### How Flyway Maintains Migration History

Flyway maintains all migration information in the `flyway_schema_history` table. This table is automatically created and managed by Flyway - you never need to touch it manually.

#### The `flyway_schema_history` Table Structure:

```sql
CREATE TABLE flyway_schema_history (
    installed_rank INTEGER PRIMARY KEY,      -- Execution order (1, 2, 3, ...)
    version VARCHAR(50),                      -- Migration version (1, 2, 3, 4, ...)
    description VARCHAR(200),                 -- Migration description from filename
    type VARCHAR(20),                         -- Migration type (SQL, JAVA, etc.)
    script VARCHAR(1000),                     -- Full filename (V1__Create_users_table.sql)
    checksum INTEGER,                         -- Hash of migration content (for validation)
    installed_by VARCHAR(100),                -- Database user who ran the migration
    installed_on TIMESTAMP,                   -- When the migration was executed
    execution_time INTEGER,                   -- Execution time in milliseconds
    success BOOLEAN                           -- Whether migration succeeded
);
```

#### What Information is Stored:

| Column | Example Value | Purpose |
|--------|---------------|---------|
| `installed_rank` | 1, 2, 3, 4, 5, 6, 7 | Execution order (first migration = 1) |
| `version` | "1", "2", "3", "4", "5", "6", "7" | Version number from filename |
| `description` | "Create users table", "Add phone number to users" | Description from filename (after `__`) |
| `type` | "SQL" | Type of migration (SQL, JAVA, etc.) |
| `script` | "V1__Create_users_table.sql", "V5__Add_phone_number_to_users.sql" | Full migration filename |
| `checksum` | 1234567890 | Hash of file content (detects changes) |
| `installed_by` | "sampleuser" | Database user who executed it |
| `installed_on` | 2024-01-15 10:30:45 | Timestamp when migration ran |
| `execution_time` | 150 | Time taken in milliseconds |
| `success` | true | Whether migration completed successfully |

#### Example Migration History:

After running all migrations (V1 through V7), the table might look like this:

```
installed_rank | version | description                        | script                                    | installed_on          | success
---------------|---------|------------------------------------|-------------------------------------------|-----------------------|--------
1              | 1       | Create users table                 | V1__Create_users_table.sql                | 2024-01-15 10:30:45  | true
2              | 2       | Create payments table              | V2__Create_payments_table.sql             | 2024-01-15 10:30:46  | true
3              | 3       | Add user status column             | V3__Add_user_status_column.sql            | 2024-01-15 10:30:47  | true
4              | 4       | Insert initial data                | V4__Insert_initial_data.sql               | 2024-01-15 10:30:48  | true
5              | 5       | Add phone number to users          | V5__Add_phone_number_to_users.sql         | 2024-01-15 10:35:12  | true
6              | 6       | Add description and fee to payments| V6__Add_description_and_fee_to_payments.sql| 2024-01-15 10:35:13  | true
7              | 7       | Add additional indexes             | V7__Add_additional_indexes.sql           | 2024-01-15 10:35:14  | true
```

**Key Observations:**
- Each migration has a unique `installed_rank` showing execution order
- `version` matches the migration file number
- `description` is extracted from the filename (after `__`)
- `checksum` ensures the file hasn't been modified
- `installed_on` shows exactly when each migration ran
- `execution_time` helps identify slow migrations
- All show `success = true` indicating successful execution

### 4. **How Flyway Prevents Duplicate Executions:**

1. **Checksum Validation**: 
   - Flyway calculates a checksum (hash) of each migration file
   - Stores this checksum in `flyway_schema_history`
   - On subsequent startups, compares current file checksum with stored checksum
   - If they differ, Flyway **fails** (prevents accidental modifications)

2. **Version Tracking**:
   - Before running a migration, Flyway checks if that version exists in `flyway_schema_history`
   - If version exists and `success = true`, the migration is **skipped**
   - Only new versions (not in history) are executed

3. **Immutable Migrations**:
   - Once a migration is applied, its checksum is locked
   - Modifying an existing migration file will cause a checksum mismatch
   - Flyway will **refuse to start** until the issue is resolved
   - This enforces the rule: **never modify existing migrations**

### 5. **What Happens on Subsequent Starts:**

After the first run, when you restart your application:

1. Flyway connects and checks `flyway_schema_history`
2. Finds that V1, V2, V3, V4, V5, V6, V7 are already applied
3. **Skips all existing migrations** (no re-execution)
4. If you add `V8__New_migration.sql`, only V8 will run
5. Application starts normally

**Example Flow:**
- **First run**: Applies V1-V4, creates initial schema
- **Second run** (after adding V5-V7): Applies V5, V6, V7 only
- **Third run**: Skips all (V1-V7 already applied), starts immediately
- **Future run** (after adding V8): Applies only V8

### 6. **Viewing Migration History:**

You can inspect the migration history anytime:

```sql
-- Connect to PostgreSQL
docker exec -it payment-postgres psql -U sampleuser -d sampledb

-- Set schema context to temp (where Flyway stores history)
SET search_path TO temp;

-- View all migrations
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- Or use fully qualified name
SELECT * FROM temp.flyway_schema_history ORDER BY installed_rank;

-- View only successful migrations
SELECT version, description, installed_on, execution_time 
FROM temp.flyway_schema_history 
WHERE success = true 
ORDER BY installed_rank;

-- Check latest migration
SELECT * FROM temp.flyway_schema_history 
ORDER BY installed_rank DESC 
LIMIT 1;
```

### Key Takeaways

- No manual SQL execution - Flyway runs everything automatically
- Automatic history tracking - `flyway_schema_history` is created and maintained by Flyway
- Idempotent - Migrations only run once, even after restarts
- Safe - Checksums prevent accidental modifications
- Transparent - Full history is visible in the database
- Team-friendly - Everyone's database stays in sync automatically

## Testing Flyway Migrations

This section shows how to test Flyway's incremental migration capabilities by adding new columns and indexes.

#### **Step 1: Initial Setup**

First, ensure your database is clean or start fresh:

```bash
# Start PostgreSQL container
docker run -d --name payment-postgres \
  -e POSTGRES_DB=sampledb \
  -e POSTGRES_USER=sampleuser \
  -e POSTGRES_HOST_AUTH_METHOD=trust \
  -p 5432:5432 \
  postgres:latest

# Build and run the application (first time)
mvn clean install
mvn spring-boot:run
```

**What happens:**
- Flyway runs migrations V1 through V4
- Creates `users` and `payments` tables
- Inserts sample data
- Creates `flyway_schema_history` table

#### **Step 2: Verify Initial State**

Check the database structure:

```sql
-- Connect to PostgreSQL
docker exec -it payment-postgres psql -U sampleuser -d sampledb

-- Set schema
SET search_path TO temp;

-- Check users table structure
\d users

-- Check payments table structure
\d payments

-- View migration history
SELECT version, description, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;
```

You should see:
- `users` table with: id, username, email, first_name, last_name, status, created_at, updated_at
- `payments` table with: id, user_id, amount, currency, status, payment_method, transaction_id, created_at, updated_at
- 4 migrations in history (V1, V2, V3, V4)

#### **Step 3: Add New Migrations (V5, V6, V7)**

The project already includes migrations V5, V6, and V7:

- **V5**: Adds `phone_number` column to users table
- **V6**: Adds `description` and `fee_amount` columns to payments table  
- **V7**: Adds multiple indexes for performance

#### **Step 4: Re-run Application**

Simply restart the application:

```bash
# Stop the application (Ctrl+C)
# Then restart
mvn spring-boot:run
```

**What happens:**
- Flyway checks `flyway_schema_history`
- Finds V1-V4 already applied
- Detects new migrations V5, V6, V7
- **Automatically executes** V5, V6, V7 in order
- Updates `flyway_schema_history` with new records

**Console output you'll see:**
```
Flyway Community Edition 9.x.x by Redgate
Database: jdbc:postgresql://localhost:5432/sampledb
Successfully validated 7 migrations (execution time 00:00.015s)
Current version of schema "temp": 4
Migrating schema "temp" to version "5 - Add phone number to users"
Successfully applied migration to schema "temp": 5 - Add phone number to users
Migrating schema "temp" to version "6 - Add description and fee to payments"
Successfully applied migration to schema "temp": 6 - Add description and fee to payments
Migrating schema "temp" to version "7 - Add additional indexes"
Successfully applied migration to schema "temp": 7 - Add additional indexes
```

#### **Step 5: Verify Changes**

Verify the new columns and indexes were added:

```sql
-- Check updated users table structure
\d users
-- You should now see: phone_number column

-- Check updated payments table structure
\d payments
-- You should now see: description and fee_amount columns

-- Check indexes on users table
\di users*
-- You should see multiple indexes including idx_users_phone_number, idx_users_username, etc.

-- Check indexes on payments table
\di payments*
-- You should see multiple indexes including idx_payments_payment_method, idx_payments_amount, etc.

-- View complete migration history
SELECT version, description, installed_on, execution_time, success 
FROM flyway_schema_history 
ORDER BY installed_rank;
```

**Expected results:**
- `users` table now has `phone_number` column
- `payments` table now has `description` and `fee_amount` columns
- Multiple new indexes on both tables
- 7 migrations in history (V1 through V7)

#### **Step 6: Test Idempotency**

Restart the application again:

```bash
mvn spring-boot:run
```

**What happens:**
- Flyway checks `flyway_schema_history`
- Finds all 7 migrations already applied
- **Skips all migrations** (no re-execution)
- Application starts immediately

**Console output:**
```
Flyway Community Edition 9.x.x by Redgate
Database: jdbc:postgresql://localhost:5432/sampledb
Successfully validated 7 migrations (execution time 00:00.012s)
Current version of schema "temp": 7
Schema "temp" is up to date. No migration necessary.
```

This demonstrates Flyway's **idempotency** - migrations only run once!

### Key Takeaways from Testing

- New migrations run automatically on startup
- Can add columns to existing tables safely
- All changes are tracked in `flyway_schema_history`
- Migrations only run once, even after restarts
- Never need to run SQL scripts manually
- Failed migrations are rolled back automatically

## Testing the API

Once the application is running, you can test the REST endpoints:

### Users API

```bash
# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Get user by username
curl http://localhost:8080/api/users/username/john_doe

# Create a new user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "firstName": "New",
    "lastName": "User"
  }'
```

### Payments API

```bash
# Get all payments
curl http://localhost:8080/api/payments

# Get payments by user ID
curl http://localhost:8080/api/payments/user/1

# Get payments by status
curl http://localhost:8080/api/payments/status/COMPLETED

# Create a new payment
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 99.99,
    "currency": "USD",
    "status": "PENDING",
    "paymentMethod": "CREDIT_CARD"
  }'
```

## Viewing Migration History

You can check the migration history directly in the database. This table is **automatically created and maintained by Flyway** in the `temp` schema - you never need to create or modify it manually.

```sql
-- Connect to PostgreSQL
docker exec -it payment-postgres psql -U sampleuser -d sampledb

-- Set schema context to temp
SET search_path TO temp;

-- View complete migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- View summary (version, description, when it ran, execution time)
SELECT 
    version, 
    description, 
    installed_on, 
    execution_time,
    success
FROM temp.flyway_schema_history 
ORDER BY installed_rank;

-- Or use fully qualified name without setting search_path
SELECT * FROM temp.flyway_schema_history ORDER BY installed_rank;

-- Check if all migrations succeeded
SELECT 
    COUNT(*) as total_migrations,
    SUM(CASE WHEN success THEN 1 ELSE 0 END) as successful,
    SUM(CASE WHEN NOT success THEN 1 ELSE 0 END) as failed
FROM temp.flyway_schema_history;
```

**Note**: See the "How Flyway Works" section above for detailed explanation of how this table is maintained.

## Creating New Migrations

To add a new migration:

1. Create a new file in `src/main/resources/db/migration/`
2. Follow the naming convention: `V{next_version}__{description}.sql`
3. Write your SQL migration script
4. Restart the application - Flyway will run it automatically

**Example - Adding a new table:**

Create `V5__Create_orders_table.sql`:

```sql
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Best Practices

1. **Never modify existing migrations** - Create new ones instead
2. **Use IF NOT EXISTS** - Makes migrations idempotent
3. **Test migrations** - Test on a copy of production data first
4. **Version control** - Commit all migration files to Git
5. **Descriptive names** - Use clear descriptions in migration filenames
6. **Small, focused migrations** - One logical change per migration
7. **Review before applying** - Always review SQL before running in production

## Flyway vs JPA DDL

| Feature | Flyway | JPA `ddl-auto` |
|---------|--------|----------------|
| Version Control | ✅ Yes | ❌ No |
| Migration History | ✅ Tracked | ❌ Not tracked |
| Production Ready | ✅ Yes | ❌ Not recommended |
| Rollback Support | ✅ Manual | ❌ No |
| Team Collaboration | ✅ Excellent | ❌ Poor |
| Schema as Code | ✅ Yes | ❌ No |

## Troubleshooting

### Migration fails on startup
- Check PostgreSQL is running: `docker ps`
- Verify database connection in `application.properties`
- Check migration SQL syntax

### Migration already applied error
- Flyway detected a checksum mismatch
- Don't modify existing migrations
- Create a new migration instead

### Database connection error
- Verify Docker container is running
- Check port 5432 is not in use
- Verify credentials in `application.properties`

## Additional Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
- [Flyway Naming Patterns](https://flywaydb.org/documentation/concepts/migrations#naming)

## Why Flyway is Better

Here are the real-world benefits of using Flyway:

### 1. Complete Version History

Without Flyway, you have no record of what database changes were made, when they happened, or who made them. There's no way to track schema evolution.

With Flyway, you get a complete audit trail in the `flyway_schema_history` table. Every change is timestamped and tracked. You can see exactly when V5 added the `phone_number` column, and you have the full history of schema evolution from V1 to V7.

**Example:**
```sql
-- See exactly when phone_number was added
SELECT version, description, installed_on 
FROM flyway_schema_history 
WHERE description LIKE '%phone%';
-- Result: V5 was applied on 2024-01-15 10:35:12
```

### 2. Team Synchronization

Without Flyway, developers end up with different schemas. Manual SQL scripts get lost or forgotten. Production databases differ from development. You get classic "works on my machine" database issues.

With Flyway, all developers run the same migrations. Everyone's database is identical. Production matches development exactly. New team members get the correct schema automatically.

Real scenario: A developer adds V5 migration (phone_number column), commits to Git. Other developers pull the code and restart the app. Flyway automatically applies V5 to their databases. Everyone has the same schema instantly.

### 3. Safe Production Deployments

Without Flyway, you're manually executing SQL in production (risky!), with no way to verify what was applied or rollback easily. Human error is a constant concern.

With Flyway, migrations are code-reviewed before deployment, tested in dev/staging first, and execute automatically (no manual errors). You have a complete record of what was applied and can see exactly what ran in production.

**Production Deployment:**
```bash
# Deploy new version with V5, V6, V7 migrations
# Flyway automatically:
# 1. Checks what's already applied (V1-V4)
# 2. Runs only new migrations (V5, V6, V7)
# 3. Records everything in flyway_schema_history
# 4. Application starts with updated schema
```

### 4. Incremental Schema Evolution

Without Flyway, you must recreate the entire database for changes, losing data in the process. You can't add columns incrementally, leading to big-bang deployments.

With Flyway, you can add columns incrementally (V5, V6), add indexes without downtime (V7), preserve existing data, and make small, safe changes over time.

In our example:
- V1-V4: Initial schema and data
- V5: Added `phone_number` to existing users (no data loss)
- V6: Added `description` and `fee_amount` to payments (existing payments preserved)
- V7: Added indexes for performance (no downtime)

### 5. Immutability and Safety

Without Flyway, you can accidentally modify production schema, there's no checksum validation, and changes can be lost or overwritten.

With Flyway, migrations are immutable (checksum protected). You can't modify applied migrations. If someone tries to change V5, Flyway detects it and forces you to create V8 instead (proper versioning).

Safety feature:
```
If you modify V5__Add_phone_number_to_users.sql after it's applied:
Flyway Error: Validate failed: Migration checksum mismatch
Solution: Revert the change, create V8 for new changes instead
```

### 6. Performance Optimization Tracking

Without Flyway, indexes are added manually with no record. You can't track when performance improvements were made or know which indexes exist or why.

With Flyway, the V7 migration documents all indexes added. You know exactly when indexes were created, can see performance improvements in history, and index creation is versioned and tracked.

In our V7 example, we added 5 indexes to the users table and 7 indexes to the payments table. All are documented in the migration script, tracked in `flyway_schema_history`, and you can see execution time for index creation.

### 7. Database as Code

Without Flyway, the database schema is separate from code, schema changes aren't in version control, and you can't review database changes in PRs.

With Flyway, the database schema is code (SQL files in Git). Schema changes are reviewed in pull requests, use the same version control as application code, and you can see database evolution in Git history.

Git integration:
```bash
# See all database changes
git log --oneline -- db/migration/

# Review V5 migration
git show HEAD:src/main/resources/db/migration/V5__Add_phone_number_to_users.sql

# Compare schema versions
git diff V1.0 V2.0 -- db/migration/
```

### 8. Zero-Downtime Migrations

Without Flyway, manual coordination is required, there's risk of missing steps, and no automatic rollback on failure.

With Flyway, execution is automatic on startup, transactional (rollback on failure), you can plan zero-downtime migrations, and it's safe to add columns/indexes incrementally.

In our V5-V7 example:
- V5: Added nullable column (safe, no downtime)
- V6: Added nullable columns with defaults (safe, no downtime)
- V7: Added indexes (safe, can run in background)
- All applied automatically, transactionally, safely

### Summary: The Flyway Advantage

| Aspect | Without Flyway | With Flyway |
|--------|---------------|-------------|
| Version History | None | Complete in `flyway_schema_history` |
| Team Sync | Manual, error-prone | Automatic, consistent |
| Production Safety | Risky manual execution | Automated, tested, tracked |
| Incremental Changes | Difficult | Easy (V5, V6, V7) |
| Immutability | Can modify anytime | Checksum protected |
| Performance Tracking | No record | Documented (V7) |
| Database as Code | Separate | In Git, reviewed |
| Zero Downtime | Manual coordination | Automatic, transactional |

## Learning Points

1. Database changes are code - All schema changes go through migration scripts
2. Version control - Every change is tracked and versioned (V1 through V7)
3. Automated migrations - Flyway runs migrations automatically on startup
4. Team collaboration - Everyone gets the same database schema automatically
5. Production safety - Migrations are tested and reviewed before deployment
6. Incremental evolution - Add columns (V5, V6) and indexes (V7) safely over time
7. Complete audit trail - `flyway_schema_history` tracks every change with timestamps
8. Immutable migrations - Once applied, migrations are protected by checksums
