CREATE TABLE IF NOT EXISTS header
(
  id            VARCHAR(64) PRIMARY KEY ,
  `name`        VARCHAR(64),
  `value`       VARCHAR(2048),
  request_id    VARCHAR(64),
  response_id   VARCHAR(64),
  time_created  DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX name_idx ON header(name);
--EOF--
CREATE TABLE IF NOT EXISTS request
(
  id            VARCHAR(64) PRIMARY KEY,
  method        VARCHAR(10),
  host          VARCHAR(64),
  port          INTEGER,
  uri           VARCHAR(2048),
  http_version  VARCHAR(10),
  content_type  VARCHAR(100),
  content_id    VARCHAR(64),
  start_time    BIGINT,
  end_time      BIGINT,
  size          INTEGER,
  time_created  DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX host_idx ON request(host);
CREATE INDEX port_idx ON request(port);
CREATE INDEX time_created_idx ON request(time_created);
--EOF--
CREATE TABLE IF NOT EXISTS response
(
  id            VARCHAR(64) PRIMARY KEY,
  request_id    VARCHAR(64),
  http_version  VARCHAR(10),
  status        INTEGER,
  content_type  VARCHAR(64),
  content_id    VARCHAR(64),
  start_time    BIGINT,
  end_time      BIGINT,
  size          INTEGER,
  time_created  DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX request_id_idx ON response(request_id);
CREATE INDEX status_idx ON response(status);
CREATE INDEX content_type_idx ON response(content_type);
--EOF--
CREATE TABLE IF NOT EXISTS content
(
  id            VARCHAR(64) PRIMARY KEY,
  content       BLOB,
  time_created  DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
--EOF--
CREATE TABLE IF NOT EXISTS connection_overview
(
    id                      VARCHAR(64) PRIMARY KEY,
    request_id              VARCHAR(64),
    client_host             VARCHAR(64),
    client_port             INTEGER,
    dns_server              VARCHAR(64),
    remote_ip               VARCHAR(16),
    client_protocol         VARCHAR(16),
    client_cipher_suite     VARCHAR(32),
    client_session_id       VARCHAR(64),
    server_session_id       VARCHAR(64),
    server_protocol         VARCHAR(16),
    server_cipher_suite     VARCHAR(32),
    use_second_proxy        INTEGER,
    second_proxy_host       VARCHAR(32),
    second_proxy_port       INTEGER,
    dns_start_time          BIGINT,
    dns_end_time            BIGINT,
    time_created            DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE UNIQUE INDEX request_id_idx ON connection_overview(request_id);
--EOF--
CREATE TABLE IF NOT EXISTS certificate_info
(
    id                              VARCHAR(64) PRIMARY KEY ,
    request_id                      VARCHAR(64),
    response_id                     VARCHAR(64),
    serial_number                   VARCHAR(128),
    issuer_common_name	            VARCHAR(128),
    issuer_organization_department	VARCHAR(128),
    issuer_organization_name	    VARCHAR(128),
    issuer_locality_name	        VARCHAR(128),
    issuer_state_name	            VARCHAR(128),
    issuer_country	                VARCHAR(64),
    subject_common_name	            VARCHAR(128),
    subject_organization_department	VARCHAR(128),
    subject_organization_name	    VARCHAR(128),
    subject_locality_name	        VARCHAR(128),
    subject_state_name	            VARCHAR(128),
    subject_country	                VARCHAR(64),
    type                            VARCHAR(32),
    version                         INTEGER,
    sig_alg_name	                VARCHAR(64),
    not_valid_before                DATETIME,
    not_valid_after                 DATETIME,
    sha_one                         VARCHAR(128),
    sha_two_five_six                VARCHAR(256),
    full_detail	                    VARCHAR(5120),
    time_created                    DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);
CREATE INDEX request_id_idx ON certificate_info(request_id);
--EOF--
CREATE TABLE IF NOT EXISTS config
(
  id          VARCHAR(64) PRIMARY KEY,
  `key`       VARCHAR(64),
  `value`     VARCHAR(64),
  `type`      INTEGER,
  time_created DATETIME DEFAULT (datetime(CURRENT_TIMESTAMP, 'localtime'))
);