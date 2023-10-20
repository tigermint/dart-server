ALTER TABLE team
ADD COLUMN view_count int null;

UPDATE team SET view_count = 0;
