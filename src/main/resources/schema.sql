CREATE TABLE IF NOT EXISTS users (
    user_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email varchar(255) NOT NULL UNIQUE,
    login varchar(255) NOT NULL UNIQUE,
    name varchar(255) NOT NULL,
    birthday date
);

CREATE TABLE IF NOT EXISTS friends (
    user_id_1 bigint REFERENCES users(user_id),
    user_id_2 bigint REFERENCES users(user_id),
    is_friend boolean
);

CREATE TABLE IF NOT EXISTS films (
    film_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    description varchar(200),
    rating varchar(5),
    duration float,
    release_date date
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id bigint REFERENCES films (film_id),
    genre varchar
);

CREATE TABLE IF NOT EXISTS likes (
    film_id bigint REFERENCES users (user_id),
    user_id bigint REFERENCES films (film_id),
    PRIMARY KEY (user_id, film_id)
);
