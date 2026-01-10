-- Update existing NULL reaction_type values to LIKE
UPDATE likes
SET reaction_type = 'LIKE'
WHERE reaction_type IS NULL;

-- Add comment for documentation
COMMENT ON COLUMN likes.reaction_type IS 'Reaction type: LIKE (default), LOVE, HAHA, WOW, SAD, ANGRY';
