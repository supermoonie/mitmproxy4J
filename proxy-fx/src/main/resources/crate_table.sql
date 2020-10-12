CREATE TABLE IF NOT EXISTS header
(
  id          VARCHAR(64) PRIMARY KEY ,
  `name`      VARCHAR(64),
  `value`     VARCHAR(2048),
  request_id   VARCHAR(64),
  response_id  VARCHAR(64),
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX name_idx ON header(name);
--EOF--
CREATE TABLE IF NOT EXISTS request
(
  id          VARCHAR(64) PRIMARY KEY,
  method      VARCHAR(10),
  host        VARCHAR(64),
  port        INTEGER,
  uri         VARCHAR(2048),
  http_version VARCHAR(10),
  content_type VARCHAR(100),
  content_id   VARCHAR(64),
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX host_idx ON request(host);
CREATE INDEX port_idx ON request(port);
CREATE INDEX time_created_idx ON request(timeCreated);
--EOF--
CREATE TABLE IF NOT EXISTS response
(
  id          VARCHAR(64) PRIMARY KEY,
  request_id   VARCHAR(64),
  http_version VARCHAR(10),
  status      INTEGER,
  content_type VARCHAR(64),
  content_id   VARCHAR(64),
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX request_id_idx ON response(request_id);
CREATE INDEX status_idx ON response(status);
CREATE INDEX content_type_idx ON response(content_type);
--EOF--
CREATE TABLE IF NOT EXISTS content
(
  id          VARCHAR(64) PRIMARY KEY,
  content     BLOB,
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
--EOF--
CREATE TABLE IF NOT EXISTS config
(
  id          VARCHAR(64) PRIMARY KEY,
  `key`       VARCHAR(64),
  `value`     VARCHAR(64),
  `type`      INTEGER,
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);