# Money Manager API Documentation

API reference for building a React SPA frontend against the Money Manager backend (Spring Boot).

---

## Table of Contents

1. [Overview](#overview)
2. [Base URL & Environment](#base-url--environment)
3. [Authentication](#authentication)
4. [Common Conventions](#common-conventions)
5. [Data Models (TypeScript)](#data-models-typescript)
6. [Endpoints](#endpoints)
   - [Health Check](#health-check)
   - [Authentication & Profile](#authentication--profile)
   - [Dashboard](#dashboard)
   - [Categories](#categories)
   - [Incomes](#incomes)
   - [Expenses](#expenses)
   - [Filter / Search](#filter--search)
   - [Excel Export (Download)](#excel-export-download)
   - [Email Reports](#email-reports)
7. [Error Handling](#error-handling)
8. [React SPA Integration Guide](#react-spa-integration-guide)
9. [Suggested Frontend Pages & Routes](#suggested-frontend-pages--routes)
10. [Important Behavioral Notes](#important-behavioral-notes)

---

## Overview

| Property | Value |
|---|---|
| **API version prefix** | `/api/v1.0` |
| **Default local URL** | `http://localhost:8080/api/v1.0` |
| **Content type** | `application/json` (except Excel downloads) |
| **Auth mechanism** | JWT Bearer token |
| **Token lifetime** | 10 hours |
| **CORS** | All origins allowed (`*`), credentials enabled |
| **Session** | Stateless (no server-side sessions) |

All authenticated endpoints scope data to the **currently logged-in user** (derived from the JWT subject, which is the user's email).

---

## Base URL & Environment

Configure your React app with an environment variable:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1.0
```

Every endpoint in this document is relative to that base URL.

**Example:** Login → `POST http://localhost:8080/api/v1.0/login`

---

## Authentication

### Public endpoints (no token required)

| Method | Path |
|---|---|
| `GET` | `/status`, `/health` |
| `POST` | `/register` |
| `GET` | `/activate` |
| `POST` | `/login` |

### Protected endpoints

All other endpoints require a valid JWT in the `Authorization` header:

```http
Authorization: Bearer <jwt_token>
```

### Auth flow

```
Register → Email with activation link → Activate account → Login → Store JWT → Call protected APIs
```

1. **Register** — Creates an inactive account and sends an activation email.
2. **Activate** — User clicks the link in the email (`GET /activate?token=...`) to activate the account.
3. **Login** — Returns a JWT and user profile. Only active accounts can log in.
4. **Use token** — Attach the JWT to every subsequent request until it expires (10 hours).

### Login response shape

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "profileImageUrl": null,
    "createdAt": "2026-01-15T10:30:00",
    "updatedAt": "2026-01-15T10:30:00"
  }
}
```

> **Note:** The `password` field is never returned in API responses.

---

## Common Conventions

### Date & time formats

| Type | Format | Example |
|---|---|---|
| `LocalDate` | `YYYY-MM-DD` | `"2026-07-04"` |
| `LocalDateTime` | ISO-8601 (no timezone) | `"2026-07-04T14:30:00"` |
| `BigDecimal` (money) | JSON number or string | `1500.50` |

### HTTP status codes used

| Code | Meaning |
|---|---|
| `200` | Success |
| `201` | Created |
| `204` | Success, no body (delete) |
| `400` | Bad request / validation / invalid credentials |
| `401` | Unauthorized (missing or invalid JWT) |
| `403` | Forbidden (e.g. inactive account on login) |
| `404` | Not found |
| `500` | Server error (unhandled exceptions) |

### Sorting (filter endpoint)

| Field | Values | Default |
|---|---|---|
| `sortField` | `"date"`, `"amount"`, or any entity field name | `"date"` |
| `sortOrder` | `"asc"` or `"desc"` | `"asc"` |

---

## Data Models (TypeScript)

Use these interfaces in your React app:

```typescript
// ─── Auth & Profile ───────────────────────────────────────────────

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  profileImageUrl?: string;
}

export interface Profile {
  id: number;
  fullName: string;
  email: string;
  profileImageUrl: string | null;
  createdAt: string;   // ISO datetime
  updatedAt: string;   // ISO datetime
}

export interface LoginResponse {
  token: string;
  user: Profile;
}

export interface ErrorResponse {
  message: string;
}

// ─── Categories ───────────────────────────────────────────────────

export type CategoryType = 'income' | 'expense';

export interface Category {
  id: number;
  profileId: number;
  name: string;
  icon: string;
  type: CategoryType;
  createdAt: string;
  updatedAt: string;
}

export interface CreateCategoryRequest {
  name: string;
  icon: string;
  type: CategoryType;
}

export interface UpdateCategoryRequest {
  name: string;
  icon: string;
}

// ─── Transactions ─────────────────────────────────────────────────

export interface Income {
  id: number;
  name: string;
  icon: string;
  categoryName: string;
  categoryId: number;
  amount: number;
  date: string;          // YYYY-MM-DD
  createdAt: string;
  updatedAt: string;
}

export interface Expense {
  id: number;
  name: string;
  icon: string;
  categoryName: string;
  categoryId: number;
  amount: number;
  date: string;          // YYYY-MM-DD
  createdAt: string;
  updatedAt: string;
}

export interface CreateIncomeRequest {
  name: string;
  icon: string;
  categoryId: number;
  amount: number;
  date?: string;         // defaults to today if omitted
}

export interface CreateExpenseRequest {
  name: string;
  icon: string;
  categoryId: number;
  amount: number;
  date?: string;         // defaults to today if omitted
}

export interface RecentTransaction {
  id: number;
  profileId: number;
  icon: string;
  name: string;
  amount: number;
  date: string;
  createdAt: string;
  updatedAt: string;
  type: 'income' | 'expense';
}

// ─── Dashboard ────────────────────────────────────────────────────

export interface DashboardData {
  totalBalance: number;
  totalIncome: number;
  totalExpense: number;
  recent5Incomes: Income[];
  recent5Expenses: Expense[];
  recentTransactions: RecentTransaction[];
}

// ─── Filter ───────────────────────────────────────────────────────

export interface FilterRequest {
  type: 'income' | 'expense';
  startDate?: string;    // YYYY-MM-DD, defaults to earliest possible date
  endDate?: string;      // YYYY-MM-DD, defaults to today
  keyword?: string;      // case-insensitive name search, defaults to ""
  sortField?: string;    // defaults to "date"
  sortOrder?: 'asc' | 'desc';  // defaults to "asc"
}
```

---

## Endpoints

### Health Check

#### `GET /status` or `GET /health`

Check whether the API is running.

**Auth:** None

**Response `200`:**

```text
Application is running
```

---

### Authentication & Profile

#### `POST /register`

Create a new user account. Account starts **inactive** until email activation.

**Auth:** None

**Request body:**

```json
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "securePassword123",
  "profileImageUrl": "https://example.com/avatar.png"
}
```

| Field | Required | Notes |
|---|---|---|
| `fullName` | Yes | Display name |
| `email` | Yes | Must be unique |
| `password` | Yes | Stored hashed (BCrypt) |
| `profileImageUrl` | No | Optional avatar URL |

**Response `201`:** `Profile` object (no password)

```json
{
  "id": 1,
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "profileImageUrl": "https://example.com/avatar.png",
  "createdAt": "2026-07-04T10:00:00",
  "updatedAt": "2026-07-04T10:00:00"
}
```

**Side effect:** Sends an activation email with link:

```text
{BASE_URL}/activate?token={uuid}
```

Default `BASE_URL` is `http://localhost:8080/api/v1.0`.

---

#### `GET /activate`

Activate a registered account using the token from the activation email.

**Auth:** None

**Query parameters:**

| Param | Required | Description |
|---|---|---|
| `token` | Yes | UUID activation token from email |

**Example:**

```http
GET /activate?token=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Response `200`:**

```text
Profile activated successfully
```

**Response `404`:**

```text
Activation token not found or already used
```

---

#### `POST /login`

Authenticate and receive a JWT.

**Auth:** None

**Request body:**

```json
{
  "email": "jane@example.com",
  "password": "securePassword123"
}
```

**Response `200`:** `LoginResponse`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "profileImageUrl": null,
    "createdAt": "2026-07-04T10:00:00",
    "updatedAt": "2026-07-04T10:00:00"
  }
}
```

**Response `403`** (inactive account):

```json
{
  "message": "Account is not active. Please activate your account first."
}
```

**Response `400`** (invalid credentials):

```json
{
  "message": "Invalid email or password"
}
```

---

### Dashboard

#### `GET /dashboard`

Returns summary totals and recent transaction data for the authenticated user.

**Auth:** Required

**Response `200`:**

```json
{
  "totalBalance": 5000.00,
  "totalIncome": 10000.00,
  "totalExpense": 5000.00,
  "recent5Incomes": [ /* Income[] — latest 5 by date */ ],
  "recent5Expenses": [ /* Expense[] — latest 5 by date */ ],
  "recentTransactions": [ /* RecentTransaction[] — merged & sorted by date desc */ ]
}
```

| Field | Description |
|---|---|
| `totalBalance` | `totalIncome - totalExpense` (all-time) |
| `totalIncome` | Sum of all incomes (all-time) |
| `totalExpense` | Sum of all expenses (all-time) |
| `recent5Incomes` | 5 most recent income records |
| `recent5Expenses` | 5 most recent expense records |
| `recentTransactions` | Combined list of recent incomes + expenses, sorted by date (desc), then `createdAt` (desc) |

> **Important:** Dashboard totals are **all-time**, while `GET /incomes` and `GET /expenses` return **current month only**.

---

### Categories

Categories are user-scoped. Each category has a `type` of `"income"` or `"expense"`. Users must create categories before adding transactions.

#### `POST /categories`

Create a new category.

**Auth:** Required

**Request body:**

```json
{
  "name": "Salary",
  "icon": "💰",
  "type": "income"
}
```

| Field | Required | Notes |
|---|---|---|
| `name` | Yes | Must be unique per user |
| `icon` | Yes | Emoji or icon identifier |
| `type` | Yes | `"income"` or `"expense"` |

**Response `201`:** `Category`

**Errors:** Duplicate name → `500` with message `"Category with this name already exists"`

---

#### `GET /categories`

List all categories for the current user.

**Auth:** Required

**Response `200`:** `Category[]`

---

#### `GET /categories/{type}`

List categories filtered by type.

**Auth:** Required

**Path parameters:**

| Param | Values |
|---|---|
| `type` | `"income"` or `"expense"` |

**Example:**

```http
GET /categories/income
```

**Response `200`:** `Category[]`

---

#### `PUT /categories/{categoryId}`

Update an existing category's name and icon. Type cannot be changed via this endpoint.

**Auth:** Required

**Path parameters:**

| Param | Type |
|---|---|
| `categoryId` | `number` |

**Request body:**

```json
{
  "name": "Freelance",
  "icon": "💻"
}
```

**Response `200`:** Updated `Category`

**Errors:** Category not found or belongs to another user → `500` with message `"Category not found or not accessible"`

---

### Incomes

#### `POST /incomes`

Add a new income record.

**Auth:** Required

**Request body:**

```json
{
  "name": "Monthly Salary",
  "icon": "💰",
  "categoryId": 1,
  "amount": 5000.00,
  "date": "2026-07-01"
}
```

| Field | Required | Notes |
|---|---|---|
| `name` | Yes | Income description |
| `icon` | Yes | Display icon |
| `categoryId` | Yes | Must reference an existing category |
| `amount` | Yes | Positive decimal |
| `date` | No | Defaults to today if omitted |

**Response `201`:** `Income`

**Errors:** Invalid `categoryId` → `500` with message `"Category not found"`

---

#### `GET /incomes`

List all income records for the **current calendar month**.

**Auth:** Required

**Response `200`:** `Income[]`

---

#### `DELETE /incomes/{id}`

Delete an income record owned by the current user.

**Auth:** Required

**Path parameters:**

| Param | Type |
|---|---|
| `id` | `number` |

**Response `204`:** No content

**Errors:**
- Not found → `500` `"Income not found"`
- Wrong owner → `500` `"Unauthorized to delete this income"`

---

### Expenses

#### `POST /expenses`

Add a new expense record.

**Auth:** Required

**Request body:**

```json
{
  "name": "Groceries",
  "icon": "🛒",
  "categoryId": 3,
  "amount": 85.50,
  "date": "2026-07-03"
}
```

| Field | Required | Notes |
|---|---|---|
| `name` | Yes | Expense description |
| `icon` | Yes | Display icon |
| `categoryId` | Yes | Must reference an existing category |
| `amount` | Yes | Positive decimal |
| `date` | No | Defaults to today if omitted |

**Response `201`:** `Expense`

**Errors:** Invalid `categoryId` → `500` with message `"Category not found"`

---

#### `GET /expenses`

List all expense records for the **current calendar month**.

**Auth:** Required

**Response `200`:** `Expense[]`

---

#### `DELETE /expenses/{id}`

Delete an expense record owned by the current user.

**Auth:** Required

**Path parameters:**

| Param | Type |
|---|---|
| `id` | `number` |

**Response `204`:** No content

**Errors:**
- Not found → `500` `"Expense not found"`
- Wrong owner → `500` `"Unauthorized to delete this expense"`

---

### Filter / Search

#### `POST /filter`

Search and sort incomes or expenses by date range and keyword.

**Auth:** Required

**Request body:**

```json
{
  "type": "expense",
  "startDate": "2026-01-01",
  "endDate": "2026-07-04",
  "keyword": "groceries",
  "sortField": "date",
  "sortOrder": "desc"
}
```

| Field | Required | Default | Notes |
|---|---|---|---|
| `type` | Yes | — | `"income"` or `"expense"` |
| `startDate` | No | `0001-01-01` | Start of date range (inclusive) |
| `endDate` | No | Today | End of date range (inclusive) |
| `keyword` | No | `""` | Case-insensitive partial match on `name` |
| `sortField` | No | `"date"` | Entity field to sort by |
| `sortOrder` | No | `"asc"` | `"asc"` or `"desc"` |

**Response `200`:** `Income[]` or `Expense[]` (depending on `type`)

**Response `400`** (invalid type):

```text
Invalid type. Must be 'income' or 'expense'.
```

---

### Excel Export (Download)

#### `GET /excel/download/incomes`

Download current month's incomes as an Excel file.

**Auth:** Required

**Response `200`:**

| Header | Value |
|---|---|
| `Content-Type` | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| `Content-Disposition` | `attachment; filename=incomes.xlsx` |

**Excel columns:** S.No, Name, Category, Amount, Date

**React example:**

```typescript
const downloadIncomesExcel = async () => {
  const response = await fetch(`${API_BASE_URL}/excel/download/incomes`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  const blob = await response.blob();
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'incomes.xlsx';
  a.click();
  URL.revokeObjectURL(url);
};
```

---

#### `GET /excel/download/expenses`

Download current month's expenses as an Excel file.

**Auth:** Required

**Response:** Same structure as incomes download, filename `expenses.xlsx`.

---

### Email Reports

These endpoints generate an Excel report for the **current month** and email it to the logged-in user's registered email address.

#### `GET /email/income-excel`

Email current month's income report.

**Auth:** Required

**Response `200`:** Empty body

**Side effect:** Sends email with subject `"Your Income Excel Report"` and `incomes.xlsx` attachment.

---

#### `GET /email/expense-excel`

Email current month's expense report.

**Auth:** Required

**Response `200`:** Empty body

**Side effect:** Sends email with subject `"Your Expense Excel Report"` and `expenses.xlsx` attachment.

---

## Error Handling

There is no global `@RestControllerAdvice` in the backend. Behavior varies by endpoint:

| Scenario | Typical response |
|---|---|
| Missing/invalid JWT | `401 Unauthorized` (Spring Security default) |
| Login — bad credentials | `400` with `{ "message": "..." }` |
| Login — inactive account | `403` with `{ "message": "..." }` |
| Invalid filter type | `400` plain text |
| Business logic errors (not found, duplicate, unauthorized) | `500` with Spring default error JSON |
| Activation token invalid | `404` plain text |

**Spring default error body (500):**

```json
{
  "timestamp": "2026-07-04T14:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Category not found",
  "path": "/api/v1.0/expenses"
}
```

### Recommended frontend error handling

```typescript
async function apiRequest<T>(url: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers,
    },
  });

  if (response.status === 401) {
    localStorage.removeItem('token');
    window.location.href = '/login';
    throw new Error('Session expired');
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: response.statusText }));
    throw new Error(error.message || 'Request failed');
  }

  if (response.status === 204) return undefined as T;
  const text = await response.text();
  return text ? JSON.parse(text) : (undefined as T);
}
```

---

## React SPA Integration Guide

### Recommended project structure

```
src/
├── api/
│   ├── client.ts          # fetch/axios wrapper with auth
│   ├── auth.api.ts
│   ├── dashboard.api.ts
│   ├── categories.api.ts
│   ├── incomes.api.ts
│   ├── expenses.api.ts
│   └── filter.api.ts
├── context/
│   └── AuthContext.tsx    # token + user state
├── hooks/
│   ├── useAuth.ts
│   └── useDashboard.ts
├── pages/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   ├── ActivatePage.tsx
│   ├── DashboardPage.tsx
│   ├── IncomesPage.tsx
│   ├── ExpensesPage.tsx
│   └── CategoriesPage.tsx
└── types/
    └── api.types.ts       # interfaces from Data Models section
```

### Auth context example

```typescript
// context/AuthContext.tsx
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { Profile, LoginResponse } from '../types/api.types';

interface AuthContextType {
  user: Profile | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [user, setUser] = useState<Profile | null>(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email: string, password: string) => {
    const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });
    if (!res.ok) {
      const err = await res.json();
      throw new Error(err.message);
    }
    const data: LoginResponse = await res.json();
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data.user));
    setToken(data.token);
    setUser(data.user);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
```

### API service examples

```typescript
// api/categories.api.ts
import { apiRequest } from './client';
import type { Category, CreateCategoryRequest, UpdateCategoryRequest } from '../types/api.types';

export const getCategories = () => apiRequest<Category[]>('/categories');

export const getCategoriesByType = (type: 'income' | 'expense') =>
  apiRequest<Category[]>(`/categories/${type}`);

export const createCategory = (data: CreateCategoryRequest) =>
  apiRequest<Category>('/categories', { method: 'POST', body: JSON.stringify(data) });

export const updateCategory = (id: number, data: UpdateCategoryRequest) =>
  apiRequest<Category>(`/categories/${id}`, { method: 'PUT', body: JSON.stringify(data) });
```

```typescript
// api/incomes.api.ts
import { apiRequest } from './client';
import type { Income, CreateIncomeRequest } from '../types/api.types';

export const getIncomes = () => apiRequest<Income[]>('/incomes');

export const createIncome = (data: CreateIncomeRequest) =>
  apiRequest<Income>('/incomes', { method: 'POST', body: JSON.stringify(data) });

export const deleteIncome = (id: number) =>
  apiRequest<void>(`/incomes/${id}`, { method: 'DELETE' });
```

```typescript
// api/filter.api.ts
import { apiRequest } from './client';
import type { FilterRequest, Income, Expense } from '../types/api.types';

export const filterTransactions = (filter: FilterRequest) =>
  apiRequest<Income[] | Expense[]>('/filter', {
    method: 'POST',
    body: JSON.stringify(filter),
  });
```

### Protected route wrapper

```typescript
// components/ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}
```

### Activation page

The activation email link points to the **backend** URL. Two options for the frontend:

**Option A — Direct backend call (simplest):**

Create a React route `/activate` that reads `?token=` from the URL and calls:

```typescript
const activateAccount = async (token: string) => {
  const res = await fetch(
    `${import.meta.env.VITE_API_BASE_URL}/activate?token=${token}`
  );
  const message = await res.text();
  return { success: res.ok, message };
};
```

**Option B — Backend link redirects to frontend:**

Configure `BASE_URL` to your React app URL and have the frontend route call the API.

---

## Suggested Frontend Pages & Routes

| Route | Page | API calls | Auth |
|---|---|---|---|
| `/login` | Login | `POST /login` | Public |
| `/register` | Register | `POST /register` | Public |
| `/activate` | Account activation | `GET /activate?token=` | Public |
| `/` or `/dashboard` | Dashboard overview | `GET /dashboard` | Protected |
| `/incomes` | Income list (current month) | `GET /incomes`, `POST /incomes`, `DELETE /incomes/{id}` | Protected |
| `/expenses` | Expense list (current month) | `GET /expenses`, `POST /expenses`, `DELETE /expenses/{id}` | Protected |
| `/categories` | Category management | `GET /categories`, `POST /categories`, `PUT /categories/{id}` | Protected |
| `/transactions` | Filtered history | `POST /filter` | Protected |
| `/reports` | Export & email | Excel download + email endpoints | Protected |

### Typical user flows

**1. Onboarding**

```
/register → check email → /activate?token=xxx → /login → /dashboard
```

**2. Add first transaction**

```
/categories (create income/expense categories)
  → /incomes or /expenses (create transaction with categoryId)
```

**3. Dashboard refresh**

```
GET /dashboard on mount
  → display totalBalance, totalIncome, totalExpense
  → render recentTransactions list
```

**4. Search historical data**

```
/transactions
  → POST /filter { type, startDate, endDate, keyword, sortField, sortOrder }
  → render results table
```

---

## Important Behavioral Notes

### Scope & date filtering

| Endpoint | Date scope |
|---|---|
| `GET /incomes` | Current calendar month only |
| `GET /expenses` | Current calendar month only |
| `GET /dashboard` totals | **All-time** sums |
| `GET /dashboard` recent lists | Latest 5 records (all-time) |
| `POST /filter` | Custom date range |
| Excel download / email | Current calendar month only |

Plan your UI labels accordingly (e.g. dashboard says "All-time balance" vs income page says "This month").

### No update endpoints for transactions

Incomes and expenses can be **created** and **deleted**, but not updated. To edit, delete and recreate, or request a backend `PUT` endpoint.

### Categories must exist before transactions

`POST /incomes` and `POST /expenses` require a valid `categoryId`. Build a category management UI or onboarding step first.

### Category name uniqueness

Category names must be unique **per user** (not per type). You cannot have two categories named "Food" even if one is income and one is expense.

### JWT expiration

Tokens expire after **10 hours**. Implement token refresh logic or redirect to login on `401` responses.

### CORS & credentials

The backend allows all origins with credentials. When using `fetch`, set `credentials: 'include'` only if you also use cookies; for JWT in headers, credentials are not required.

### No profile update endpoint

There is currently no API to update profile (`fullName`, `profileImageUrl`) or change password after registration.

---

## Quick Reference — All Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/status` | No | Health check |
| `GET` | `/health` | No | Health check |
| `POST` | `/register` | No | Register new user |
| `GET` | `/activate?token=` | No | Activate account |
| `POST` | `/login` | No | Login, get JWT |
| `GET` | `/dashboard` | Yes | Dashboard summary |
| `POST` | `/categories` | Yes | Create category |
| `GET` | `/categories` | Yes | List all categories |
| `GET` | `/categories/{type}` | Yes | List categories by type |
| `PUT` | `/categories/{categoryId}` | Yes | Update category |
| `POST` | `/incomes` | Yes | Create income |
| `GET` | `/incomes` | Yes | List current month incomes |
| `DELETE` | `/incomes/{id}` | Yes | Delete income |
| `POST` | `/expenses` | Yes | Create expense |
| `GET` | `/expenses` | Yes | List current month expenses |
| `DELETE` | `/expenses/{id}` | Yes | Delete expense |
| `POST` | `/filter` | Yes | Search/filter transactions |
| `GET` | `/excel/download/incomes` | Yes | Download incomes Excel |
| `GET` | `/excel/download/expenses` | Yes | Download expenses Excel |
| `GET` | `/email/income-excel` | Yes | Email incomes Excel |
| `GET` | `/email/expense-excel` | Yes | Email expenses Excel |

---

*Generated from the Money Manager Spring Boot backend source code.*
