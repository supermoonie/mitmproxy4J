CREATE TABLE IF NOT EXISTS header
(
  id          VARCHAR(64) PRIMARY KEY ,
  `name`      VARCHAR(64),
  `value`     VARCHAR(2048),
  requestId   VARCHAR(64),
  responseId  VARCHAR(64),
  timeCreated DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
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
  httpVersion VARCHAR(10),
  contentType VARCHAR(100),
  contentId   VARCHAR(64),
  timeCreated DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX host_idx ON request(host);
CREATE INDEX port_idx ON request(port);
CREATE INDEX time_created_idx ON request(timeCreated);
--EOF--
CREATE TABLE IF NOT EXISTS response
(
  id          VARCHAR(64) PRIMARY KEY,
  requestId   VARCHAR(64),
  httpVersion VARCHAR(10),
  status      INTEGER,
  contentType VARCHAR(64),
  contentId   VARCHAR(64),
  timeCreated DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX request_id_idx ON response(requestId);
CREATE INDEX status_idx ON response(status);
CREATE INDEX content_type_idx ON response(contentType);
--EOF--
CREATE TABLE IF NOT EXISTS content
(
  id          VARCHAR(64) PRIMARY KEY,
  content     BLOB,
  timeCreated DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
--EOF--
CREATE TABLE IF NOT EXISTS config
(
  id          VARCHAR(64) PRIMARY KEY,
  `key`       VARCHAR(64),
  `value`     VARCHAR(64),
  timeCreated DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);