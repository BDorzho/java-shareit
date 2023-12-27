CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(256) UNIQUE NOT NULL,
  email VARCHAR(512) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS item_requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  description TEXT,
  requester_id BIGINT REFERENCES users(id),
  created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(256) NOT NULL,
  description TEXT,
  is_available BOOLEAN,
  owner_id BIGINT REFERENCES users(id),
  request_id BIGINT REFERENCES item_requests(id)
 );

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  start_date TIMESTAMP WITHOUT TIME ZONE,
  end_date TIMESTAMP WITHOUT TIME ZONE,
  item_id BIGINT REFERENCES items(id),
  booker_id BIGINT REFERENCES users(id),
  status VARCHAR(50)
 );

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  item_id BIGINT REFERENCES items(id),
  author_id BIGINT REFERENCES users(id),
  text TEXT,
  created TIMESTAMP WITHOUT TIME ZONE
);
