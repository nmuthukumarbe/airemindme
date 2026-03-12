# 🚀 RemindMe – Scalable Reminder & Promotion Engine

A **high-performance reminder and campaign delivery engine** designed for SaaS CRM systems.
RemindMe enables businesses to send **automated reminders and promotions via SMS, Email, and WhatsApp** at scale.

The architecture is inspired by **modern background job systems used by Stripe, Shopify, GitHub, and Uber**, enabling **safe parallel processing, retries, and delivery tracking**.

---

# ✨ Features

* ⏰ Scheduled reminders (one-time & recurring)
* 📣 Promotion campaigns for customer groups
* 📲 Multi-channel messaging (SMS, Email, WhatsApp)
* ⚡ Queue-based asynchronous processing
* 🔁 Automatic retries & failure handling
* 📊 Delivery execution logs
* 🚀 Horizontal scaling with parallel workers
* 🧠 Priority-based job processing

---

# 🏗 System Architecture

```
scheduler
   │
   ▼
schedule_entry
   │
   ▼
message_queue
   │
   ▼
workers (parallel)
   │
   ▼
send SMS / Email / WhatsApp
   │
   ▼
execution_log tables
```

The system follows a **queue-driven architecture** where reminders and promotions are processed asynchronously by workers.

---

# 🔄 Queue Processing Flow

```
scheduler
   │
   └── insert message_queue rows
             │
             ▼
        workers (parallel)
             │
             ▼
      FOR UPDATE SKIP LOCKED
             │
             ▼
send SMS / WhatsApp / Email
             │
             ▼
update execution logs
```

Workers safely process queue jobs using **row locking (`FOR UPDATE SKIP LOCKED`)** to prevent duplicate execution when multiple workers run concurrently.

---

# ⚡ Performance

The system scales linearly with the number of workers.

| Workers | Reminders / Minute |
| ------- | ------------------ |
| 1       | ~300               |
| 5       | ~1500              |
| 20      | ~6000              |

This allows **millions of reminders per day** on modest infrastructure.

---

# 🗄 Database Architecture

```
account
   │
   ├── customer_group
   │
   ├── customer
   │
   ├── promotion
   │      └── promotion_entry
   │             └── promotion_execution_log
   │
   ├── schedule
   │      └── schedule_entry
   │             └── schedule_execution_log
   │
   └── message_queue
```

---

# 📦 Modules

## 👤 Account

Multi-tenant isolation layer for SaaS environments.

---

## 👥 Customer Groups

Segments customers for targeted promotions.

Examples:

* VIP customers
* Premium subscribers
* Retail customers

---

## 👤 Customers

Stores customer contact information used for messaging.

Example fields:

* name
* email
* mobile
* city / state / country
* birthday
* anniversary

---

# 📣 Promotion System

## promotion

Defines a marketing campaign.

Examples:

* Festival promotions
* Product announcements
* Discount campaigns

---

## promotion_entry

Tracks which customers receive which promotions.

---

## promotion_execution_log

Tracks delivery attempts and results.

Example:

| promotion_entry | channel  | result  |
| --------------- | -------- | ------- |
| 101             | WHATSAPP | SUCCESS |
| 101             | EMAIL    | FAILED  |

---

# ⏰ Reminder System

## schedule

Defines reminder rules.

Examples:

* payment reminder
* appointment reminder
* subscription renewal

---

## schedule_entry

Individual reminder occurrences generated from schedules.

Example:

| schedule         | occurrence_date |
| ---------------- | --------------- |
| Gym subscription | 2026-01-01      |
| Gym subscription | 2026-02-01      |
| Gym subscription | 2026-03-01      |

---

## schedule_execution_log

Tracks delivery attempts and outcomes for reminders.

---

# 📨 Message Queue

The **core engine of RemindMe**.

All reminders and promotions are converted into queue jobs and processed asynchronously.

Example queue record:

| entity_type | entity_entry_id | channel  | priority |
| ----------- | --------------- | -------- | -------- |
| SCHEDULE    | 1001            | WHATSAPP | 1        |
| PROMOTION   | 205             | EMAIL    | 5        |

---

# 🔁 Parallel Worker Processing

Workers continuously fetch jobs:

```sql
SELECT *
FROM message_queue
WHERE status = 'PENDING'
ORDER BY priority, id
LIMIT 50
FOR UPDATE SKIP LOCKED;
```

Benefits:

* safe parallel execution
* no duplicate sends
* high throughput

---

# 🧠 Priority-Based Processing

Jobs are processed using priority levels.

| Priority | Example              |
| -------- | -------------------- |
| 1        | payment reminder     |
| 2        | appointment reminder |
| 5        | marketing promotion  |

Processing order:

```
ORDER BY priority ASC, id ASC
```

---

# 📝 Execution Logging

Every delivery attempt is logged.

Example logs:

| entity          | channel  | result  |
| --------------- | -------- | ------- |
| schedule_entry  | SMS      | SUCCESS |
| schedule_entry  | WHATSAPP | FAILED  |
| promotion_entry | EMAIL    | SUCCESS |

Benefits:

* auditing
* debugging
* campaign analytics

---

# 🏢 Industry Pattern

RemindMe follows the **background job architecture used by leading tech platforms**.

| Company | Use Case         |
| ------- | ---------------- |
| Stripe  | payment retries  |
| Shopify | background jobs  |
| GitHub  | notifications    |
| Uber    | event processing |

---

# 🛠 Technology Stack

Typical implementation stack:

* Java / Spring Boot
* MySQL
* JPA / Hibernate
* Scheduled workers
* REST APIs
* SMS / Email / WhatsApp integrations

---

# 📈 Scalability

Queue-based architecture provides:

* horizontal worker scaling
* retry mechanisms
* failure recovery
* priority-based processing
* delivery tracking

---

# 🔮 Future Improvements

Possible enhancements:

* Redis / Kafka queue integration
* rate limiting per provider
* campaign analytics dashboards
* batch messaging optimization
* distributed worker clusters

---

# 📄 License

Mindful Money Securities Pvt Ltd project – **RemindMe CRM Engine**

