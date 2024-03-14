drop table if exists users, items, bookings, requests, comments;

CREATE TABLE IF NOT EXISTS users (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(16) NOT NULL,
  email VARCHAR(32) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  description VARCHAR(64) NOT NULL,
  requestor_id INTEGER NOT NULL,
  FOREIGN KEY(requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  name VARCHAR(32) NOT NULL,
  description VARCHAR(64),
  available BOOLEAN NOT NULL,
  owner_id INTEGER NOT NULL,
  request_id INTEGER NOT NULL,
  FOREIGN KEY(owner_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY(request_id) REFERENCES requests(id)  ON DELETE CASCADE

);

CREATE TABLE IF NOT EXISTS bookings (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id INTEGER NOT NULL,
  booker_id INTEGER NOT NULL,
  status VARCHAR(16) NOT NULL,
  FOREIGN KEY(item_id) REFERENCES items(id)  ON DELETE CASCADE,
  FOREIGN KEY(booker_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
  text VARCHAR(256) NOT NULL,
  item_id INTEGER NOT NULL,
  author_id INTEGER NOT NULL ,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE,
  FOREIGN KEY(author_id) REFERENCES users(id) ON DELETE CASCADE
);

