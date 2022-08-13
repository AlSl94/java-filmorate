CREATE TABLE IF NOT EXISTS users (
    user_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email varchar(255) NOT NULL UNIQUE,
    login varchar(255) NOT NULL UNIQUE,
    name varchar(255),
    birthday date
);

CREATE TABLE IF NOT EXISTS friends (
    user_id bigint REFERENCES users(user_id),
    friend_id bigint REFERENCES users(user_id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_id smallint NOT NULL PRIMARY KEY,
    mpa varchar(5)
);

CREATE TABLE IF NOT EXISTS films (
    film_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    description varchar(200),
    mpa_id smallint REFERENCES mpa_rating(mpa_id),
    duration float,
    release_date date
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id smallint NOT NULL PRIMARY KEY,
    genre varchar(50)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id bigint REFERENCES films(film_id),
    genre_id smallint REFERENCES genres(genre_id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id bigint REFERENCES films (film_id),
    user_id bigint REFERENCES users (user_id),
    PRIMARY KEY (user_id, film_id)
);