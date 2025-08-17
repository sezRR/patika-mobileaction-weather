ALTER TABLE pollutions
DROP
COLUMN filter_end;

ALTER TABLE pollutions
DROP
COLUMN filter_start;

ALTER TABLE pollutions
    ADD filter_end date;

ALTER TABLE pollutions
    ADD filter_start date;