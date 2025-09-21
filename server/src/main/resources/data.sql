ALTER TABLE items
ADD COLUMN item_request_id BIGINT;

ALTER TABLE items
ADD CONSTRAINT fk_items_item_request
FOREIGN KEY (item_request_id) REFERENCES item_requests(item_request_id);