ALTER TABLE reflector_info ADD country CHAR(2) DEFAULT '00' NOT NULL;
ALTER TABLE reflector_info ADD os VARCHAR(16);
ALTER TABLE reflector_info ADD os_version VARCHAR(16);