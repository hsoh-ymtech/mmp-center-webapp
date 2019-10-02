ALTER TABLE reflector_info ADD os_arch VARCHAR(32) NULL;
ALTER TABLE reflector_info ADD update_time timestamp DEFAULT current_timestamp() NULL;