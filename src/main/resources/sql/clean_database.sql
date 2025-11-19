-- =============================================================================
-- DATABASE CLEANUP SCRIPT
-- WARNING: This will DROP ALL objects in the current user's schema.
-- Run this only when you want to completely reset the 'school' user's data.
-- =============================================================================
SET SERVEROUTPUT ON;

BEGIN
  -- 1. Drop all Tables
  FOR t IN (SELECT table_name FROM user_tables) LOOP
    EXECUTE IMMEDIATE 'DROP TABLE ' || t.table_name || ' CASCADE CONSTRAINTS PURGE';
    DBMS_OUTPUT.PUT_LINE('Dropped table: ' || t.table_name);
  END LOOP;

  -- 2. Drop all Sequences
  FOR s IN (SELECT sequence_name FROM user_sequences) LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE ' || s.sequence_name;
    DBMS_OUTPUT.PUT_LINE('Dropped sequence: ' || s.sequence_name);
  END LOOP;

  -- 3. Drop all Triggers
  FOR tr IN (SELECT trigger_name FROM user_triggers) LOOP
    BEGIN
      EXECUTE IMMEDIATE 'DROP TRIGGER ' || tr.trigger_name;
      DBMS_OUTPUT.PUT_LINE('Dropped trigger: ' || tr.trigger_name);
    EXCEPTION
      WHEN OTHERS THEN
        NULL;
    END;
  END LOOP;

  -- 4. Drop all Views
  FOR v IN (SELECT view_name FROM user_views) LOOP
    EXECUTE IMMEDIATE 'DROP VIEW ' || v.view_name;
    DBMS_OUTPUT.PUT_LINE('Dropped view: ' || v.view_name);
  END LOOP;

  -- 5. Drop all Stored Procedures/Functions
  FOR p IN (SELECT object_name, object_type FROM user_objects WHERE object_type IN ('PROCEDURE', 'FUNCTION')) LOOP
    EXECUTE IMMEDIATE 'DROP ' || p.object_type || ' ' || p.object_name;
    DBMS_OUTPUT.PUT_LINE('Dropped ' || p.object_type || ': ' || p.object_name);
  END LOOP;

  COMMIT;
  DBMS_OUTPUT.PUT_LINE('Database cleanup complete. Schema is empty.');
END;
/