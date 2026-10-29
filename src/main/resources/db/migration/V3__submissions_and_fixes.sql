-- V1 declared these keys SERIAL, which is a 32-bit integer, while every entity maps them
-- to Long and every foreign key referencing them is already BIGINT. source_type and
-- side_quest_source were the only two that got it right.
ALTER TABLE country ALTER COLUMN id TYPE BIGINT;
ALTER TABLE destination ALTER COLUMN id TYPE BIGINT;
ALTER TABLE side_quest ALTER COLUMN id TYPE BIGINT;
ALTER TABLE side_quest_image ALTER COLUMN id TYPE BIGINT;
ALTER TABLE category ALTER COLUMN id TYPE BIGINT;

-- CHAR pads to width and compares ignoring trailing spaces, which is a trap nobody needs
-- on a two-letter code.
ALTER TABLE country ALTER COLUMN country_code TYPE VARCHAR(2);


-- Submissions are open to anyone, so nothing is public until it has been reviewed.
ALTER TABLE side_quest
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    ADD COLUMN submitted_by VARCHAR(200),
    ADD COLUMN rejection_reason TEXT,
    ADD COLUMN moderated_at TIMESTAMPTZ;

-- Everything seeded before moderation existed was already vetted.
UPDATE side_quest SET status = 'APPROVED';

ALTER TABLE side_quest
    ADD CONSTRAINT side_quest_status_check
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));


-- DECIMAL(9,7) leaves only two integer digits, so any |longitude| >= 100 overflows.
ALTER TABLE side_quest
    ALTER COLUMN longitude TYPE DECIMAL(10,7);


-- A site about places all over the world should not store wall-clock timestamps.
ALTER TABLE side_quest
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE 'UTC';


-- An image is either an uploaded object (storage_key) or an external link (image_url).
ALTER TABLE side_quest_image
    ADD COLUMN storage_key TEXT,
    ALTER COLUMN image_url DROP NOT NULL;

ALTER TABLE side_quest_image
    ADD CONSTRAINT side_quest_image_source_check
        CHECK ((image_url IS NULL) <> (storage_key IS NULL)),
    ADD CONSTRAINT side_quest_image_storage_key_key UNIQUE (storage_key);


-- side_quest_category was the only child table without cascade delete.
ALTER TABLE side_quest_category
    DROP CONSTRAINT side_quest_category_side_quest_id_fkey,
    ADD CONSTRAINT side_quest_category_side_quest_id_fkey
        FOREIGN KEY (side_quest_id) REFERENCES side_quest(id) ON DELETE CASCADE;


-- Submitters name their own destinations, so find-or-create needs something to race against.
CREATE UNIQUE INDEX destination_country_id_lower_name_key
    ON destination (country_id, lower(name));


-- The moderation queue is a handful of rows in a table that is overwhelmingly APPROVED,
-- so a partial index serves it far better than one over the whole status column.
CREATE INDEX side_quest_pending_idx ON side_quest (created_at) WHERE status = 'PENDING';
CREATE INDEX side_quest_status_destination_idx ON side_quest (status, destination_id);
CREATE INDEX destination_country_id_idx ON destination (country_id);

-- The composite primary key covers side_quest_id but leaves the reverse lookup unindexed.
CREATE INDEX side_quest_category_category_id_idx ON side_quest_category (category_id);
