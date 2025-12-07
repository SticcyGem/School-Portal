-- =============================================================================
-- POPULATION SCRIPT: (Updated for Dec 2025 Testing)
-- =============================================================================
-- 1. Enrollment Window is open until Dec 31, 2025.
-- 2. Student ID is auto-generated via Trigger.
-- 3. Professor ID is auto-generated via Trigger.
-- 4. Populates 'course_subjects' (Curriculum) so Irregulars see data.
-- =============================================================================

SET search_path TO school;

-- 1. REFERENCE DATA (Colleges, Courses, School Years)
-- -----------------------------------------------------------------------------
INSERT INTO colleges (college_code, college_name)
VALUES ('CISTM', 'College of Information Systems and Technology Management')
ON CONFLICT DO NOTHING;

INSERT INTO courses (course_code, course_name, course_tier, college_code)
VALUES ('BSCS', 'Bachelor of Science in Computer Science', 'UNDERGRADUATE', 'CISTM')
ON CONFLICT DO NOTHING;

INSERT INTO school_years (sy_name, sy_start_date, sy_end_date)
VALUES ('2025-2026', '2025-08-01', '2026-05-30')
ON CONFLICT DO NOTHING; -- Added conflict check

INSERT INTO term_types (term_type_id, term_name)
VALUES ('1S', 'First Semester')
ON CONFLICT DO NOTHING; -- Added conflict check

-- Get IDs for FKs
WITH sy AS (SELECT sy_no FROM school_years WHERE sy_name = '2025-2026' LIMIT 1)
INSERT INTO academic_terms (term_name, term_start_date, term_end_date, enrollment_start_date, enrollment_end_date, sy_no, term_type_id)
SELECT 'First Semester 2025-2026', '2025-08-24', '2025-12-18', '2025-08-01', '2025-12-31', sy.sy_no, '1S'
FROM sy
WHERE NOT EXISTS (SELECT 1 FROM academic_terms WHERE term_name = 'First Semester 2025-2026'); -- Added check

-- 2. BLOCKS
-- -----------------------------------------------------------------------------
INSERT INTO blocks (course_code, year_level, block_number)
SELECT 'BSCS', 2, 2 WHERE NOT EXISTS (SELECT 1 FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2);

INSERT INTO blocks (course_code, year_level, block_number)
SELECT 'BSCS', 3, 2 WHERE NOT EXISTS (SELECT 1 FROM blocks WHERE course_code='BSCS' AND year_level=3 AND block_number=2);

-- 3. LOCATIONS
-- -----------------------------------------------------------------------------
INSERT INTO buildings (building_name) VALUES ('Gusaling Villegas') ON CONFLICT DO NOTHING;

WITH b_grounds AS (SELECT building_no FROM buildings WHERE building_name = 'University Grounds' LIMIT 1),
     b_gv      AS (SELECT building_no FROM buildings WHERE building_name = 'Gusaling Villegas' LIMIT 1)
INSERT INTO rooms (room_name, room_type, building_no)
VALUES
    ('COMP LAB 3', 'LAB', (SELECT building_no FROM b_grounds)),
    ('COMP LAB 4', 'LAB', (SELECT building_no FROM b_grounds)),
    ('GV 307', 'CLASSROOM', (SELECT building_no FROM b_gv))
ON CONFLICT DO NOTHING;

-- 4. SUBJECTS
-- -----------------------------------------------------------------------------
INSERT INTO subjects (subject_code, subject_name, lec_units, lab_units)
VALUES
    ('CSC 0212',   'Object Oriented Programming (Lecture)', 2, 0),
    ('CSC 0212.1', 'Object Oriented Programming (Laboratory)', 0, 1),
    ('CSC 0213',   'Logic Design and Digital Computer Circuits (Lecture)', 2, 0),
    ('CSC 0213.1', 'Logic Design and Digital Computer Circuits (Laboratory)', 0, 1),
    ('CSC 0224',   'Operation Research', 3, 0),
    ('CSC 0312',   'Programming Languages (Lecture)', 2, 0),
    ('CSC 0312.1', 'Programming Languages (Laboratory)', 0, 1),
    ('ICC 0105',   'Information Management (Lecture)', 2, 0),
    ('ICC 0105.1', 'Information Management (Laboratory)', 0, 1)
ON CONFLICT DO NOTHING;

