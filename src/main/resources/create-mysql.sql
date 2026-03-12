-- ==============================
-- CASE DETAILS (Main Table)
-- ==============================
CREATE TABLE case_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_no VARCHAR(50) NOT NULL,
    case_year INT NOT NULL,
    case_type VARCHAR(50) NOT NULL,
    filing_no VARCHAR(100),
    filing_date DATE,
    registration_no VARCHAR(100),
    registration_date DATE,
    stage VARCHAR(100),
    cnr VARCHAR(100),
    subject VARCHAR(255),
    district VARCHAR(100),
    nature_of_writ VARCHAR(255),
    case_ready_status VARCHAR(100),
    coram VARCHAR(255),
    causelist VARCHAR(255),
    bench_type VARCHAR(100),
    last_listed_on DATE,
    item_no VARCHAR(50),
    last_return_date DATE,
    last_represent_date DATE,
    prayer TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE KEY uq_case (case_no, case_year, case_type)
);

-- ==============================
-- PETITIONERS
-- ==============================
CREATE TABLE petitioners (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    name VARCHAR(255),
    details TEXT,
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- RESPONDENTS
-- ==============================
CREATE TABLE respondents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    name VARCHAR(255),
    details TEXT,
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- LOWER COURT DETAILS
-- ==============================
CREATE TABLE lower_court_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    sl_no VARCHAR(50),
    lower_case_number VARCHAR(100),
    lower_court_name_district VARCHAR(255),
    order_date DATE,
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- APPLICATION DETAILS
-- ==============================
CREATE TABLE application_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    case_no VARCHAR(100),
    prayer_details TEXT,
    date_of_filing DATE,
    advocate VARCHAR(255),
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- CONNECTED MATTERS
-- ==============================
CREATE TABLE connected_matters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    case_no VARCHAR(100),
    stage VARCHAR(100),
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- CASE HEARING HISTORY
-- ==============================
CREATE TABLE case_hearing_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    judge VARCHAR(255),
    item_no VARCHAR(50),
    business_on_date DATE,
    business TEXT,
    hearing_date DATE,
    purpose_of_hearing VARCHAR(255),
    adjournment VARCHAR(255),
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- DOCUMENTS
-- ==============================
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    sl_no VARCHAR(50),
    document_no VARCHAR(100),
    document_name VARCHAR(255),
    advocate_name VARCHAR(255),
    filing_date DATE,
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- CAVEATS
-- ==============================
CREATE TABLE caveats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    sl_no VARCHAR(50),
    filing_no VARCHAR(100),
    caveat_no VARCHAR(100),
    petitioner VARCHAR(255),
    respondent VARCHAR(255),
    petitioner_counsel VARCHAR(255),
    filing_date DATE,
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- ORDERS
-- ==============================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    case_id BIGINT NOT NULL,
    sl_no VARCHAR(50),
    case_details VARCHAR(255),
    petitioner_name VARCHAR(255),
    respondent_name VARCHAR(255),
    order_date DATE,
    judge_name VARCHAR(255),
    order_copy_file_name VARCHAR(255),
    FOREIGN KEY (case_id) REFERENCES case_details(id) ON DELETE CASCADE
);

-- ==============================
-- case_reminder
-- ==============================

CREATE TABLE case_reminder (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reminder_id VARCHAR(255),
    reminder_date DATE,
    case_reference VARCHAR(255) NOT NULL,
    reminder_time TIME,
    reminder_message TEXT,
    recipient_name VARCHAR(100),
    recipient_phone VARCHAR(20),
    recipient_email VARCHAR(100),
    notification_sms VARCHAR(10),
    notification_whatsapp VARCHAR(10),
    notification_email VARCHAR(10),
    notification_inapp VARCHAR(10),
    created_at VARCHAR(50),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ==============================
-- case_appointments
-- ==============================

CREATE TABLE case_appointments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    appointment_id VARCHAR(255),
    client_name VARCHAR(100) NOT NULL,
    client_phone VARCHAR(15) NOT NULL,
    client_email VARCHAR(100) NOT NULL,
    related_case VARCHAR(255),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    appointment_status VARCHAR(50),
    appointment_remarks TEXT,
    notification_sms VARCHAR(10),
    notification_whatsapp VARCHAR(10),
    notification_email VARCHAR(10),
    notification_inapp VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_appointment_id (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ==============================
-- articles
-- ==============================

CREATE TABLE `articles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `type` VARCHAR(50) NOT NULL,             -- Article type (e.g., CASE_LAW, LEGAL_ARTICLE)
  `url` VARCHAR(1000) DEFAULT NULL,       -- Optional URL
  `author` VARCHAR(255) DEFAULT NULL,     -- Optional Author
  `date` DATE DEFAULT NULL,               -- Publication Date
  `summary` TEXT DEFAULT NULL,            -- Long description
  `tags` VARCHAR(1000) DEFAULT NULL,      -- Comma-separated tags
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_articles_title` (`title`),
  INDEX `idx_articles_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================
-- community
-- ==============================
CREATE TABLE IF NOT EXISTS posts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  author_name VARCHAR(200),
  title VARCHAR(500),
  content TEXT,
  category VARCHAR(100),
  practice_area VARCHAR(100),
  privacy VARCHAR(50) DEFAULT 'public',
  likes_count INT DEFAULT 0,
  views_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) UNIQUE
);

CREATE TABLE IF NOT EXISTS post_tags (
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (post_id, tag_id),
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS photos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  url VARCHAR(1000),
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  author_name VARCHAR(200),
  content TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_likes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  author_name VARCHAR(200),
  UNIQUE KEY (post_id, author_name),
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
