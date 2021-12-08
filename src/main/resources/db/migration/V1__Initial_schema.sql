CREATE TABLE users (
  id uuid NOT NULL,
  email varchar(255) NOT NULL,
  firstname varchar(255),
  lastname varchar(255),
  gender varchar(255),
  birthdate timestamp NOT NULL,
  consent boolean NOT NULL,
  created_date timestamp NOT NULL,
  created_by varchar(255),
  last_modified_date  timestamp NOT NULL,
  last_modified_by varchar(255),
  version integer NOT NULL,
  CONSTRAINT users_id PRIMARY KEY (id),
  CONSTRAINT users_email_key UNIQUE (email)
);

CREATE TABLE campaigns (
  id uuid NOT NULL,
  name varchar(255) NOT NULL,
  created_date timestamp NOT NULL,
  created_by varchar(255),
  last_modified_date  timestamp NOT NULL,
  last_modified_by varchar(255),
  version integer NOT NULL,
  CONSTRAINT campaigns_id PRIMARY KEY (id)
);

CREATE TABLE subscriptions (
  user_id uuid NOT NULL,
  campaign_id uuid NOT NULL,
  CONSTRAINT user_id_campaign_id PRIMARY KEY (user_id, campaign_id),
  CONSTRAINT fk_campaign_id FOREIGN KEY (campaign_id)
      REFERENCES campaigns (id) MATCH SIMPLE
      ON UPDATE NO ACTION
      ON DELETE NO ACTION,
  CONSTRAINT fk_user_id FOREIGN KEY (user_id)
      REFERENCES users (id) MATCH SIMPLE
      ON UPDATE NO ACTION
      ON DELETE NO ACTION
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO users (id, email, firstname, lastname, gender,  birthdate, consent, created_date, created_by, last_modified_date, version)
	VALUES ('fc2ff7a9-59f4-4a33-918f-f974a7668dc1', 'john.doe@company.com', 'John', 'Doe', 'Male', current_timestamp, true, current_timestamp, 'admin', current_timestamp, 1);

INSERT INTO users (id, email, firstname, lastname, gender,  birthdate, consent, created_date, created_by, last_modified_date, version)
	VALUES ('97eeeec7-52a1-4963-bca5-37b4c4d263db', 'jane.doe@company.com', 'Jane', 'Doe', 'Female', current_timestamp, true, current_timestamp, 'admin', current_timestamp, 1);

INSERT INTO users (id, email, firstname, lastname, gender,  birthdate, consent, created_date, created_by, last_modified_date, version)
	VALUES ('d4d734c2-7eae-4b28-943e-278b07a67bd2', 'michael.smith@company.com', 'Michael', 'Smith', 'Male', current_timestamp, true, current_timestamp, 'admin', current_timestamp, 1);

INSERT INTO campaigns(id, name, created_by, created_date, last_modified_date, version)
	VALUES ('9aa2ac4a-8402-455a-aafa-25167179a049', 'Drake life', 'admin', current_timestamp, current_timestamp, 0);

INSERT INTO campaigns(id, name, created_by, created_date, last_modified_date, version)
	VALUES ('1768c798-6684-4e9f-bc13-6e107d1f97e6', 'RHCP concert', 'admin', current_timestamp, current_timestamp, 0);

INSERT INTO campaigns(id, name, created_by, created_date, last_modified_date, version)
	VALUES ('454e30dd-1643-4864-ae6f-752b10920216', 'Imagine', 'admin', current_timestamp, current_timestamp, 0);

INSERT INTO subscriptions(user_id, campaign_id)
	VALUES ('fc2ff7a9-59f4-4a33-918f-f974a7668dc1', '454e30dd-1643-4864-ae6f-752b10920216');

INSERT INTO subscriptions(user_id, campaign_id)
	VALUES ('fc2ff7a9-59f4-4a33-918f-f974a7668dc1', '9aa2ac4a-8402-455a-aafa-25167179a049');
