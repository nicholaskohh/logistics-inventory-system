CREATE TABLE IF NOT EXISTS orders (
  id              VARCHAR(36) PRIMARY KEY,
  order_no        VARCHAR(40) NOT NULL UNIQUE,
  customer_id     VARCHAR(40) NOT NULL,
  total_amount    DECIMAL(18,2) NOT NULL,
  payment_method  VARCHAR(20),
  delivery_method VARCHAR(20),
  status          VARCHAR(20) NOT NULL,
  expected_delivery_at DATETIME(6) NULL,
  created_at      DATETIME(6) NOT NULL,
  updated_at      DATETIME(6) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
  id         VARCHAR(36) PRIMARY KEY,
  order_id   VARCHAR(36) NOT NULL,
  product_id VARCHAR(40) NOT NULL,
  quantity   INT NOT NULL,
  unit_price DECIMAL(18,2) NOT NULL,
  CONSTRAINT fk_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shipments (
  id           VARCHAR(36) PRIMARY KEY,
  order_id     VARCHAR(36) NOT NULL,
  carrier      VARCHAR(30),
  tracking_no  VARCHAR(60) NOT NULL UNIQUE,
  status       VARCHAR(20) NOT NULL,
  shipped_at   DATETIME(6) NULL,
  delivered_at DATETIME(6) NULL,
  CONSTRAINT fk_ship_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
-- 1:1：同一订单只允许一个运单
CREATE UNIQUE INDEX IF NOT EXISTS uq_shipments_order ON shipments(order_id);

CREATE TABLE IF NOT EXISTS shipment_events (
  id          VARCHAR(36) PRIMARY KEY,
  shipment_id VARCHAR(36) NOT NULL,
  event_type  VARCHAR(30) NOT NULL,
  location    VARCHAR(120),
  occurred_at DATETIME(6) NULL,
  remark      VARCHAR(255),
  CONSTRAINT fk_event_ship FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);
