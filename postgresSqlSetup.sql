-- 1. טבלת משתמשים
/*
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. טבלת אקורדים כלליים
CREATE TABLE chords (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) UNIQUE NOT NULL  -- לדוגמה: Am, C, G
);

-- 3. טבלת קשר בין משתמשים לאקורדים שהם יודעים
CREATE TABLE user_chords (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    chord_id INT REFERENCES chords(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, chord_id)
);

-- 4. טבלת שירים
CREATE TABLE songs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    artist VARCHAR(100),
    tabs TEXT,
    source_url TEXT
);

-- 5. טבלת קשר בין שיר לאקורדים שהוא כולל
CREATE TABLE song_chords (
    song_id INT REFERENCES songs(id) ON DELETE CASCADE,
    chord_id INT REFERENCES chords(id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, chord_id)
);

-- טבלת ז'אנרים
CREATE TABLE genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL  -- לדוגמה: Rock, Pop, Jazz
);

-- קשר בין שירים לז'אנרים
CREATE TABLE song_genres (
    song_id INT REFERENCES songs(id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (song_id, genre_id)
);

-- קשר בין משתמשים לז'אנרים אהובים
CREATE TABLE user_genres (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    genre_id INT REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, genre_id)
);

-- עדכון טבלת משתמשים עם תאריך לידה
ALTER TABLE users
ADD COLUMN date_of_birth DATE;


CREATE TABLE user_songs (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    song_id INT REFERENCES songs(id) ON DELETE CASCADE,
    status VARCHAR(20),  -- לדוגמה: 'favorite', 'learned', 'to_learn'
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, song_id)
);


-- טבלת אמנים
CREATE TABLE artists (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- קשר בין אמן לשיר (n:n)
CREATE TABLE artist_songs (
    artist_id INT REFERENCES artists(id) ON DELETE CASCADE,
    song_id INT REFERENCES songs(id) ON DELETE CASCADE,
    PRIMARY KEY (artist_id, song_id)
);


ALTER TABLE songs
ADD COLUMN artist_id INT REFERENCES artists(id);

ALTER TABLE songs
ADD CONSTRAINT unique_title_per_artist UNIQUE (title, artist_id);


ALTER TABLE songs
DROP COLUMN source_url
DROP COLUMN artist
DROP COLUMN tabs;

ALTER TABLE songs
ADD COLUMN lyrics TEXT;


*/


SELECT 
    s.id AS song_id,
    s.title AS song_title,
    c.name AS chord_name,
    c.id AS chord_id
FROM song_chords sc
JOIN chords c ON sc.chord_id = c.id
JOIN songs s ON sc.song_id = s.id
ORDER BY s.id, c.name;

SELECT 
    s.title AS song_title,
	--s.lyrics AS lyr,
    a.name AS artist_name,
    COUNT(sc.chord_id) AS chord_count
FROM songs s
JOIN artists a ON s.artist_id = a.id
LEFT JOIN song_chords sc ON s.id = sc.song_id
GROUP BY s.id, s.title, a.name
ORDER BY chord_count DESC;



SELECT * 
FROM songs s
JOIN song_chords sc ON s.id = sc.song_id
JOIN chords c ON sc.chord_id = c.id
WHERE s.title ='Joyriders';

SELECT s.title AS song_title
FROM songs s
JOIN artists a ON s.artist_id = a.id
WHERE a.name ='Led Zeppelin';

select lyrics from songs



