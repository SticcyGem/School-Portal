-- =============================================================================
-- Database Initialization Script for 'school' User
-- This script drops the existing 'school' user (if any) and recreates it.
-- Run this as SYSDBA (SYS) to completely wipe and recreate the 'school' user.
-- =============================================================================

WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK;

SET SERVEROUTPUT ON;

DECLARE
  l_user_count NUMBER;
  l_sessions_killed NUMBER := 0;
BEGIN
  -- 1. Check if user 'SCHOOL' exists
  SELECT COUNT(1) INTO l_user_count FROM all_users WHERE username = 'SCHOOL';

  IF l_user_count > 0 THEN
    DBMS_OUTPUT.PUT_LINE('User "school" found.');
    
    -- -------------------------------------------------------------------------
    -- FORCE DISCONNECT LOGIC
    -- -------------------------------------------------------------------------
    FOR s IN (SELECT sid, serial# FROM v$session WHERE username = 'SCHOOL') LOOP
      BEGIN
        EXECUTE IMMEDIATE 'ALTER SYSTEM KILL SESSION ''' || s.sid || ',' || s.serial# || ''' IMMEDIATE';
        l_sessions_killed := l_sessions_killed + 1;
        DBMS_OUTPUT.PUT_LINE('Killed session: ' || s.sid || ',' || s.serial#);
      EXCEPTION
        WHEN OTHERS THEN
          NULL;
      END;
    END LOOP;

    IF l_sessions_killed > 0 THEN
        DBMS_OUTPUT.PUT_LINE(l_sessions_killed || ' sessions killed. Proceeding to drop...');
    END IF;

    -- 2. Drop the existing 'school' user
    DBMS_OUTPUT.PUT_LINE('Dropping user and all objects...');
    BEGIN
        EXECUTE IMMEDIATE 'DROP USER school CASCADE';
        DBMS_OUTPUT.PUT_LINE('User "school" dropped successfully.');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -1940 THEN
                DBMS_OUTPUT.PUT_LINE('ERROR: Could not drop user. Sessions still active.');
                DBMS_OUTPUT.PUT_LINE('Please wait a moment and try again, or check for stubborn connections.');
                RAISE;
            ELSE
                RAISE;
            END IF;
    END;
    
  ELSE
    DBMS_OUTPUT.PUT_LINE('User "school" does not exist. Skipping drop.');
  END IF;

  -- 3. Re-create the user freshly
  DBMS_OUTPUT.PUT_LINE('Creating fresh user "school"...');
  EXECUTE IMMEDIATE 'CREATE USER school IDENTIFIED BY school';
  EXECUTE IMMEDIATE 'GRANT UNLIMITED TABLESPACE TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE TABLE TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE SEQUENCE TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE VIEW TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE TRIGGER TO school';
  EXECUTE IMMEDIATE 'GRANT CREATE PROCEDURE TO school';
  
  DBMS_OUTPUT.PUT_LINE('User "school" recreated and ready for initialization.');

EXCEPTION
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('FATAL ERROR during hard reset: ' || SQLERRM);
    RAISE; 
END;
/

-- =============================================================================
-- TABLE DEFINITIONS
-- =============================================================================
-- I. USER MANAGEMENT
--    1. Accounts
CREATE TABLE school.accounts (
  account_id                VARCHAR(10)                   NOT NULL
  , email                   VARCHAR(255)                  NOT NULL
  , password_hash           VARCHAR(255)                  NOT NULL
  , first_name              VARCHAR(50)                   NOT NULL
  , middle_name             VARCHAR(50)                   NULL
  , last_name               VARCHAR(50)                   NOT NULL
  , CONSTRAINT              acc_id_pk                     PRIMARY KEY(account_id)
);
--    2. Roles
CREATE TABLE school.roles (
  role_id                   NUMBER                        NOT NULL
  , role_name               VARCHAR(20)                   NOT NULL
  , CONSTRAINT              role_id_pk                    PRIMARY KEY(role_id)
);
--    3. Account_Roles
CREATE TABLE school.account_roles (
  account_id                VARCHAR(10)                   NOT NULL
  , role_id                 NUMBER                        NOT NULL
  , CONSTRAINT              acc_roles_pk                  PRIMARY KEY(account_id, role_id)
);
--    4. Students
CREATE TABLE school.students (
  student_no                NUMBER(10)                    NOT NULL
  , account_id              VARCHAR(10)                   NOT NULL
  , student_type            VARCHAR(20)                   NOT NULL
  , year_level              NUMBER(1)                     NOT NULL
  , block_id                NUMBER(5)                     NOT NULL
  , CONSTRAINT              stu_no_pk                     PRIMARY KEY(student_no)
);
--    5. Professors
CREATE TABLE school.professors (
  professor_no              VARCHAR(10)                   NOT NULL
  , account_id              VARCHAR(10)                   NOT NULL
  , employee_type           VARCHAR(20)                   NOT NULL
  , CONSTRAINT              prof_no_pk                    PRIMARY KEY(professor_no)
);

-- II. ACADEMIC STRUCTURE
--    1. Colleges
CREATE TABLE school.colleges (
  college_code              VARCHAR(10)                   NOT NULL
  , college_name            VARCHAR(100)                  NOT NULL
  , CONSTRAINT              col_code_pk                   PRIMARY KEY(college_code)
);
--    2. Courses
CREATE TABLE school.courses (
  course_code               VARCHAR(10)                   NOT NULL
  , course_name             VARCHAR(100)                  NOT NULL
  , college_code            VARCHAR(10)                   NOT NULL
  , CONSTRAINT              course_code_pk                PRIMARY KEY(course_code)
);
--    3. Blocks
CREATE TABLE school.blocks (
  block_id                  NUMBER(5)                     NOT NULL
  , block_name              VARCHAR(100)                  NOT NULL
  , course_code             VARCHAR(10)                   NOT NULL
  , year_level              NUMBER(1)                     NOT NULL
  , block_number            NUMBER(2)                     NOT NULL
  , CONSTRAINT              block_id_pk                   PRIMARY KEY(block_id)
);
--    4. Course_Subjects
CREATE TABLE school.course_subjects (
  course_code               VARCHAR(10)                   NOT NULL
  , subject_code            VARCHAR(10)                   NOT NULL
  , CONSTRAINT              crs_sub_pk                    PRIMARY KEY(course_code, subject_code)
);

-- III. ACADEMIC OFFERINGS
--    1. Subjects
CREATE TABLE school.subjects (
  subject_code              VARCHAR(10)                   NOT NULL
  , subject_name            VARCHAR(100)                  NOT NULL
  , units                   NUMBER(2)                     NOT NULL
  , CONSTRAINT              sub_code_pk                   PRIMARY KEY(subject_code)
);
--    2. Subject_Prereqs
CREATE TABLE school.subject_prereqs (
  subject_code              VARCHAR(10)                   NOT NULL
  , prereq_subject_code     VARCHAR(10)                   NOT NULL
  , CONSTRAINT              sub_prereq_pk                 PRIMARY KEY(subject_code, prereq_subject_code)
);
--    3. Sections
CREATE TABLE school.sections (
  section_id                VARCHAR(10)                   NOT NULL
  , subject_code            VARCHAR(10)                   NOT NULL
  , professor_no            VARCHAR(10)                   NOT NULL
  , academic_term_id        NUMBER(5)                     NOT NULL
  , available_slots         NUMBER(3)                     NOT NULL
  , version                 NUMBER(10)                    DEFAULT 0 NOT NULL
  , CONSTRAINT              sec_id_pk                     PRIMARY KEY(section_id)
);
--    4. Section_Blocks
CREATE TABLE school.section_blocks (
  section_id                VARCHAR(10)                   NOT NULL
  , block_id                NUMBER(5)                     NOT NULL
  , CONSTRAINT              sec_blk_pk                    PRIMARY KEY(section_id, block_id)
);
--    5. Schedules
CREATE TABLE school.schedules (
  schedule_id               NUMBER(10)                    NOT NULL
  , section_id              VARCHAR(10)                   NOT NULL
  , room_id                 NUMBER(5)                     NULL
  , day_of_week             VARCHAR(20)                   NOT NULL
  , start_time              VARCHAR(10)                   NOT NULL
  , end_time                VARCHAR(10)                   NOT NULL
  , CONSTRAINT              sched_id_pk                   PRIMARY KEY(schedule_id)
);

-- IV. ENROLLMENT
--    1. Enrollments
CREATE TABLE school.enrollments (
  enrollment_id             VARCHAR(10)                   NOT NULL
  , student_no              NUMBER(10)                    NOT NULL
  , academic_term_id        NUMBER(5)                     NOT NULL
  , enrollment_date         DATE                          DEFAULT SYSDATE NOT NULL
  , status                  VARCHAR(20)                   NOT NULL
  , CONSTRAINT              enroll_id_pk                  PRIMARY KEY(enrollment_id)
);
--    2. Enrolled_Sections
CREATE TABLE school.enrolled_sections (
  enrollment_id             VARCHAR(10)                   NOT NULL
  , section_id              VARCHAR(10)                   NOT NULL
  , final_grade             NUMBER(4,2)                   NULL
  , grade_status            VARCHAR(20)                   NULL
  , CONSTRAINT              enr_sec_pk                    PRIMARY KEY(enrollment_id, section_id)
);

-- V. TIME & LOCATION
--    1. School_Years
CREATE TABLE school.school_years (
  sy_id                     NUMBER(5)                     NOT NULL
  , sy_name                 VARCHAR(50)                   NOT NULL
  , start_date              DATE                          NOT NULL
  , end_date                DATE                          NOT NULL
  , CONSTRAINT              sy_id_pk                      PRIMARY KEY(sy_id)
);
--    2. Semesters
CREATE TABLE school.semesters (
  semester_id               VARCHAR(10)                   NOT NULL
  , semester_name           VARCHAR(50)                   NOT NULL
  , CONSTRAINT              sem_id_pk                     PRIMARY KEY(semester_id)
);
--    3. Academic_Terms
CREATE TABLE school.academic_terms (
  academic_term_id          NUMBER(5)                     NOT NULL
  , sy_id                   NUMBER(5)                     NOT NULL
  , semester_id             VARCHAR(10)                   NOT NULL
  , term_name               VARCHAR(100)                  NOT NULL
  , term_start_date         DATE                          NOT NULL
  , term_end_date           DATE                          NOT NULL
  , enrollment_start        DATE                          NOT NULL
  , enrollment_end          DATE                          NOT NULL
  , CONSTRAINT              acad_term_pk                  PRIMARY KEY(academic_term_id)
);
--    4. Buildings
CREATE TABLE school.buildings (
  building_id               NUMBER(5)                     NOT NULL
  , building_name           VARCHAR(100)                  NOT NULL
  , CONSTRAINT              bldg_id_pk                    PRIMARY KEY(building_id)
);
--    5. Rooms
CREATE TABLE school.rooms (
  room_id                   NUMBER(5)                     NOT NULL
  , room_name               VARCHAR(50)                   NOT NULL
  , room_capacity           NUMBER(3)                     NOT NULL
  , room_type               VARCHAR(50)                   NOT NULL
  , building_id             NUMBER(5)                     NULL
  , CONSTRAINT              room_id_pk                    PRIMARY KEY(room_id)
);

-- =============================================================================
-- 3. CONSTRAINTS
-- =============================================================================

-- I. USER MANAGEMENT
--    1. Accounts
ALTER TABLE school.accounts 
    ADD CONSTRAINT acc_email_uk 
    UNIQUE (email);
ALTER TABLE school.accounts 
    ADD CONSTRAINT email_fmt_ck 
    CHECK (REGEXP_LIKE(email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'));
ALTER TABLE school.accounts 
    ADD CONSTRAINT email_lower_ck 
    CHECK (email = LOWER(email));
ALTER TABLE school.accounts 
    ADD CONSTRAINT acc_id_upper_ck 
    CHECK (account_id = UPPER(account_id));

--    2. Roles
ALTER TABLE school.roles 
    ADD CONSTRAINT role_name_uk 
    UNIQUE (role_name);

--    3. Account_Roles
ALTER TABLE school.account_roles 
    ADD CONSTRAINT acc_roles_acc_fk 
    FOREIGN KEY (account_id) 
    REFERENCES school.accounts(account_id);
ALTER TABLE school.account_roles 
    ADD CONSTRAINT acc_roles_role_fk 
    FOREIGN KEY (role_id) 
    REFERENCES school.roles(role_id);

--    4. Students
ALTER TABLE school.students 
    ADD CONSTRAINT stu_acc_uk 
    UNIQUE (account_id);
ALTER TABLE school.students 
    ADD CONSTRAINT stu_acc_fk 
    FOREIGN KEY (account_id) 
    REFERENCES school.accounts(account_id);
ALTER TABLE school.students 
    ADD CONSTRAINT stu_block_fk 
    FOREIGN KEY (block_id) 
    REFERENCES school.blocks(block_id);
ALTER TABLE school.students 
    ADD CONSTRAINT stu_type_ck 
    CHECK (student_type IN ('Regular', 'Irregular'));
ALTER TABLE school.students 
    ADD CONSTRAINT stu_year_ck 
    CHECK (year_level BETWEEN 1 AND 10);

--    5. Professors
ALTER TABLE school.professors 
    ADD CONSTRAINT prof_acc_uk 
    UNIQUE (account_id);
ALTER TABLE school.professors 
    ADD CONSTRAINT prof_acc_fk 
    FOREIGN KEY (account_id) 
    REFERENCES school.accounts(account_id);
ALTER TABLE school.professors 
    ADD CONSTRAINT prof_type_ck 
    CHECK (employee_type IN ('Full-time', 'Part-time'));
ALTER TABLE school.professors 
    ADD CONSTRAINT prof_no_upper_ck 
    CHECK (professor_no = UPPER(professor_no));

-- II. ACADEMIC STRUCTURE
--    1. Colleges
ALTER TABLE school.colleges 
    ADD CONSTRAINT col_code_upper_ck 
    CHECK (college_code = UPPER(college_code));

--    2. Courses
ALTER TABLE school.courses 
    ADD CONSTRAINT course_col_fk 
    FOREIGN KEY (college_code) 
    REFERENCES school.colleges(college_code);
ALTER TABLE school.courses 
    ADD CONSTRAINT course_upper_ck 
    CHECK (course_code = UPPER(course_code));

--    3. Blocks
ALTER TABLE school.blocks 
    ADD CONSTRAINT block_course_fk 
    FOREIGN KEY (course_code) 
    REFERENCES school.courses(course_code);
ALTER TABLE school.blocks 
    ADD CONSTRAINT block_nat_key_uk 
    UNIQUE (course_code, year_level, block_number);
ALTER TABLE school.blocks 
    ADD CONSTRAINT blk_num_pos_ck 
    CHECK (block_number > 0);

--    4. Course_Subjects
ALTER TABLE school.course_subjects 
    ADD CONSTRAINT crs_sub_crs_fk 
    FOREIGN KEY (course_code) 
    REFERENCES school.courses(course_code);
ALTER TABLE school.course_subjects 
    ADD CONSTRAINT crs_sub_sub_fk 
    FOREIGN KEY (subject_code) 
    REFERENCES school.subjects(subject_code);

-- III. ACADEMIC OFFERINGS
--    1. Subjects
ALTER TABLE school.subjects 
    ADD CONSTRAINT sub_units_ck 
    CHECK (units > 0);
ALTER TABLE school.subjects 
    ADD CONSTRAINT sub_code_upper_ck 
    CHECK (subject_code = UPPER(subject_code));
    
--    2. Subject_Prereqs
ALTER TABLE school.subject_prereqs 
    ADD CONSTRAINT sub_prereq_sub_fk 
    FOREIGN KEY (subject_code) 
    REFERENCES school.subjects(subject_code);
ALTER TABLE school.subject_prereqs 
    ADD CONSTRAINT sub_prereq_pre_fk 
    FOREIGN KEY (prereq_subject_code) 
    REFERENCES school.subjects(subject_code);

--    3. Sections
ALTER TABLE school.sections 
    ADD CONSTRAINT sec_sub_fk 
    FOREIGN KEY (subject_code) 
    REFERENCES school.subjects(subject_code);
ALTER TABLE school.sections 
    ADD CONSTRAINT sec_prof_fk 
    FOREIGN KEY (professor_no) 
    REFERENCES school.professors(professor_no);
ALTER TABLE school.sections 
    ADD CONSTRAINT sec_term_fk 
    FOREIGN KEY (academic_term_id) 
    REFERENCES school.academic_terms(academic_term_id);
ALTER TABLE school.sections 
    ADD CONSTRAINT sec_slots_ck 
    CHECK (available_slots >= 0);
ALTER TABLE school.sections 
    ADD CONSTRAINT sec_id_upper_ck 
    CHECK (section_id = UPPER(section_id));

--    4. Section_Blocks
ALTER TABLE school.section_blocks 
    ADD CONSTRAINT sec_blk_sec_fk 
    FOREIGN KEY (section_id) 
    REFERENCES school.sections(section_id);
ALTER TABLE school.section_blocks 
    ADD CONSTRAINT sec_blk_blk_fk 
    FOREIGN KEY (block_id) 
    REFERENCES school.blocks(block_id);

--    5. Schedules
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_sec_fk 
    FOREIGN KEY (section_id) 
    REFERENCES school.sections(section_id);
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_room_fk 
    FOREIGN KEY (room_id) 
    REFERENCES school.rooms(room_id);
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_day_ck 
    CHECK (day_of_week IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'));
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_time_uk 
    UNIQUE (section_id, day_of_week, start_time);
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_logic_ck 
    CHECK (end_time > start_time);
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_start_fmt_ck 
    CHECK (REGEXP_LIKE(start_time, '^([0-1][0-9]|2[0-3]):[0-5][0-9]$'));
ALTER TABLE school.schedules 
    ADD CONSTRAINT sched_end_fmt_ck 
    CHECK (REGEXP_LIKE(end_time, '^([0-1][0-9]|2[0-3]):[0-5][0-9]$'));

-- IV. ENROLLMENT
--    1. Enrollments
ALTER TABLE school.enrollments 
    ADD CONSTRAINT enroll_stu_fk 
    FOREIGN KEY (student_no) 
    REFERENCES school.students(student_no);
ALTER TABLE school.enrollments 
    ADD CONSTRAINT enroll_term_fk 
    FOREIGN KEY (academic_term_id) 
    REFERENCES school.academic_terms(academic_term_id);
ALTER TABLE school.enrollments 
    ADD CONSTRAINT enroll_stat_ck 
    CHECK (status IN ('Pending', 'Enrolled', 'Cancelled', 'Withdrawn'));
ALTER TABLE school.enrollments 
    ADD CONSTRAINT enroll_id_upper_ck 
    CHECK (enrollment_id = UPPER(enrollment_id));

--    2. Enrolled_Sections
ALTER TABLE school.enrolled_sections 
    ADD CONSTRAINT enr_sec_enr_fk 
    FOREIGN KEY (enrollment_id) 
    REFERENCES school.enrollments(enrollment_id);
ALTER TABLE school.enrolled_sections 
    ADD CONSTRAINT enr_sec_sec_fk 
    FOREIGN KEY (section_id) 
    REFERENCES school.sections(section_id);
ALTER TABLE school.enrolled_sections 
    ADD CONSTRAINT grad_stat_ck 
    CHECK (grade_status IN ('In_Progress', 'Passed', 'Failed', 'Withdrawn', 'Incomplete', 'Dropped'));
ALTER TABLE school.enrolled_sections 
    ADD CONSTRAINT enr_sec_grade_range_ck 
    CHECK (final_grade IS NULL OR (final_grade >= 1.00 AND final_grade <= 5.00));

-- V. TIME & LOCATION
--    1. School_Years
ALTER TABLE school.school_years 
    ADD CONSTRAINT sy_name_uk 
    UNIQUE (sy_name);
ALTER TABLE school.school_years 
    ADD CONSTRAINT sy_dates_ck 
    CHECK (end_date > start_date);

--    2. Semesters
ALTER TABLE school.semesters 
    ADD CONSTRAINT sem_id_upper_ck 
    CHECK (semester_id = UPPER(semester_id));

--    3. Academic_Terms
ALTER TABLE school.academic_terms 
    ADD CONSTRAINT term_sy_fk 
    FOREIGN KEY (sy_id) 
    REFERENCES school.school_years(sy_id);
ALTER TABLE school.academic_terms 
    ADD CONSTRAINT term_sem_fk 
    FOREIGN KEY (semester_id) 
    REFERENCES school.semesters(semester_id);
ALTER TABLE school.academic_terms 
    ADD CONSTRAINT term_unique_uk 
    UNIQUE (sy_id, semester_id);

--    4. Buildings

--    5. Rooms
ALTER TABLE school.rooms 
    ADD CONSTRAINT room_bldg_fk 
    FOREIGN KEY (building_id) 
    REFERENCES school.buildings(building_id);
ALTER TABLE school.rooms 
    ADD CONSTRAINT room_cap_ck 
    CHECK (room_capacity >= 0);

-- =============================================================================
-- 4. SEQUENCES
-- =============================================================================

-- role_seq
CREATE SEQUENCE school.role_seq     START WITH 1 INCREMENT BY 1 NOCACHE;
-- block_seq
CREATE SEQUENCE school.block_seq    START WITH 1 INCREMENT BY 1 NOCACHE;
-- sy_seq
CREATE SEQUENCE school.sy_seq       START WITH 1 INCREMENT BY 1 NOCACHE;
-- term_seq
CREATE SEQUENCE school.term_seq     START WITH 1 INCREMENT BY 1 NOCACHE;
-- bldg_seq
CREATE SEQUENCE school.bldg_seq     START WITH 1 INCREMENT BY 1 NOCACHE;
-- room_seq
CREATE SEQUENCE school.room_seq     START WITH 1 INCREMENT BY 1 NOCACHE;
-- sched_seq
CREATE SEQUENCE school.sched_seq    START WITH 1 INCREMENT BY 1 NOCACHE;
-- student_seq
CREATE SEQUENCE school.student_seq  START WITH 1 INCREMENT BY 1 MAXVALUE 99999 NOCYCLE NOCACHE;

-- =============================================================================
-- 5. TRIGGERS
-- =============================================================================

-- trg_role_id
CREATE OR REPLACE TRIGGER school.trg_role_id
BEFORE INSERT ON school.roles FOR EACH ROW
BEGIN
  IF :NEW.role_id IS NULL THEN
    SELECT school.role_seq.NEXTVAL INTO :NEW.role_id FROM dual;
  END IF;
END;
/

-- trg_block_id
CREATE OR REPLACE TRIGGER school.trg_block_id
BEFORE INSERT ON school.blocks FOR EACH ROW
BEGIN
  IF :NEW.block_id IS NULL THEN
    SELECT school.block_seq.NEXTVAL INTO :NEW.block_id FROM dual;
  END IF;
END;
/

-- trg_sy_id
CREATE OR REPLACE TRIGGER school.trg_sy_id
BEFORE INSERT ON school.school_years FOR EACH ROW
BEGIN
  IF :NEW.sy_id IS NULL THEN
    SELECT school.sy_seq.NEXTVAL INTO :NEW.sy_id FROM dual;
  END IF;
END;
/

-- trg_term_id
CREATE OR REPLACE TRIGGER school.trg_term_id
BEFORE INSERT ON school.academic_terms FOR EACH ROW
BEGIN
  IF :NEW.academic_term_id IS NULL THEN
    SELECT school.term_seq.NEXTVAL INTO :NEW.academic_term_id FROM dual;
  END IF;
END;
/

-- trg_bldg_id
CREATE OR REPLACE TRIGGER school.trg_bldg_id
BEFORE INSERT ON school.buildings FOR EACH ROW
BEGIN
  IF :NEW.building_id IS NULL THEN
    SELECT school.bldg_seq.NEXTVAL INTO :NEW.building_id FROM dual;
  END IF;
END;
/

-- trg_room_id
CREATE OR REPLACE TRIGGER school.trg_room_id
BEFORE INSERT ON school.rooms FOR EACH ROW
BEGIN
  IF :NEW.room_id IS NULL THEN
    SELECT school.room_seq.NEXTVAL INTO :NEW.room_id FROM dual;
  END IF;
END;
/

-- trg_sched_id
CREATE OR REPLACE TRIGGER school.trg_sched_id
BEFORE INSERT ON school.schedules FOR EACH ROW
BEGIN
  IF :NEW.schedule_id IS NULL THEN
    SELECT school.sched_seq.NEXTVAL INTO :NEW.schedule_id FROM dual;
  END IF;
END;
/

-- trg_student_no
CREATE OR REPLACE TRIGGER school.trg_student_no
BEFORE INSERT ON school.students
FOR EACH ROW
BEGIN
  IF :NEW.student_no IS NULL THEN
      :NEW.student_no := TO_NUMBER(
                            TO_CHAR(EXTRACT(YEAR FROM SYSDATE)) ||
                            LPAD(school.student_seq.NEXTVAL, 5, '0')
                         );
  END IF;
END;
/

-- =============================================================================
-- 6. FINAL COMMIT
-- =============================================================================
COMMIT;