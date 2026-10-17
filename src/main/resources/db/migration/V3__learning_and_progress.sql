CREATE TABLE learning_track (
    id UUID PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    position INTEGER NOT NULL CHECK (position >= 0)
);

CREATE TABLE learning_module (
    id UUID PRIMARY KEY,
    track_id UUID NOT NULL REFERENCES learning_track(id),
    title VARCHAR(150) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    position INTEGER NOT NULL CHECK (position >= 0)
);
CREATE INDEX idx_module_track_position ON learning_module(track_id, position);

CREATE TABLE activity (
    id UUID PRIMARY KEY,
    module_id UUID NOT NULL REFERENCES learning_module(id),
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
CREATE INDEX idx_activity_module_position ON activity(module_id, position);

CREATE TABLE student_activity_attempt (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user(id),
    activity_id UUID NOT NULL REFERENCES activity(id),
    answer INTEGER NOT NULL,
    is_correct BOOLEAN NOT NULL,
    score INTEGER NOT NULL CHECK (score BETWEEN 0 AND 100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_attempt_student_created ON student_activity_attempt(student_id, created_at);
CREATE INDEX idx_attempt_activity ON student_activity_attempt(activity_id);

CREATE TABLE student_activity_completion (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user(id),
    activity_id UUID NOT NULL REFERENCES activity(id),
    points INTEGER NOT NULL CHECK (points BETWEEN 0 AND 100),
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (student_id, activity_id)
);

CREATE TABLE student_achievement (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES app_user(id),
    code VARCHAR(40) NOT NULL CHECK (code IN ('FIRST_ACTIVITY', 'FIRST_TRACK')),
    achieved_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE (student_id, code)
);
