CREATE TABLE IF NOT EXISTS users (
    user_id  bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email    varchar(255) NOT NULL UNIQUE,
    login    varchar(255) NOT NULL UNIQUE,
    name     varchar(255),
    birthday date
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   bigint REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id bigint REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_id smallint NOT NULL PRIMARY KEY,
    mpa    varchar(5)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id   int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    director_name varchar(255)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name         varchar(255),
    description  varchar(200),
    mpa_id       smallint REFERENCES mpa_rating (mpa_id),
    duration     float,
    release_date date,
    average_mark double
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id     bigint REFERENCES films(film_id) ON DELETE CASCADE,
    director_id int REFERENCES directors(director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id smallint NOT NULL PRIMARY KEY,
    genre    varchar(50)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id  bigint REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id smallint REFERENCES genres(genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id bigint REFERENCES films (film_id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS marks
(
    film_id bigint REFERENCES films (film_id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (user_id) ON DELETE CASCADE,
    mark    smallint CHECK(mark between 1 and 10) NOT NULL,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    content     varchar(3000) NOT NULL,
    positive    boolean,
    user_id     bigint REFERENCES users (user_id) ON DELETE CASCADE,
    film_id     bigint REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_likes
(
    review_id bigint REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id   bigint REFERENCES users (user_id) ON DELETE CASCADE,
    is_like   boolean
);

CREATE TABLE IF NOT EXISTS event_types
(
    type_id   smallint NOT NULL PRIMARY KEY,
    type_name varchar(10)
);

CREATE TABLE IF NOT EXISTS event_operations
(
    operation_id   smallint NOT NULL PRIMARY KEY,
    operation_name varchar(10)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id        bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id         bigint NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    entity_id       bigint NOT NULL,
    type_id         smallint NOT NULL REFERENCES event_types(type_id),
    operation_id    smallint NOT NULL REFERENCES event_operations(operation_id),
    event_timestamp timestamp without time zone DEFAULT now() NOT NULL
);