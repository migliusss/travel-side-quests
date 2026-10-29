INSERT INTO country (name, country_code)
VALUES ('Iceland', 'IS');


INSERT INTO destination (country_id, name, type)
VALUES (
        (SELECT id FROM country WHERE country_code = 'IS'),
        'Westfjords',
        'REGION'
);


INSERT INTO side_quest (
    destination_id,
    name,
    description,
    latitude,
    longitude
)
VALUES
    (
     (SELECT id FROM destination WHERE name = 'Westfjords'),
     'Patreksfjörður',
     'Huge fjords, silent mountains and a strong sense of tradition amongst the locals.',
     65.5941799,
        -24.00924
    ),
    (
     (SELECT id FROM destination WHERE name = 'Westfjords'),
     'Látrabjarg',
     'Iceland''s westernmost point, and the golden sands of Rauðisandur.',
     65.5036938,
     -24.5317509
    );


INSERT INTO source_type (name)
VALUES
    ('INSTAGRAM'),
    ('TIKTOK'),
    ('WEBSITE'),
    ('BLOG'),
    ('BOOK'),
    ('YOUTUBE'),
    ('PODCAST'),
    ('FRIEND'),
    ('OTHER')
ON CONFLICT (name) DO NOTHING;


INSERT INTO side_quest_source (
    side_quest_id,
    name,
    source_type_id,
    page_number
)
SELECT
    sq.id,
    'Intrepid Iceland',
    st.id,
    '24'
FROM side_quest sq
         JOIN destination d ON d.id = sq.destination_id
         JOIN country c ON c.id = d.country_id
         JOIN source_type st ON st.name = 'BOOK'
WHERE c.country_code = 'IS'
  AND d.name = 'Westfjords'
  AND sq.name IN ('Patreksfjörður', 'Látrabjarg');


INSERT INTO category (name)
VALUES
    ('TOWN'),
    ('NATURE'),
    ('VIEWPOINT'),
    ('MUSEUM'),
    ('RESTAURANT'),
    ('CAFE'),
    ('HIKING'),
    ('BEACH'),
    ('HISTORY');


INSERT INTO side_quest_category (
    side_quest_id,
    category_id
)
SELECT
    sq.id,
    c.id
FROM side_quest sq
         CROSS JOIN category c
WHERE
    (sq.name = 'Patreksfjörður' AND c.name = 'TOWN')
   OR
    (sq.name = 'Látrabjarg' AND c.name IN ('NATURE', 'VIEWPOINT'));