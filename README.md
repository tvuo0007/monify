# 💰 Monify - Backend API

A robust and secure RESTful API for personal finance management, built with Spring Boot 4.0 and modern Java practices. This backend service provides comprehensive expense and income tracking capabilities with JWT-based authentication, daily email notifications and data export functionality.

[![Java](https://img.shields.io/badge/Java-25-orange?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=flat&logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat&logo=mysql)](https://www.mysql.com/)

## Key Features

### Core Functionality
- **Financial Transaction Management**: Track expenses and income with detailed categorization
- **Dashboard Analytics**: Real-time financial insights and transaction summaries
- **Category Management**: Custom category creation and organization for better tracking
- **User Profile Management**: Comprehensive user account and preference management
- **Advanced Filtering**: Filter transactions by date, category, amount, and other criteria
- **Data Export**: Export financial data to Excel format for external analysis

### Security & Authentication
- **JWT Authentication**: Secure token-based authentication system
- **Password Encryption**: BCrypt password hashing for enhanced security
- **Email Verification**: Account activation via email verification
- **Session Management**: Stateless REST API with JWT token management
- **CORS Configuration**: Cross-origin resource sharing for frontend integration

### Technical Highlights
- **RESTful API Design**: Clean and intuitive API endpoints following REST principles
- **DTO Pattern**: Data Transfer Objects for clean separation of concerns
- **Service Layer Architecture**: Well-structured business logic separation
- **JPA/Hibernate**: Efficient database operations with ORM
- **Email Service Integration**: Automated email notifications using SMTP

## Tech Stack

### Backend Framework
- **Spring Boot 4.0.1** - Application framework
- **Spring Web MVC** - RESTful web services
- **Spring Data JPA** - Data persistence layer
- **Spring Security** - Authentication and authorization
- **Spring Mail** - Email service integration

### Database & Persistence
- **MySQL** - Primary relational database
- **Hibernate** - ORM implementation
- **JPA** - Java Persistence API

### Security & Authentication
- **JWT (JSON Web Tokens)** - Token-based authentication
- **BCrypt** - Password encryption
- **JJWT (io.jsonwebtoken)** - JWT implementation library

### Additional Libraries
- **Lombok** - Boilerplate code reduction
- **Apache POI** - Excel file generation and manipulation
- **Maven** - Dependency management and build tool

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 25** or higher
- **Apache Maven 3.6+** for dependency management
- **MySQL 8.0+** database server
- **Git** for version control

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
```

### 2. Database Setup
Create a MySQL database:
```sql
CREATE DATABASE <your-db-name>;
```

### 3. Configure Application Properties
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=your-db-connection
spring.datasource.username=your_username
spring.datasource.password=your_password

# Email Configuration (using Brevo SMTP)
spring.mail.username=your-mail-username
spring.mail.password=your-mail-password
spring.mail.properties.mail.smtp.from=your-sender-mail

# JWT Secret Key (generate your own secret)
jwt.secret=your_jwt_secret_key

# Front-end URL (for demo email sent)
app.frontend.url=your_frontend_url
app.base.url=your_base_url
```

### 4. Set Environment Variables
Create environment variables for sensitive data in .env file:
```bash
MAIL_USERNAME=your_email_username
MAIL_PASSWORD=your_email_password
MAIL_FROM_EMAIL=your_sender_email
```

### 5. Build the Application
```bash
mvn clean install -DskipTests
```

### 6. Run the Application
```bash
mvn spring-boot:run
```

The API will start running at `http://localhost:8080/api/v1.0` on your local computer. I will deploy this project in the future.

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1.0/register` | Register new user | ❌ |
| POST | `/api/v1.0/login` | User login | ❌ |
| GET | `/api/v1.0/activate` | Activate account | ❌ |
| DELETE | `/api/v1.0/account` | Delete account and all user data (password confirmation required) | ✅ |
| GET | `/api/v1.0/status` | API health check | ❌ |

### Dashboard
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1.0/dashboard` | Get dashboard analytics | ✅ |

### Expenses
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1.0/expenses` | Add new expense | ✅ |
| GET | `/api/v1.0/expenses` | Get all expenses | ✅ |
| DELETE | `/api/v1.0/expenses/{id}` | Delete expense | ✅ |

### Income
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1.0/incomes` | Add new income | ✅ |
| GET | `/api/v1.0/incomes` | Get all income | ✅ |
| DELETE | `/api/v1.0/incomes/{id}` | Delete income | ✅ |

### Categories
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1.0/categories` | Create category | ✅ |
| GET | `/api/v1.0/categories` | Get all categories | ✅ |
| GET | `/api/v1.0/categories/{type}` | Get category by type | ✅ |
| PUT | `/api/v1.0/categories/{id}` | Update category | ✅ |

### Filters
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1.0/filter` | Filter transactions | ✅ |

### Excel Export
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1.0/excel/download/incomes` | Download current month incomes as Excel file | ✅ |
| GET | `/api/v1.0/excel/download/expenses` | Download current month expenses as Excel file | ✅ |

### Email Income and Expense Excel Report
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1.0/email/income-excel` | Send email with Excel file for income report | ✅ |
| GET | `/api/v1.0/email/expense-excel` | Send email with Excel file for expense report | ✅ |

## Project Structure

```
├── 📜 HELP.md
├── 📄 mvnw
├── 📄 mvnw.cmd
├── 📄 pom.xml
├── 📜 README.md
├── 📁 src
│ ├── 📁 main
│ │ ├── 📁 java
│ │ │ ├── 📁 com
│ │ │ │ ├── 📁 thephong
│ │ │ │ │ ├── 📁 moneymanager
│ │ │ │ │ │ ├── 📁 config
│ │ │ │ │ │ │ ├── 📄 SecurityConfig.java
│ │ │ │ │ │ ├── 📁 controller
│ │ │ │ │ │ │ ├── 📄 CategoryController.java
│ │ │ │ │ │ │ ├── 📄 DashboardController.java
│ │ │ │ │ │ │ ├── 📄 EmailController.java
│ │ │ │ │ │ │ ├── 📄 ExcelController.java
│ │ │ │ │ │ │ ├── 📄 ExpenseController.java
│ │ │ │ │ │ │ ├── 📄 FilterController.java
│ │ │ │ │ │ │ ├── 📄 HomeController.java
│ │ │ │ │ │ │ ├── 📄 IncomeController.java
│ │ │ │ │ │ │ ├── 📄 ProfileController.java
│ │ │ │ │ │ ├── 📁 dto
│ │ │ │ │ │ │ ├── 📄 AuthDTO.java
│ │ │ │ │ │ │ ├── 📄 CategoryDTO.java
│ │ │ │ │ │ │ ├── 📄 ExpenseDTO.java
│ │ │ │ │ │ │ ├── 📄 FilterDTO.java
│ │ │ │ │ │ │ ├── 📄 IncomeDTO.java
│ │ │ │ │ │ │ ├── 📄 ProfileDTO.java
│ │ │ │ │ │ │ ├── 📄 RecentTransactionDTO.java
│ │ │ │ │ │ ├── 📁 entity
│ │ │ │ │ │ │ ├── 📄 CategoryEntity.java
│ │ │ │ │ │ │ ├── 📄 ExpenseEntity.java
│ │ │ │ │ │ │ ├── 📄 IncomeEntity.java
│ │ │ │ │ │ │ ├── 📄 ProfileEntity.java
│ │ │ │ │ │ ├── 📁 repository
│ │ │ │ │ │ │ ├── 📄 CategoryRepository.java
│ │ │ │ │ │ │ ├── 📄 ExpenseRepository.java
│ │ │ │ │ │ │ ├── 📄 IncomeRepository.java
│ │ │ │ │ │ │ ├── 📄 ProfileRepository.java
│ │ │ │ │ │ ├── 📁 security
│ │ │ │ │ │ │ ├── 📄 JwtRequestFilter.java
│ │ │ │ │ │ ├── 📁 service
│ │ │ │ │ │ │ ├── 📄 AppUserDetailsService.java
│ │ │ │ │ │ │ ├── 📄 CategoryService.java
│ │ │ │ │ │ │ ├── 📄 DashboardService.java
│ │ │ │ │ │ │ ├── 📄 EmailService.java
│ │ │ │ │ │ │ ├── 📄 ExcelService.java
│ │ │ │ │ │ │ ├── 📄 ExpenseService.java
│ │ │ │ │ │ │ ├── 📄 IncomeService.java
│ │ │ │ │ │ │ ├── 📄 NotificationService.java
│ │ │ │ │ │ │ ├── 📄 ProfileService.java
| | | | | | ├── 📄 MoneymanagerApplication.java
│ │ │ │ │ │ ├── 📁 util
│ │ │ │ │ │ │ ├── 📄 JwtUtil.java
│ │ ├── 📁 resources
│ │ │ ├── 📄 application.properties
│ │ │ ├── 📁 static
│ │ │ ├── 📁 templates
│ ├── 📁 test
│ │ ├── 📁 java
│ │ │ ├── 📁 com
│ │ │ │ ├── 📁 thephong
│ │ │ │ │ ├── 📁 moneymanager
│ │ │ │ │ │ ├── 📄 MoneymanagerApplicationTests.java

```

## Authentication Flow

1. **Registration**: User registers with email and password
2. **Email Verification**: Activation link sent to user's email
3. **Account Activation**: User clicks activation link to enable account
4. **Login**: User authenticates with credentials
5. **JWT Token**: Server issues JWT token upon successful authentication
6. **Protected Routes**: Include JWT token in `Authorization` header for subsequent requests
   ```
   Authorization: Bearer <your_jwt_token>
   ```

## Database Schema

The application uses the following main entities:

### ProfileEntity
- User profile information
- Authentication credentials
- Account activation status

### CategoryEntity
- Category name and type
- User association
- Transaction categorization

### ExpenseEntity
- Expense details (amount, date, description)
- Category association
- User ownership

### IncomeEntity
- Income details (amount, date, source)
- Category association
- User ownership

## Security Features

1. **Password Encryption**: All passwords are hashed using BCrypt before storage
2. **JWT Authentication**: Stateless authentication with token expiration
3. **CORS Protection**: Configured CORS policy for secure cross-origin requests
4. **SQL Injection Prevention**: JPA/Hibernate parameterized queries
5. **Session Management**: Stateless REST API design

## Data Export

Export your financial data to Excel format for:
- Personal record keeping
- Tax preparation
- Financial analysis
- Budget planning

## Future Enhancements

- Managing expenses and incomes with AI
- Multi-currency support
- Financial goals tracking

## Author

**The Phong Vuong**

- GitHub: [@thephong](https://github.com/tvuo0007)
- Project: Monify Backend with Spring Boot 4.0 and MySQL 8.0+