-- 4.5 CURRICULUM
-- -----------------------------------------------------------------------------
INSERT INTO course_subjects (course_code, subject_code)
VALUES
    ('BSCS', 'CSC 0212'),
    ('BSCS', 'CSC 0212.1'),
    ('BSCS', 'CSC 0213'),
    ('BSCS', 'CSC 0213.1'),
    ('BSCS', 'CSC 0224'),
    ('BSCS', 'CSC 0312'),
    ('BSCS', 'CSC 0312.1'),
    ('BSCS', 'ICC 0105'),
    ('BSCS', 'ICC 0105.1')
ON CONFLICT DO NOTHING;

-- 5. ACCOUNTS & USERS (Refactored to be Idempotent)
-- -----------------------------------------------------------------------------

-- A. Dummy Student: Joshu Ramos
DO $$
    DECLARE
        v_acc_id UUID;
    BEGIN
        -- 1. Check/Insert Account
        SELECT account_id INTO v_acc_id FROM accounts WHERE email = 'jsramos2023@plm.edu.ph';

        IF v_acc_id IS NULL THEN
            INSERT INTO accounts (email, password_hash, auth_provider)
            VALUES ('jsramos2023@plm.edu.ph', '$2a$10$urO3DSWyBBPEYuh8GQzXPu4KFlksPbWsc.pnRWY6Z0vTA.jsMWTkO', 'LOCAL')
            RETURNING account_id INTO v_acc_id;

            INSERT INTO user_profiles (account_id, first_name, middle_name, last_name)
            VALUES (v_acc_id, 'Joshu', 'Salonga', 'Ramos');

            INSERT INTO account_roles (account_id, role_no)
            VALUES (v_acc_id, 1); -- 1 = STUDENT

            INSERT INTO students (account_id, student_status, education_level, student_type, year_level, course_code, block_no)
            VALUES (
                       v_acc_id, 'ENROLLED', 'UNDERGRADUATE', 'IRREGULAR', 2, 'BSCS',
                       (SELECT block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2)
                   );
        END IF;
    END $$;

-- B. Dummy Professor: James Bonifacio
DO $$
    DECLARE
        v_acc_id UUID;
    BEGIN
        -- 1. Check/Insert Account
        SELECT account_id INTO v_acc_id FROM accounts WHERE email = 'jccbonifacio2024@plm.edu.ph';

        IF v_acc_id IS NULL THEN
            INSERT INTO accounts (email, password_hash)
            VALUES ('jccbonifacio2024@plm.edu.ph', '$2a$10$urO3DSWyBBPEYuh8GQzXPu4KFlksPbWsc.pnRWY6Z0vTA.jsMWTkO')
            RETURNING account_id INTO v_acc_id;

            INSERT INTO user_profiles (account_id, first_name, last_name)
            VALUES (v_acc_id, 'James', 'Bonifacio');

            INSERT INTO account_roles (account_id, role_no)
            VALUES (v_acc_id, 2); -- 2 = PROFESSOR

            -- professor_id auto-generated by trigger
            INSERT INTO professors (account_id, employee_type)
            VALUES (v_acc_id, 'FULL_TIME');
        END IF;
    END $$;


