CREATE TABLE installed (
  id_installed INTEGER PRIMARY KEY AUTOINCREMENT,
  package_name TEXT UNIQUE ON CONFLICT REPLACE,
  name TEXT,
  version_code INTEGER DEFAULT 0,
  version_name TEXT DEFAULT "",
  signature TEXT DEFAULT ""
);
