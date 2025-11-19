-- =============================================================================
-- SAMPLE DATA INSERTION SCRIPT
-- INSTRUCTIONS: Run this as SYSDBA (SYS).
-- It will switch context to the 'SCHOOL' schema and insert data there.
-- =============================================================================
SET SERVEROUTPUT ON;

ALTER SESSION SET CURRENT_SCHEMA = SCHOOL;

BEGIN
  -- ===========================================================================
  -- 1. USER MANAGEMENT
  -- ===========================================================================
  
  -- Roles
  BEGIN INSERT INTO roles (role_name) VALUES ('Admin'); EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL; END;
  BEGIN INSERT INTO roles (role_name) VALUES ('Student'); EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL; END;
  BEGIN INSERT INTO roles (role_name) VALUES ('Professor'); EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL; END;

  -- Accounts
  INSERT INTO accounts (account_id, email, password_hash, first_name, last_name) 
  VALUES ('ADM-001', 'admin@school.edu', 'hashed_secret', 'System', 'Admin');
  
  INSERT INTO accounts (account_id, email, password_hash, first_name, last_name) 
  VALUES ('PROF-001', 'smith@school.edu', 'hashed_secret', 'John', 'Smith');
  
  INSERT INTO accounts (account_id, email, password_hash, first_name, last_name) 
  VALUES ('PROF-002', 'doe@school.edu', 'hashed_secret', 'Jane', 'Doe');

  INSERT INTO accounts (account_id, email, password_hash, first_name, last_name) 
  VALUES ('STU-001', 'emmanuel@school.edu', 'hashed_secret', 'Emmanuel', 'Perez');

  INSERT INTO accounts (account_id, email, password_hash, first_name, last_name) 
  VALUES ('STU-002', 'irreg@school.edu', 'hashed_secret', 'Ian', 'Irregular');

  -- Account_Roles
  INSERT INTO account_roles (account_id, role_id) VALUES ('ADM-001', (SELECT role_id FROM roles WHERE role_name='Admin'));
  INSERT INTO account_roles (account_id, role_id) VALUES ('PROF-001', (SELECT role_id FROM roles WHERE role_name='Professor'));
  INSERT INTO account_roles (account_id, role_id) VALUES ('PROF-002', (SELECT role_id FROM roles WHERE role_name='Professor'));
  INSERT INTO account_roles (account_id, role_id) VALUES ('STU-001', (SELECT role_id FROM roles WHERE role_name='Student'));
  INSERT INTO account_roles (account_id, role_id) VALUES ('STU-002', (SELECT role_id FROM roles WHERE role_name='Student'));

  -- ===========================================================================
  -- 2. ACADEMIC STRUCTURE
  -- ===========================================================================

  INSERT INTO colleges (college_code, college_name) VALUES ('CISTM', 'College of ISTM');
  INSERT INTO colleges (college_code, college_name) VALUES ('CET', 'College of Engineering');

  INSERT INTO courses (course_code, course_name, college_code) VALUES ('BSCS', 'BS Computer Science', 'CISTM');
  INSERT INTO courses (course_code, course_name, college_code) VALUES ('BSIT', 'BS Info Tech', 'CISTM');

  -- Blocks
  INSERT INTO blocks (block_name, course_code, year_level, block_number) 
  VALUES ('BSCS 2-1', 'BSCS', 2, 1);

  INSERT INTO blocks (block_name, course_code, year_level, block_number) 
  VALUES ('BSCS 2-2', 'BSCS', 2, 2);

  -- Students & Professors
  INSERT INTO students (account_id, student_type, year_level, block_id)
  VALUES ('STU-001', 'Regular', 2, (SELECT block_id FROM blocks WHERE block_name='BSCS 2-2'));

  INSERT INTO students (account_id, student_type, year_level, block_id)
  VALUES ('STU-002', 'Irregular', 2, (SELECT block_id FROM blocks WHERE block_name='BSCS 2-1'));

  INSERT INTO professors (professor_no, account_id, employee_type) VALUES ('P-001', 'PROF-001', 'Full-time');
  INSERT INTO professors (professor_no, account_id, employee_type) VALUES ('P-002', 'PROF-002', 'Part-time');

  -- ===========================================================================
  -- 3. TIME ENTITIES
  -- ===========================================================================
  
  INSERT INTO school_years (sy_name, start_date, end_date) 
  VALUES ('SY 2025-2026', TO_DATE('2025-08-01', 'YYYY-MM-DD'), TO_DATE('2026-05-30', 'YYYY-MM-DD'));

  INSERT INTO semesters (semester_id, semester_name) VALUES ('1ST_SEM', 'First Semester');
  INSERT INTO semesters (semester_id, semester_name) VALUES ('2ND_SEM', 'Second Semester');

  INSERT INTO academic_terms (sy_id, semester_id, term_name, term_start_date, term_end_date, enrollment_start, enrollment_end)
  VALUES (
    (SELECT sy_id FROM school_years WHERE sy_name='SY 2025-2026'),
    '1ST_SEM',
    '1st Sem 2025-2026',
    TO_DATE('2025-08-15', 'YYYY-MM-DD'),
    TO_DATE('2025-12-20', 'YYYY-MM-DD'),
    TO_DATE('2025-08-01', 'YYYY-MM-DD'),
    TO_DATE('2025-08-14', 'YYYY-MM-DD')
  );

  -- ===========================================================================
  -- 4. LOCATION ENTITIES
  -- ===========================================================================

  INSERT INTO buildings (building_name) VALUES ('CISTM Building');
  INSERT INTO buildings (building_name) VALUES ('Main Building');

  INSERT INTO rooms (room_name, room_capacity, room_type, building_id) 
  VALUES ('COMP LAB 3', 40, 'Computer Lab', (SELECT building_id FROM buildings WHERE building_name='CISTM Building'));
  
  INSERT INTO rooms (room_name, room_capacity, room_type, building_id) 
  VALUES ('GV 307', 50, 'Lecture Hall', (SELECT building_id FROM buildings WHERE building_name='Main Building'));

  INSERT INTO rooms (room_name, room_capacity, room_type, building_id) 
  VALUES ('MS TEAMS', 999, 'Virtual', NULL);

  INSERT INTO rooms (room_name, room_capacity, room_type, building_id) 
  VALUES ('FIELD', 999, 'Open Area', NULL);

  -- ===========================================================================
  -- 5. ACADEMIC OFFERINGS
  -- ===========================================================================

  INSERT INTO subjects (subject_code, subject_name, units) VALUES ('CSC 0212', 'OOP Lecture', 2);
  INSERT INTO subjects (subject_code, subject_name, units) VALUES ('CSC 0212.1', 'OOP Lab', 1);
  INSERT INTO subjects (subject_code, subject_name, units) VALUES ('CSC 0224', 'Operation Research', 3);

  -- Sections
  -- 1. OOP Lecture
  INSERT INTO sections (section_id, subject_code, professor_no, academic_term_id, available_slots)
  VALUES ('SEC-001', 'CSC 0212', 'P-001', (SELECT academic_term_id FROM academic_terms WHERE term_name='1st Sem 2025-2026'), 40);
  
  INSERT INTO section_blocks (section_id, block_id) 
  VALUES ('SEC-001', (SELECT block_id FROM blocks WHERE block_name='BSCS 2-2'));

  -- 2. OOP Lab
  INSERT INTO sections (section_id, subject_code, professor_no, academic_term_id, available_slots)
  VALUES ('SEC-002', 'CSC 0212.1', 'P-001', (SELECT academic_term_id FROM academic_terms WHERE term_name='1st Sem 2025-2026'), 40);
  
  INSERT INTO section_blocks (section_id, block_id) 
  VALUES ('SEC-002', (SELECT block_id FROM blocks WHERE block_name='BSCS 2-2'));

  -- 3. Op Research
  INSERT INTO sections (section_id, subject_code, professor_no, academic_term_id, available_slots)
  VALUES ('SEC-003', 'CSC 0224', 'P-002', (SELECT academic_term_id FROM academic_terms WHERE term_name='1st Sem 2025-2026'), 40);

  INSERT INTO section_blocks (section_id, block_id) 
  VALUES ('SEC-003', (SELECT block_id FROM blocks WHERE block_name='BSCS 2-2'));


  -- 5.3 Schedules
  -- OOP Lecture:
  INSERT INTO schedules (section_id, room_id, day_of_week, start_time, end_time)
  VALUES (
    'SEC-001',
    (SELECT room_id FROM rooms WHERE room_name='MS TEAMS'),
    'Friday',
    '18:00',
    '20:00'
  );

  -- OOP Lab:
  INSERT INTO schedules (section_id, room_id, day_of_week, start_time, end_time)
  VALUES (
    'SEC-002',
    (SELECT room_id FROM rooms WHERE room_name='COMP LAB 3'),
    'Tuesday',
    '18:00',
    '21:00'
  );

  -- Op Research:
  INSERT INTO schedules (section_id, room_id, day_of_week, start_time, end_time)
  VALUES (
    'SEC-003',
    NULL, -- No room assigned
    'Wednesday',
    '08:30',
    '11:30'
  );

  -- ===========================================================================
  -- 6. ENROLLMENT
  -- ===========================================================================

  INSERT INTO enrollments (enrollment_id, student_no, academic_term_id, status)
  VALUES (
    'ENR-001',
    (SELECT student_no FROM students WHERE account_id='STU-001'),
    (SELECT academic_term_id FROM academic_terms WHERE term_name='1st Sem 2025-2026'),
    'Enrolled'
  );

  INSERT INTO enrolled_sections (enrollment_id, section_id) VALUES ('ENR-001', 'SEC-001');
  INSERT INTO enrolled_sections (enrollment_id, section_id) VALUES ('ENR-001', 'SEC-002');
  INSERT INTO enrolled_sections (enrollment_id, section_id) VALUES ('ENR-001', 'SEC-003');
  
  COMMIT;

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
END;
/