-- 6. SECTIONS & SCHEDULES (Refactored to Check Existence)
-- -----------------------------------------------------------------------------
DO $$
    DECLARE
        v_term_no BIGINT;
        v_prof_id VARCHAR;
        v_sec_no BIGINT;
        v_room_teams BIGINT;
        v_room_lab3 BIGINT;
        v_room_lab4 BIGINT;
        v_room_field BIGINT;
        v_room_gv307 BIGINT;
    BEGIN
        SELECT academic_term_no INTO v_term_no FROM academic_terms WHERE term_name = 'First Semester 2025-2026';

        -- Get the auto-generated Professor ID for James Bonifacio
        SELECT p.professor_id INTO v_prof_id
        FROM professors p
                 JOIN accounts a ON p.account_id = a.account_id
        WHERE a.email = 'jccbonifacio2024@plm.edu.ph';

        SELECT room_no INTO v_room_teams FROM rooms WHERE room_name = 'MS TEAMS';
        SELECT room_no INTO v_room_lab3 FROM rooms WHERE room_name = 'COMP LAB 3';
        SELECT room_no INTO v_room_lab4 FROM rooms WHERE room_name = 'COMP LAB 4';
        SELECT room_no INTO v_room_field FROM rooms WHERE room_name = 'FIELD';
        SELECT room_no INTO v_room_gv307 FROM rooms WHERE room_name = 'GV 307';

        -- 1. CSC 0212 (Lec)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0212' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'ONLINE', 'CSC 0212', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('FRIDAY', '18:00:00', '20:00:00', v_sec_no, v_room_teams);
        END IF;

        -- 2. CSC 0212.1 (Lab)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0212.1' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'FACE_TO_FACE', 'CSC 0212.1', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('TUESDAY', '18:00:00', '21:00:00', v_sec_no, v_room_lab3);
        END IF;

        -- 3. CSC 0213 (Lec)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0213' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'ONLINE', 'CSC 0213', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('MONDAY', '12:00:00', '14:00:00', v_sec_no, v_room_teams);
        END IF;

        -- 4. CSC 0213.1 (Lab)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0213.1' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'FACE_TO_FACE', 'CSC 0213.1', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('MONDAY', '08:00:00', '11:00:00', v_sec_no, v_room_field);
        END IF;

        -- 5. CSC 0224 (Lec)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0224' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'ONLINE', 'CSC 0224', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('WEDNESDAY', '08:30:00', '11:30:00', v_sec_no, v_room_teams);
        END IF;

        -- 6. CSC 0312 (Lec)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0312' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'ONLINE', 'CSC 0312', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=3 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('WEDNESDAY', '18:00:00', '20:00:00', v_sec_no, v_room_teams);
        END IF;

        -- 7. CSC 0312.1 (Lab)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'CSC 0312.1' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'FACE_TO_FACE', 'CSC 0312.1', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=3 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('WEDNESDAY', '15:00:00', '18:00:00', v_sec_no, v_room_lab3);
        END IF;

        -- 8. ICC 0105 (Lec)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'ICC 0105' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'FACE_TO_FACE', 'ICC 0105', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('SATURDAY', '10:00:00', '12:00:00', v_sec_no, v_room_gv307);
        END IF;

        -- 9. ICC 0105.1 (Lab)
        IF NOT EXISTS (SELECT 1 FROM sections WHERE subject_code = 'ICC 0105.1' AND academic_term_no = v_term_no) THEN
            INSERT INTO sections (available_slots, delivery_mode, subject_code, professor_id, academic_term_no)
            VALUES (40, 'FACE_TO_FACE', 'ICC 0105.1', v_prof_id, v_term_no) RETURNING section_no INTO v_sec_no;

            INSERT INTO section_blocks (section_no, block_no) SELECT v_sec_no, block_no FROM blocks WHERE course_code='BSCS' AND year_level=2 AND block_number=2;

            INSERT INTO schedules (day_name, start_time, end_time, section_no, room_no)
            VALUES ('SATURDAY', '07:00:00', '10:00:00', v_sec_no, v_room_lab4);
        END IF;

    END $$;

-- 7. ENROLLMENT TRANSACTION (For Joshu Ramos)
-- -----------------------------------------------------------------------------
DO $$
    DECLARE
        v_student_acc UUID;
        v_term_no BIGINT;
        v_enrollment_id BIGINT;
    BEGIN
        SELECT account_id INTO v_student_acc FROM accounts WHERE email = 'jsramos2023@plm.edu.ph';
        SELECT academic_term_no INTO v_term_no FROM academic_terms WHERE term_name = 'First Semester 2025-2026';

        -- Check if already enrolled
        IF NOT EXISTS (SELECT 1 FROM enrollments WHERE account_id = v_student_acc AND academic_term_no = v_term_no) THEN
            -- Create Header
            INSERT INTO enrollments (enrolled_at, created_at, enrollment_status, account_id, academic_term_no)
            VALUES (NOW(), NOW(), 'ENROLLED', v_student_acc, v_term_no)
            RETURNING enrollment_no INTO v_enrollment_id;

            -- Create Details
            INSERT INTO enrollment_sections (enrollment_no, section_no, subject_status)
            SELECT v_enrollment_id, s.section_no, 'ENROLLED'
            FROM sections s
            WHERE s.academic_term_no = v_term_no
              AND s.subject_code IN ('CSC 0212', 'CSC 0212.1', 'CSC 0213', 'CSC 0213.1', 'CSC 0224', 'CSC 0312', 'CSC 0312.1', 'ICC 0105', 'ICC 0105.1');
        END IF;

    END $$;