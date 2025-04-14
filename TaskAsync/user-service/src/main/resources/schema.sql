CREATE TABLE IF NOT EXISTS `users` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keycloak_subject_id VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    full_name VARCHAR(510),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `user_tasks` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    task_status VARCHAR(50) DEFAULT 'TODO',
    role_in_task VARCHAR(50), -- e.g., 'ASSIGNEE', 'REVIEWER', 'OBSERVER'
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `user_notifications` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_type VARCHAR(100),
    message TEXT NOT NULL,        -- Full notification content
    metadata JSON,                -- Flexible storage for any additional data
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
