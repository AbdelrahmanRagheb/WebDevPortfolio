CREATE TABLE IF NOT EXISTS task (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      created_at DATETIME,
                      updated_at DATETIME,
                      creator_id BIGINT,
                      title VARCHAR(255) UNIQUE,
                      description TEXT,
                      due_date DATETIME,
                      priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
                      status ENUM('TODO', 'IN_PROGRESS', 'DONE') DEFAULT 'TODO',
                      category ENUM('WORK', 'PERSONAL', 'OTHER'),
                      estimated_effort BIGINT,
                      recurrence_rule BIGINT,
                      assigned_users JSON,
    UNIQUE (title)
);
CREATE TABLE IF NOT EXISTS task_comments (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               created_at DATETIME,
                               updated_at DATETIME,
                               commenter_id BIGINT,
                               content TEXT,
                               task_id BIGINT,
                               parent_comment_id BIGINT,
                               FOREIGN KEY (task_id) REFERENCES task(id) ON DELETE CASCADE,
                               FOREIGN KEY (parent_comment_id) REFERENCES task_comments(id) ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS task_history (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              updated_at DATETIME,
                              task_id BIGINT,
                              changed_by_user_id BIGINT,
                              change_details JSON,
                              FOREIGN KEY (task_id) REFERENCES task(id)
);
CREATE TABLE IF NOT EXISTS task_dependencies (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   created_at DATETIME,
                                   updated_at DATETIME,
                                   task_id BIGINT,
                                   depends_on_task_id BIGINT,
                                   FOREIGN KEY (task_id) REFERENCES task(id),
                                   FOREIGN KEY (depends_on_task_id) REFERENCES task(id)
);