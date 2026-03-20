-- Convert any existing READY stories to IN_PROGRESS (since READY is being removed)
UPDATE user_stories SET status = 'IN_PROGRESS' WHERE status = 'READY';
