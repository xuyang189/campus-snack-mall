USE campus_snack_mall;

ALTER TABLE snack_product
    ADD COLUMN IF NOT EXISTS merchant_id BIGINT NOT NULL DEFAULT 1 AFTER category_id,
    ADD COLUMN IF NOT EXISTS audit_status TINYINT NOT NULL DEFAULT 0 AFTER status;

ALTER TABLE snack_product
    ADD KEY idx_merchant_id (merchant_id),
    ADD KEY idx_audit_status (audit_status);

UPDATE snack_product
SET merchant_id = 1
WHERE merchant_id IS NULL OR merchant_id = 0;

UPDATE snack_product
SET audit_status = CASE WHEN status = 1 THEN 1 ELSE 0 END
WHERE audit_status IS NULL;