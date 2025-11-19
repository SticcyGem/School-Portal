-- =============================================================================
-- CREATE VIEWS FOR DATA INSPECTION
-- Run this as the 'school' user.
-- =============================================================================
SET SERVEROUTPUT ON;

-- 1. View: All Users with their Roles
CREATE OR REPLACE VIEW view_users_roles AS
SELECT 
    a.account_id,
    a.first_name || ' ' || a.last_name AS full_name,
    a.email,
    r.role_name
FROM 
    accounts a
JOIN 
    account_roles ar ON a.account_id = ar.account_id
JOIN 
    roles r ON r.role_id = ar.role_id
ORDER BY 
    a.account_id;

-- 2. View: Student Details (with Block and Course)
CREATE OR REPLACE VIEW view_student_details AS
SELECT 
    s.student_no,
    a.first_name || ' ' || a.last_name AS student_name,
    s.student_type,
    s.year_level,
    b.block_name,
    c.course_code
FROM 
    students s
JOIN 
    accounts a ON s.account_id = a.account_id
JOIN 
    blocks b ON s.block_id = b.block_id
JOIN 
    courses c ON b.course_code = c.course_code
ORDER BY 
    s.student_no;

-- 3. View: Professor Details
CREATE OR REPLACE VIEW view_professor_details AS
SELECT 
    p.professor_no,
    a.first_name || ' ' || a.last_name AS professor_name,
    p.employee_type,
    a.email
FROM 
    professors p
JOIN 
    accounts a ON p.account_id = a.account_id
ORDER BY 
    p.professor_no;

-- 4. View: Master Schedule of Classes
CREATE OR REPLACE VIEW view_master_schedule AS
SELECT 
    sec.section_id,
    sub.subject_code,
    sub.subject_name,
    p.professor_no,
    sch.day_of_week,
    sch.start_time || ' - ' || sch.end_time AS time,
    COALESCE(r.room_name, 'TBD/Async') AS room,
    at.term_name
FROM 
    sections sec
JOIN 
    subjects sub ON sec.subject_code = sub.subject_code
JOIN 
    professors p ON sec.professor_no = p.professor_no
JOIN 
    schedules sch ON sec.section_id = sch.section_id
LEFT JOIN 
    rooms r ON sch.room_id = r.room_id
JOIN 
    academic_terms at ON sec.academic_term_id = at.academic_term_id
ORDER BY 
    sec.section_id;

-- 5. View: Student Enrollment Records (The SER View)
CREATE OR REPLACE VIEW view_student_enrollments AS
SELECT 
    e.enrollment_id,
    s.student_no,
    a.first_name || ' ' || a.last_name AS student_name,
    e.status,
    sec.section_id,
    sub.subject_code,
    sub.subject_name,
    sub.units
FROM 
    enrollments e
JOIN 
    students s ON e.student_no = s.student_no
JOIN 
    accounts a ON s.account_id = a.account_id
JOIN 
    enrolled_sections es ON e.enrollment_id = es.enrollment_id
JOIN 
    sections sec ON es.section_id = sec.section_id
JOIN 
    subjects sub ON sec.subject_code = sub.subject_code
ORDER BY 
    e.enrollment_id, sec.section_id;

COMMIT;