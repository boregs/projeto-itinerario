-- Itinerário — schema PostgreSQL consolidado
--
-- Este arquivo descreve o schema atual das migrations V1, V2 e V3.
-- Use em um banco vazio para criação manual/inspeção.
-- Em ambientes da aplicação, prefira o Flyway:
-- src/main/resources/db/migration/V1__identity.sql
-- src/main/resources/db/migration/V2__mentorship_and_audit.sql
-- src/main/resources/db/migration/V3__learning_and_progress.sql
--
-- Não execute este arquivo em um banco que já tenha migrations aplicadas.

BEGIN;

CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'MENTOR', 'ADMIN')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'BLOCKED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE student_profile (
    user_id UUID PRIMARY KEY REFERENCES app_user (id) ON DELETE CASCADE,
    school_year INTEGER NOT NULL CHECK (school_year BETWEEN 1 AND 3),
    interests VARCHAR(500) NOT NULL
);

CREATE TABLE mentor_profile (
    user_id UUID PRIMARY KEY REFERENCES app_user (id) ON DELETE CASCADE,
    bio VARCHAR(1000) NOT NULL,
    expertise_areas VARCHAR(500) NOT NULL,
    availability VARCHAR(500) NOT NULL,
    experience VARCHAR(1000) NOT NULL,
    approval_status VARCHAR(20) NOT NULL CHECK (approval_status IN ('PENDING_APPROVAL', 'APPROVED', 'REJECTED')),
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by UUID REFERENCES app_user (id)
);

CREATE TABLE mentorship_request (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user (id),
    subject VARCHAR(150) NOT NULL,
    area VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    objective VARCHAR(500) NOT NULL,
    preferred_schedule VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('OPEN', 'MATCHED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_request_student_created ON mentorship_request (student_id, created_at);
CREATE INDEX idx_request_status_created ON mentorship_request (status, created_at);

CREATE TABLE mentorship (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE REFERENCES mentorship_request (id),
    student_id UUID NOT NULL REFERENCES app_user (id),
    mentor_id UUID NOT NULL REFERENCES app_user (id),
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('MATCHED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    summary VARCHAR(2000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    CHECK (student_id <> mentor_id),
    CHECK (
        (status = 'COMPLETED' AND completed_at IS NOT NULL)
        OR (status <> 'COMPLETED' AND completed_at IS NULL)
    )
);

CREATE INDEX idx_mentorship_student_created ON mentorship (student_id, created_at);
CREATE INDEX idx_mentorship_mentor_created ON mentorship (mentor_id, created_at);

CREATE TABLE audit_event (
    id UUID PRIMARY KEY,
    actor_id UUID REFERENCES app_user (id),
    target_id UUID NOT NULL,
    action VARCHAR(60) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_created ON audit_event (created_at);

CREATE TABLE learning_track (
    id UUID PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    position INTEGER NOT NULL CHECK (position >= 0)
);

CREATE TABLE learning_module (
    id UUID PRIMARY KEY,
    track_id UUID NOT NULL REFERENCES learning_track (id),
    title VARCHAR(150) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    position INTEGER NOT NULL CHECK (position >= 0)
);

CREATE INDEX idx_module_track_position ON learning_module (track_id, position);

CREATE TABLE activity (
    id UUID PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES learning_module (id),
    title VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL CHECK (type IN ('MULTIPLE_CHOICE', 'TRUE_FALSE', 'LOGIC_CHALLENGE')),
    question VARCHAR(2000) NOT NULL,
    options_json VARCHAR(5000) NOT NULL,
    correct_answer INTEGER NOT NULL CHECK (correct_answer >= 0 AND correct_answer < 6),
    explanation VARCHAR(2000) NOT NULL,
    points INTEGER NOT NULL CHECK (points BETWEEN 0 AND 100),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    position INTEGER NOT NULL CHECK (position >= 0)
);

CREATE INDEX idx_activity_module_position ON activity (module_id, position);

CREATE TABLE student_activity_attempt (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user (id),
    activity_id UUID NOT NULL REFERENCES activity (id),
    answer INTEGER NOT NULL,
    is_correct BOOLEAN NOT NULL,
    score INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_attempt_student_created ON student_activity_attempt (student_id, created_at);
CREATE INDEX idx_attempt_activity ON student_activity_attempt (activity_id);

CREATE TABLE student_activity_completion (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user (id),
    activity_id UUID NOT NULL REFERENCES activity (id),
    points INTEGER NOT NULL CHECK (points BETWEEN 0 AND 100),
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (student_id, activity_id)
);

CREATE TABLE student_achievement (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user (id),
    code VARCHAR(40) NOT NULL CHECK (code IN ('FIRST_ACTIVITY', 'FIRST_TRACK')),
    achieved_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (student_id, code)
);

COMMIT;
