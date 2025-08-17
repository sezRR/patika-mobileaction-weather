ALTER TABLE pollutions
    ADD date date;

ALTER TABLE pollutions
    ALTER COLUMN date SET NOT NULL;

ALTER TABLE pollutions
DROP
COLUMN filter_end;

ALTER TABLE pollutions
DROP
COLUMN filter_start;