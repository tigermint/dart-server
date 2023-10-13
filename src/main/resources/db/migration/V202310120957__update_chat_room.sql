ALTER TABLE chat_room
ADD COLUMN created_time datetime null,
ADD COLUMN last_modified_time datetime null;

UPDATE chat_room
SET created_time = CASE WHEN latest_chat_message_time IS NULL THEN NOW() ELSE latest_chat_message_time END,
    last_modified_time = CASE WHEN latest_chat_message_time IS NULL THEN NOW() ELSE latest_chat_message_time END;

ALTER TABLE chat_room
DROP COLUMN latest_chat_message_time;
