DROP TABLE IF EXISTS ACCOUNT_LOGIN CASCADE;
DROP TABLE IF EXISTS ACCOUNT_OTP CASCADE;
DROP TABLE IF EXISTS YARD_PICTURE CASCADE;
DROP TABLE IF EXISTS VOTES CASCADE;
DROP TABLE IF EXISTS BOOKING_HISTORY CASCADE;
DROP TABLE IF EXISTS BOOKING CASCADE;
DROP TABLE IF EXISTS SLOTS CASCADE;
DROP TABLE IF EXISTS SUB_YARDS CASCADE;
DROP TABLE IF EXISTS TYPE_YARDS CASCADE;
DROP TABLE IF EXISTS YARDS CASCADE;
DROP TABLE IF EXISTS DISTRICTS CASCADE;
DROP TABLE IF EXISTS PROVINCES CASCADE;
DROP TABLE IF EXISTS VOUCHERS CASCADE;
DROP TABLE IF EXISTS ACCOUNTS CASCADE;
DROP TABLE IF EXISTS ROLES CASCADE;
DROP TABLE IF EXISTS YARD_REPORT CASCADE;

CREATE TABLE ROLES
(
    ID        SERIAL PRIMARY KEY,
    ROLE_NAME VARCHAR(50)
);
ALTER SEQUENCE roles_id_seq RESTART 1000;

CREATE TABLE PROVINCES
(
    ID            INTEGER NOT NULL PRIMARY KEY,
    PROVINCE_NAME VARCHAR(50)
);

CREATE TABLE DISTRICTS
(
    ID            SERIAL PRIMARY KEY,
    PROVINCE_ID   INTEGER REFERENCES PROVINCES ON DELETE CASCADE,
    DISTRICT_NAME VARCHAR(50)
);
ALTER SEQUENCE districts_id_seq RESTART WITH 1000;

CREATE TABLE TYPE_YARDS
(
    ID        SERIAL PRIMARY KEY,
    TYPE_NAME VARCHAR(10) DEFAULT '3 VS 3'::CHARACTER VARYING
);
ALTER SEQUENCE type_yards_id_seq RESTART WITH 1000;

CREATE TABLE ACCOUNTS
(
    ID           VARCHAR(50) NOT NULL PRIMARY KEY,
    ROLE_ID      INTEGER REFERENCES ROLES ON DELETE CASCADE,
    EMAIL        VARCHAR(50) UNIQUE,
    PHONE        VARCHAR(10) UNIQUE,
    FULL_NAME    VARCHAR(100),
    PASSWORD     VARCHAR(200),
    AVATAR_URL   VARCHAR(200),
    IS_CONFIRMED BOOLEAN DEFAULT FALSE,
    IS_ACTIVE    BOOLEAN DEFAULT TRUE,
    CREATE_AT    TIMESTAMP
);

CREATE TABLE ACCOUNT_LOGIN
(
    ID           SERIAL PRIMARY KEY,
    ACCOUNT_ID   VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    ACCESS_TOKEN TEXT,
    IS_LOGOUT    BOOLEAN
);
ALTER SEQUENCE account_login_id_seq RESTART WITH 1000;

CREATE TABLE ACCOUNT_OTP
(
    ID         SERIAL PRIMARY KEY,
    ACCOUNT_ID VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    OTP_CODE   VARCHAR(6),
    EXPIRE_AT  TIMESTAMP,
    CREATE_AT  TIMESTAMP,
    USED       BOOLEAN DEFAULT FALSE
);
ALTER SEQUENCE account_otp_id_seq RESTART WITH 1000;

CREATE TABLE YARDS
(
    ID                 VARCHAR(50) NOT NULL PRIMARY KEY,
    NAME               VARCHAR(45) NOT NULL,
    CREATE_AT          TIMESTAMP,
    ADDRESS            TEXT,
    DISTRICT_ID        INTEGER REFERENCES DISTRICTS ON DELETE CASCADE,
    IS_ACTIVE          BOOLEAN   DEFAULT TRUE,
    IS_DELETED         BOOLEAN   DEFAULT FALSE,
    OWNER_ID           VARCHAR(50) REFERENCES ACCOUNTS ON DELETE CASCADE,
    OPEN_AT            TIME,
    CLOSE_AT           TIME,
    SLOT_DURATION      INTEGER,
    SCORE              INTEGER   DEFAULT 0,
    NUMBER_OF_VOTE     INTEGER   DEFAULT 0,
    REFERENCE          SERIAL,
    CREATED_AT         TIMESTAMP DEFAULT NOW(),
    NUMBER_OF_BOOKINGS INTEGER   DEFAULT 0
);
ALTER SEQUENCE yards_reference_seq RESTART WITH 1000;

CREATE TABLE YARD_PICTURE
(
    ID     SERIAL PRIMARY KEY,
    REF_ID VARCHAR(50),
    IMAGE  VARCHAR(200)
);
ALTER SEQUENCE yard_picture_id_seq RESTART WITH 1000;

CREATE TABLE SUB_YARDS
(
    ID               VARCHAR(50) NOT NULL PRIMARY KEY,
    NAME             VARCHAR(100),
    PARENT_YARD      VARCHAR(50) REFERENCES YARDS ON DELETE CASCADE,
    TYPE_YARD        INTEGER REFERENCES TYPE_YARDS ON DELETE CASCADE,
    CREATED_AT       TIMESTAMP,
    IS_ACTIVE        BOOLEAN DEFAULT TRUE,
    REFERENCE        SERIAL,
    IS_PARENT_ACTIVE BOOLEAN DEFAULT TRUE,
    IS_DELETED       BOOLEAN DEFAULT FALSE,
    UPDATED_AT       TIMESTAMP
);
ALTER SEQUENCE sub_yards_reference_seq RESTART WITH 1000;

CREATE TABLE SLOTS
(
    ID               SERIAL PRIMARY KEY,
    IS_ACTIVE        BOOLEAN DEFAULT TRUE,
    REF_YARD         VARCHAR(50) REFERENCES SUB_YARDS ON DELETE CASCADE,
    PRICE            INTEGER,
    START_TIME       TIME,
    END_TIME         TIME,
    IS_PARENT_ACTIVE BOOLEAN DEFAULT TRUE
);
ALTER SEQUENCE slots_id_seq RESTART WITH 1000;

CREATE TABLE BOOKING
(
    ID             VARCHAR(50) NOT NULL PRIMARY KEY,
    SLOT_ID        INTEGER REFERENCES SLOTS,
    ACCOUNT_ID     VARCHAR(100),
    STATUS         VARCHAR(20),
    DATE           TIMESTAMP,
    NOTE           VARCHAR(200),
    PRICE          INTEGER,
    BOOK_AT        TIMESTAMP,
    REFERENCE      SERIAL,
    SUB_YARD_ID    VARCHAR(50),
    BIG_YARD_ID    VARCHAR(50),
    ORIGINAL_PRICE INTEGER,
    VOUCHER_CODE   VARCHAR(20)
);
ALTER SEQUENCE booking_reference_seq RESTART WITH 1000;

CREATE TABLE VOTES
(
    ID         VARCHAR(50)             NOT NULL PRIMARY KEY,
    BOOKING_ID VARCHAR(50) REFERENCES BOOKING ON DELETE CASCADE,
    SCORE      INTEGER,
    COMMENT    VARCHAR(200),
    DATE       TIMESTAMP DEFAULT NOW() NOT NULL,
    IS_DELETED BOOLEAN   DEFAULT FALSE
);


CREATE TABLE BOOKING_HISTORY
(
    ID             VARCHAR(50),
    BOOKING_ID     VARCHAR(50) REFERENCES BOOKING,
    CREATED_BY     VARCHAR(50) REFERENCES ACCOUNTS,
    BOOKING_STATUS VARCHAR(20),
    CREATED_AT     TIMESTAMP DEFAULT NOW(),
    NOTE           VARCHAR(200),
    REFERENCE      SERIAL
);
ALTER SEQUENCE booking_history_reference_seq RESTART WITH 1000;

CREATE TABLE VOUCHERS
(
    ID           VARCHAR(50) NOT NULL PRIMARY KEY,
    TYPE         VARCHAR(20),
    TITLE        VARCHAR(200),
    DESCRIPTION  TEXT,
    IS_ACTIVE    BOOLEAN   DEFAULT TRUE,
    MAX_QUANTITY INTEGER,
    USAGES       INTEGER   DEFAULT 0,
    VOUCHER_CODE VARCHAR(20) UNIQUE,
    DISCOUNT     DOUBLE PRECISION,
    START_DATE   TIMESTAMP,
    END_DATE     TIMESTAMP,
    STATUS       VARCHAR(20),
    CREATED_BY   VARCHAR(50) REFERENCES ACCOUNTS,
    CREATED_AT   TIMESTAMP DEFAULT NOW(),
    REFERENCE    SERIAL
);
ALTER SEQUENCE vouchers_reference_seq RESTART WITH 1000;

CREATE TABLE YARD_REPORT
(
    ID         VARCHAR(50) NOT NULL PRIMARY KEY,
    USER_ID    VARCHAR(50),
    YARD_ID    VARCHAR(50),
    REASON     VARCHAR(300),
    CREATED_AT TIMESTAMP,
    UPDATED_AT TIMESTAMP,
    STATUS     VARCHAR(50),
    REFERENCE  SERIAL
);
ALTER SEQUENCE yard_report_reference_seq RESTART WITH 1000;

INSERT INTO type_yards (id, type_name) VALUES (DEFAULT, '3 VS 3');
INSERT INTO type_yards (id, type_name) VALUES (DEFAULT, '5 VS 5');

INSERT INTO roles (role_name) VALUES ('user');
INSERT INTO roles (role_name) VALUES ('admin');
INSERT INTO roles (role_name) VALUES ('owner');

INSERT INTO provinces (id, province_name) VALUES (58, 'Thành phố Hồ Chí Minh');
INSERT INTO provinces (id, province_name) VALUES (24, 'Hà Nội');
INSERT INTO provinces (id, province_name) VALUES (15, 'Đà Nẵng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Bình Chánh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Cần Giờ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Củ Chi');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Hóc Môn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Huyện Nhà Bè');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 1');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 10');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 11');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 12');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 3');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 4');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 5');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 6');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 7');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận 8');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Bình Tân');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Bình Thạnh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Gò Vấp');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Phú Nhuận');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Tân Bình');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Quận Tân Phú');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 58, 'Thành phố Thủ Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Ba Đình');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hoàn Kiếm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Tây Hồ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Long Biên');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Cầu Giấy');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Đống Đa');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hai Bà Trưng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hoàng Mai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Thanh Xuân');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Sóc Sơn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Đông Anh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Gia Lâm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Nam Từ Liêm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thanh Trì');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Bắc Từ Liêm');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Mê Linh');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Quận Hà Đông');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Thị xã Sơn Tây');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Ba Vì');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Phúc Thọ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Đan Phượng');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Hoài Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Quốc Oai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thạch Thất');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Chương Mỹ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thanh Oai');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Thường Tín');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Phú Xuyên');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Ứng Hòa');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 24, 'Huyện Mỹ Đức');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Liên Chiểu');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Thanh Khê');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Hải Châu');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Sơn Trà');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Ngũ Hành Sơn');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Quận Cẩm Lệ');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Huyện Hòa Vang');
INSERT INTO districts (id, province_id, district_name) VALUES (DEFAULT, 15, 'Huyện Hoàng Sa');

INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('2274575e-8991-44a1-b85f-685baf27a72b', 1002, 'trungnmse150182@fpt.edu.vn', '0335840116', 'Minh Trung', '$2a$10$0Qbp/sO/XZCj7wzKpR2LFOvJddbccBP6L3CqClVQE1pYIfTciTPSu', null, true, true, '2022-07-31 01:01:11.778229');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('172afebb-319f-4260-b6c1-08db8ee18226', 1001, 'toannbse150270@fpt.edu.vn', '0337850114', 'Bảo Toàn', '$2a$10$OU0K6UnSE0y2OHW.KNssCOEuMGGYlPrEMbnujAee87N6COM05B4ty', null, true, true, '2022-07-31 02:01:03.475134');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 1000, 'maianh@gmail.com', '0334840120', 'Mai Anh', '$2a$10$.eUVZjwpLz7LayRTRJpYR.HCv2kCaTgVrYS9B.ZqTAk37Zyd.v3MK', null, true, true, '2022-07-31 01:49:49.269197');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('2a1d060d-58b4-40a7-92ab-82dd56f59373', 1000, 'giangphse150249@fpt.edu.vn', '0336984556', 'Hà Giang', '$2a$10$.eUVZjwpLz7LayRTRJpYR.HCv2kCaTgVrYS9B.ZqTAk37Zyd.v3MK', null, true, true, '2022-07-31 17:10:05.000000');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('8025ca69-50eb-4df7-8a28-a1416439d7e6', 1000, 'tantqse150208@fpt.edu.vn', '0334840119', 'Quan Tân', '$2a$10$.eUVZjwpLz7LayRTRJpYR.HCv2kCaTgVrYS9B.ZqTAk37Zyd.v3MK', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/e6b3430b-0346-4410-9f69-efa78536c452?alt=media', true, true, '2022-07-31 01:49:49.269197');
INSERT INTO accounts (id, role_id, email, phone, full_name, password, avatar_url, is_confirmed, is_active, create_at) VALUES ('38c1ab75-6e7a-4899-8879-41b8a52eb0c9', 1002, 'nguyenminhtrungfacebook@gmail.com', '0337895401', 'Đức Quang', '$2a$10$FWuJNWPBw2C5KQll0nbgV.UN4HF5dm7ZSYlPA6/u7EPwlc0P/2oca', null, true, true, '2022-07-31 17:59:48.713486');

INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('f1292a0c-7874-4cb9-991b-d85597b04dbe', 'Sân A', '2022-08-01 00:28:12.191213', '756 Đường Nguyễn Trãi, Huyện Bình Chánh, Thành phố Hồ Chí Minh', 1000, true, true, '2274575e-8991-44a1-b85f-685baf27a72b', '00:00:00', '23:30:00', 30, 0, 0, '2022-07-31 17:28:12.207091', 0);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'Sân bóng rổ Thanh Xuân', '2022-07-31 18:03:11.337222', '166 Khuất Duy Tiến, Quận Thanh Xuân, Hà Nội', 1030, true, false, '38c1ab75-6e7a-4899-8879-41b8a52eb0c9', '07:00:00', '22:00:00', 60, 0, 0, '2022-07-31 11:03:11.341336', 0);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'Sân Vân Đồn', '2022-07-31 01:14:13.975192', '120-122 Đường Khánh Hội, Quận 4, TP.Hồ Chí Minh', 1010, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '20:00:00', 60, 100, 2, '2022-07-30 18:14:13.976058', 5);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'Sân Quận 8', '2022-07-31 01:39:10.903149', '8 Đường Quang Đông, Quận 8, TP.Hồ Chí Minh', 1014, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 80, 1, '2022-07-30 18:39:10.908071', 4);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'Sân HUTECH', '2022-07-31 09:39:32.610207', '475A Điện Biên Phủ, Phường 25, Quận Bình Thạnh, Thành phố Hồ Chí Minh', 1016, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '07:00:00', '22:00:00', 60, 0, 0, '2022-07-31 02:39:32.612555', 5);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'Sân Quận Thủ Đức', '2022-07-31 01:42:54.668541', '402A Lê Văn Việt, Tăng Nhơn Phú A, Thủ Đức, Thành phố Hồ Chí Minh', 1021, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 90, 1, '2022-07-30 18:42:54.670530', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'Sân Bóng Rổ Quận 11', '2022-07-31 01:41:14.927749', '8 Đường Quang Đông, Quận 8, Thành phố Hồ Chí Minh', 1007, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '07:00:00', '20:00:00', 60, 80, 2, '2022-07-30 18:41:14.929550', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'Sân Quận 7', '2022-07-31 01:17:09.761265', '702 Nguyễn Văn Linh, Tân Hưng, Quận 7, Thành phố Hồ Chí Minh', 1013, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '05:00:00', '22:00:00', 60, 85, 2, '2022-07-30 18:17:09.762290', 2);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'Sân Bóng Rổ Hoa Lư', '2022-07-31 01:09:31.752206', '2 Đinh Tiên Hoàng, Đài Kao, Quận 1, TP.Hồ Chí Minh', 1005, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '22:00:00', 60, 85, 2, '2022-07-30 18:09:31.757080', 4);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'Sân Tinh Võ', '2022-07-31 01:16:13.440919', '756 Đường Nguyễn Trãi, Quận 5, TP.Hồ Chí Minh', 1011, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '06:00:00', '23:00:00', 60, 100, 2,'2022-07-30 18:16:13.442096', 3);
INSERT INTO yards (id, name, create_at, address, district_id, is_active, is_deleted, owner_id, open_at, close_at, slot_duration, score, number_of_vote, created_at, number_of_bookings) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'Sân Phan Đình Phùng', '2022-07-31 01:12:08.853909', '8 Võ Văn Tần, Quận 3, TP.Hồ Chí Minh', 1009, true, false, '2274575e-8991-44a1-b85f-685baf27a72b', '05:00:00', '22:00:00', 60, 65, 2, '2022-07-30 18:12:08.854834', 3);

INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 'Sân 2', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 1001, '2022-07-31 01:09:31.762472', true, true, false, '2022-07-31 01:09:31.762513');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('b2decd20-fd5b-4afe-85dc-c0c649db0acc', 'Sân 1', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 1000, '2022-07-31 01:09:31.763498', true, true, false, '2022-07-31 01:09:31.763555');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('444f7626-940b-4d42-9c42-d4696d4a7557', 'Sân 2', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 1001, '2022-07-31 01:12:08.857146', true, true, false, '2022-07-31 01:12:08.857179');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 'Sân 1', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 1000, '2022-07-31 01:12:08.857657', true, true, false, '2022-07-31 01:12:08.857671');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 'Sân 2', '95681556-4fd5-4504-9fbe-2471fd322702', 1001, '2022-07-31 01:14:13.977904', true, true, false, '2022-07-31 01:14:13.977933');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('e1843835-2c16-4764-8295-5b45215a9913', 'Sân 1', '95681556-4fd5-4504-9fbe-2471fd322702', 1000, '2022-07-31 01:14:13.978334', true, true, false, '2022-07-31 01:14:13.978346');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('12728671-60ff-4658-944e-225eb4a866cd', 'Sân 2', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 1000, '2022-07-31 01:16:13.444366', true, true, false, '2022-07-31 01:16:13.444415');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 'Sân 1', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 1000, '2022-07-31 01:16:13.444963', true, true, false, '2022-07-31 01:16:13.444992');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('d21d1788-87d4-4bf5-9ef3-c6881693e557', 'Sân 3', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.763620', true, true, false, '2022-07-31 01:37:16.763670');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('e1954053-1123-459d-809f-c50a86de626d', 'Sân 2', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.764618', true, true, false, '2022-07-31 01:37:16.764648');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('81281461-1816-4b13-964d-abdfdad52e37', 'Sân 1', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 1000, '2022-07-31 01:37:16.765148', true, true, false, '2022-07-31 01:37:16.765165');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('625b9cb4-f186-45da-95ed-e14b1d5ff600', 'Sân 2', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 1001, '2022-07-31 01:39:10.909692', true, true, false, '2022-07-31 01:39:10.909723');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('98fcb70a-a40e-4659-b568-d8da28256976', 'Sân 1', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 1000, '2022-07-31 01:39:10.910237', true, true, false, '2022-07-31 01:39:10.910252');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 'Sân 2', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 1001, '2022-07-31 01:41:14.930998', true, true, false, '2022-07-31 01:41:14.931033');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('4b417364-437a-4044-9c8f-95c6db5dd84c', 'Sân 1', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 1000, '2022-07-31 01:41:14.931389', true, true, false, '2022-07-31 01:41:14.931404');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('b3a1d704-a1e4-478e-944a-d63d3703fb9a', 'Sân 3', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.672158', true, true, false, '2022-07-31 01:42:54.672236');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 'Sân 2', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.672765', true, true, false, '2022-07-31 01:42:54.672790');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('956d7e9c-594c-4f44-b8fd-fd70169f8366', 'Sân 1', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 1000, '2022-07-31 01:42:54.673294', true, true, false, '2022-07-31 01:42:54.673318');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('f0a88a8b-550f-4de3-b80d-93e734111a84', 'Sân 1', '7db1754b-23f0-41f6-9e18-08bc904c1881', 1000, '2022-07-31 09:39:32.613616', true, true, false, '2022-07-31 09:39:32.613655');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('67cc05a9-8ebc-482c-a2ff-0cd258014716', 'Sân 2', '7db1754b-23f0-41f6-9e18-08bc904c1881', 1001, '2022-07-31 09:40:50.802402', true, true, false, '2022-07-31 09:40:50.802452');
INSERT INTO sub_yards (id, name, parent_yard, type_yard, created_at, is_active, is_parent_active, is_deleted, updated_at) VALUES ('4c9a6473-885a-45d3-b415-7e709b185d9f', 'Sân 1', 'ef782200-1289-4ff7-a91d-9e66edab015f', 1000, '2022-07-31 18:03:11.345739', true, true, false, '2022-07-31 18:03:11.345780');

INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '444f7626-940b-4d42-9c42-d4696d4a7557', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', 45000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1843835-2c16-4764-8295-5b45215a9913', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '12728671-60ff-4658-944e-225eb4a866cd', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', 50000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'd21d1788-87d4-4bf5-9ef3-c6881693e557', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'e1954053-1123-459d-809f-c50a86de626d', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '05:00:00', '06:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '81281461-1816-4b13-964d-abdfdad52e37', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '625b9cb4-f186-45da-95ed-e14b1d5ff600', 60000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '98fcb70a-a40e-4659-b568-d8da28256976', 50000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4b417364-437a-4044-9c8f-95c6db5dd84c', 45000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', 60000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '06:00:00', '07:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '956d7e9c-594c-4f44-b8fd-fd70169f8366', 55000, '22:00:00', '23:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, 'f0a88a8b-550f-4de3-b80d-93e734111a84', 50000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '67cc05a9-8ebc-482c-a2ff-0cd258014716', 60000, '21:00:00', '22:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '07:00:00', '08:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '08:00:00', '09:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '09:00:00', '10:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '10:00:00', '11:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '11:00:00', '12:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '12:00:00', '13:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '13:00:00', '14:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '14:00:00', '15:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '15:00:00', '16:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '16:00:00', '17:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '17:00:00', '18:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '18:00:00', '19:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '19:00:00', '20:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '20:00:00', '21:00:00', true);
INSERT INTO slots (is_active, ref_yard, price, start_time, end_time, is_parent_active) VALUES (true, '4c9a6473-885a-45d3-b415-7e709b185d9f', 50000, '21:00:00', '22:00:00', true);

INSERT INTO vouchers (id, type, title, description, is_active, max_quantity, usages, voucher_code, discount, start_date, end_date, status, created_by, created_at) VALUES ('99a3f66a-a904-4b80-9a0f-0b696c056db0', 'cash', 'Discount 7k', '', true, 10, 3, 'yUlq5pvZkJsZvwG', 7000, '2022-07-31 00:00:00.000000', '2022-08-03 23:59:00.000000', 'active', '2274575e-8991-44a1-b85f-685baf27a72b', '2022-07-31 09:42:49.216441');
INSERT INTO vouchers (id, type, title, description, is_active, max_quantity, usages, voucher_code, discount, start_date, end_date, status, created_by, created_at) VALUES ('1a774735-2d03-4dec-906d-cc1613009879', 'cash', 'Welcome August Discount 10k', '', true, 10, 4, 'E47WfUPcgLC6fjQ', 10000, '2022-07-31 00:00:00.000000', '2022-08-03 23:59:00.000000', 'active', '2274575e-8991-44a1-b85f-685baf27a72b', '2022-07-31 01:46:11.657880');
INSERT INTO vouchers (id, type, title, description, is_active, max_quantity, usages, voucher_code, discount, start_date, end_date, status, created_by, created_at) VALUES ('0e1dc252-a57b-4fc6-adf3-9519942a3103', 'percent', 'Welcome August Discount 5%', '', true, 10, 5, '17sBLLE9yB9soUo', 5, '2022-07-31 00:00:00.000000', '2022-08-06 23:59:00.000000', 'active', '2274575e-8991-44a1-b85f-685baf27a72b', '2022-07-31 01:46:48.435056');
INSERT INTO vouchers (id, type, title, description, is_active, max_quantity, usages, voucher_code, discount, start_date, end_date, status, created_by, created_at) VALUES ('0502336c-5391-4217-81ff-2d18379e7a76', 'percent', 'Discount 6%', '', true, 10, 0, 'Xvp61qNCxOZS4BG', 6, '2022-07-31 00:00:00.000000', '2022-08-10 23:59:00.000000', 'active', '38c1ab75-6e7a-4899-8879-41b8a52eb0c9', '2022-07-31 18:04:15.779430');

INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('129482dd-8582-4be5-8967-c255e95e26a8', 1080, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 35000, '2022-07-31 01:55:15.073788', 'e1843835-2c16-4764-8295-5b45215a9913', '95681556-4fd5-4504-9fbe-2471fd322702', 45000, 'E47WfUPcgLC6fjQ');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('46b2e8e9-03d8-46b8-819d-3aa8a97d2b0f', 1032, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 40000, '2022-07-31 01:55:43.902216', '444f7626-940b-4d42-9c42-d4696d4a7557', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 50000, 'E47WfUPcgLC6fjQ');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('b4f87d85-04f0-47df-8219-6aca6f6d6efc', 1000, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 52250, '2022-07-31 01:56:19.838057', '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 55000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('370d93ae-cf5b-420f-93c3-510fd17cd6e8', 1111, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 47500, '2022-07-31 01:56:43.572601', '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 50000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('734c3dd2-34b8-4d36-a85e-78c8b1a11a94', 1128, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 57000, '2022-07-31 01:57:22.746480', 'd21d1788-87d4-4bf5-9ef3-c6881693e557', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 60000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('e0ebe3ad-8006-4eae-bc47-32f9c10c4930', 1196, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 40000, '2022-07-31 01:58:15.474601', '98fcb70a-a40e-4659-b568-d8da28256976', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 50000, 'E47WfUPcgLC6fjQ');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('3ad407ac-6e6e-4f3e-a17b-02818d08f89c', 1227, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 35000, '2022-07-31 07:26:19.514665', '4b417364-437a-4044-9c8f-95c6db5dd84c', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 45000, 'E47WfUPcgLC6fjQ');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('a94cf59c-6500-4e41-a716-3e30c340a35f', 1241, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 57000, '2022-07-31 07:26:48.589798', 'b3a1d704-a1e4-478e-944a-d63d3703fb9a', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 60000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('c7c9918d-8773-491f-9e79-7e6043c946b3', 1000, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 00:00:00.000000', '', 52250, '2022-07-31 08:46:07.860940', '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 55000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('cc782d75-52e7-43ca-88a9-fdaf8ea7080b', 1001, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'CANCELED', '2022-08-01 00:00:00.000000', 'Booking canceled at: 31/07/2022 08:47:59 - Reason: My team is busy', 52250, '2022-07-31 08:46:07.822678', '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 55000, '17sBLLE9yB9soUo');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('42ecfc87-20e0-4432-ae19-9ddc179a12a0', 1206, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 50000, '2022-07-31 09:50:09.864399', '98fcb70a-a40e-4659-b568-d8da28256976', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('e280aac6-fef3-479f-b3bd-95b6472cb955', 1205, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'CANCELED', '2022-07-31 00:00:00.000000', 'Booking canceled at: 31/07/2022 09:50:45 - Reason: Yard upgrading', 50000, '2022-07-31 09:50:09.896156', '98fcb70a-a40e-4659-b568-d8da28256976', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('c7892c91-c91c-4d60-8408-982dd8a8b44e', 1189, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 60000, '2022-07-31 15:53:23.433975', '625b9cb4-f186-45da-95ed-e14b1d5ff600', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 60000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('dd1ed690-d354-497c-8e8c-971d36df9616', 1224, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 50000, '2022-07-31 17:17:39.732729', '1d3cb78c-8ff9-43dc-ac97-2ad34ca2912c', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('41e43529-169a-4da9-9f44-dcdb68390faf', 1123, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 50000, '2022-07-31 17:18:14.328273', '2c8dde46-fe11-49a6-9628-d9c5f0f72c9d', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('a54f4633-41cb-4625-801d-2b1a588cfacd', 1078, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 50000, '2022-07-31 17:18:35.114999', 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', '95681556-4fd5-4504-9fbe-2471fd322702', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('55253a44-826a-4ff5-a8fb-c165dd40d6e4', 1141, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 60000, '2022-07-31 17:19:14.945896', 'd21d1788-87d4-4bf5-9ef3-c6881693e557', '87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 60000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('cf70a30d-7f88-4f6f-888b-d6204b847de9', 1028, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 55000, '2022-07-31 17:19:37.965569', 'b2decd20-fd5b-4afe-85dc-c0c649db0acc', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 55000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('caff4c5f-d5ad-4713-a2c2-bb2381a07554', 1285, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 48000, '2022-07-31 17:20:53.252712', '956d7e9c-594c-4f44-b8fd-fd70169f8366', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 55000, 'yUlq5pvZkJsZvwG');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('fbaf04b9-f90a-46a9-87ad-46e90d68ec84', 1045, '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 43000, '2022-07-31 17:21:30.733576', '444f7626-940b-4d42-9c42-d4696d4a7557', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 50000, 'yUlq5pvZkJsZvwG');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('5f6349df-22a2-47c5-a95b-157c246d3820', 1237, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 38000, '2022-07-31 17:24:41.399711', '4b417364-437a-4044-9c8f-95c6db5dd84c', '61dbf913-9b02-4e47-bbc0-73bad1b5238b', 45000, 'yUlq5pvZkJsZvwG');
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('46572656-db56-47c6-a10d-336107119b1e', 1106, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 55000, '2022-07-31 17:24:59.933944', '12728671-60ff-4658-944e-225eb4a866cd', '62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 55000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('0bac10ee-7039-4b50-94c1-4584733f3ee0', 1092, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 45000, '2022-07-31 17:25:14.988364', 'e1843835-2c16-4764-8295-5b45215a9913', '95681556-4fd5-4504-9fbe-2471fd322702', 45000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('0b6bf7f3-165b-4d63-af7c-d2ca43e8d012', 1079, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'CANCELED', '2022-07-31 00:00:00.000000', 'Booking canceled at: 31/07/2022 17:26:56 - Reason: My team is busy, and us can''t play this time. Thankyou!', 50000, '2022-07-31 17:26:04.762453', 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', '95681556-4fd5-4504-9fbe-2471fd322702', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('348ea26b-b6e1-493c-bae0-31ca88180d1b', 1191, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 60000, '2022-07-31 17:27:12.756541', '625b9cb4-f186-45da-95ed-e14b1d5ff600', '42bbac1a-112a-4a1a-b071-ba0bffad71cf', 60000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('9325812d-9d01-44ef-94f1-8fb7009b52fd', 1012, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 60000, '2022-07-31 17:27:29.443822', '51c171c2-4d16-4f13-8f69-ce2f3656ed0f', '3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 60000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('5ac50e2a-bec8-4b9d-a8a9-580c5a747662', 1062, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 45000, '2022-07-31 17:28:07.895400', 'cb642bbd-3a4c-42f4-ac00-c30cd397d7eb', '8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 45000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('ac033e8d-f019-458a-9a15-4b529ab3aff8', 1268, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 55000, '2022-07-31 17:28:26.270842', 'c3b2ed36-9415-45ff-a7bc-270e921ff5b9', '3aa0212a-a656-4aa5-9725-9bb6818417ec', 55000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('8b01d771-171e-4025-ac55-7a3b1e163551', 1301, 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 00:00:00.000000', '', 50000, '2022-07-31 17:28:46.546832', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('13871683-dfc6-4fc9-8c4e-61d5f08cfcea', 1070, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 00:00:00.000000', '', 50000, '2022-08-01 01:06:38.458605', 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', '95681556-4fd5-4504-9fbe-2471fd322702', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('806c6150-33fd-4342-a098-b765b372e476', 1072, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 00:00:00.000000', '', 50000, '2022-08-01 01:06:38.482090', 'ea496cbd-975b-4a3e-bd6c-a8394cc9e0af', '95681556-4fd5-4504-9fbe-2471fd322702', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('accfa1bc-a4f3-4107-8bab-cfe66b6f931e', 1296, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 00:00:00.000000', '', 50000, '2022-08-01 01:56:27.327766', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('c12d1863-3d4b-45a4-9f3f-9053dce61bc6', 1294, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 00:00:00.000000', '', 50000, '2022-08-01 01:56:27.340431', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('e1814b4c-5eb8-48e4-9e19-9d05eb19be9a', 1295, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-12 00:00:00.000000', '', 50000, '2022-08-01 02:51:50.270659', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('2d9aafcd-32bd-4052-9562-70a593951c0f', 1297, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-12 00:00:00.000000', '', 50000, '2022-08-01 02:53:06.560653', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);
INSERT INTO booking (id, slot_id, account_id, status, date, note, price, book_at, sub_yard_id, big_yard_id, original_price, voucher_code) VALUES ('879c7a2d-927d-4970-9eb9-648d816b2d14', 1297, '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'FAILED', '2022-08-12 00:00:00.000000', 'Slot of slot id 1297 is booked.', 50000, '2022-08-01 02:53:11.785205', 'f0a88a8b-550f-4de3-b80d-93e734111a84', '7db1754b-23f0-41f6-9e18-08bc904c1881', 50000, null);

INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('042f1587-23d8-4833-a1b2-2039d024a3a1', '129482dd-8582-4be5-8967-c255e95e26a8', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:55:15.078333', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('a57d9cbf-a373-4003-a93e-5b2ef44463b6', '46b2e8e9-03d8-46b8-819d-3aa8a97d2b0f', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:55:43.903645', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('15846859-a0cb-4a95-ae71-15cdec48c876', 'b4f87d85-04f0-47df-8219-6aca6f6d6efc', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:56:19.839306', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('a8ea9fb6-f111-431e-acad-2f8913ec7485', '370d93ae-cf5b-420f-93c3-510fd17cd6e8', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:56:43.573701', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('e53ab406-9205-4d2a-8a9b-0b8fb7290c19', '734c3dd2-34b8-4d36-a85e-78c8b1a11a94', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:57:22.747564', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('3605b271-0cab-4b73-aaee-8f3ad0defc89', 'e0ebe3ad-8006-4eae-bc47-32f9c10c4930', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 01:58:15.475695', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('234df17c-2181-443b-9a89-cf66398d0af2', '3ad407ac-6e6e-4f3e-a17b-02818d08f89c', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 07:26:19.518861', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('f65a7d0d-4357-4fae-977a-1771ee5dce0e', 'a94cf59c-6500-4e41-a716-3e30c340a35f', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 07:26:48.590973', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('9a4d14fa-1b90-403d-9a9c-7e2ac2f301cc', 'cc782d75-52e7-43ca-88a9-fdaf8ea7080b', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 08:46:07.842717', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('e6f88f87-71b7-4ab5-a7da-ef1ff7d31579', 'c7c9918d-8773-491f-9e79-7e6043c946b3', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 08:46:07.861918', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('3adddc7a-d13e-459f-913b-2b4aacca5d8c', 'cc782d75-52e7-43ca-88a9-fdaf8ea7080b', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'CANCELED', '2022-07-31 08:47:59.929137', 'My team is busy');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('ae0adcb1-b180-47b2-83d6-64c4db8d3fd5', '42ecfc87-20e0-4432-ae19-9ddc179a12a0', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 09:50:09.872921', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('371f06d1-75e7-431d-b18d-79395c969846', 'e280aac6-fef3-479f-b3bd-95b6472cb955', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 09:50:09.897340', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('dac0ca08-9fc5-4bd3-8682-2066d3d7e22b', 'e280aac6-fef3-479f-b3bd-95b6472cb955', '2274575e-8991-44a1-b85f-685baf27a72b', 'CANCELED', '2022-07-31 09:50:45.838958', 'Yard upgrading');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('0b5f49e3-6506-439a-8c0a-b4fa8aa2d0ee', 'c7892c91-c91c-4d60-8408-982dd8a8b44e', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-07-31 15:53:23.450093', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('2cf4350e-89c4-4bfe-9285-5a6b8534fd29', 'dd1ed690-d354-497c-8e8c-971d36df9616', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:17:39.749362', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('ddc64c4f-128b-4dcb-b00b-45f92faec771', '41e43529-169a-4da9-9f44-dcdb68390faf', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:18:14.329684', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('546ca321-ed63-457d-a898-1ae75c682964', 'a54f4633-41cb-4625-801d-2b1a588cfacd', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:18:35.116077', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('f6b02375-420f-4c24-a176-dec3681051e7', '55253a44-826a-4ff5-a8fb-c165dd40d6e4', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:19:14.949573', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('c9850652-7a25-484b-a0d3-245c53a9c852', 'cf70a30d-7f88-4f6f-888b-d6204b847de9', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:19:37.966777', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('09e827f8-f129-4835-8d47-08c53d7d437e', 'caff4c5f-d5ad-4713-a2c2-bb2381a07554', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:20:53.254201', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('db9616b3-67fe-40fe-9fdb-6b4c8e9eb565', 'fbaf04b9-f90a-46a9-87ad-46e90d68ec84', '2a1d060d-58b4-40a7-92ab-82dd56f59373', 'SUCCESS', '2022-07-31 17:21:30.734600', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('d668d299-c3a1-4cec-b7a5-94f1c107ce8a', '5f6349df-22a2-47c5-a95b-157c246d3820', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:24:41.400702', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('beecbb78-630f-46fc-a49b-9aa1f41e1b86', '46572656-db56-47c6-a10d-336107119b1e', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:24:59.934879', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('1b903b36-9b4a-4af9-849b-737a53eee67d', '0bac10ee-7039-4b50-94c1-4584733f3ee0', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:25:14.989200', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('8e1e32fb-ba05-475b-a877-2d5c1a525c87', '0b6bf7f3-165b-4d63-af7c-d2ca43e8d012', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:26:04.763414', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('995feb0d-df3d-4d80-a3b8-911d7ddbaafb', '0b6bf7f3-165b-4d63-af7c-d2ca43e8d012', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'CANCELED', '2022-07-31 17:26:56.381044', 'My team is busy, and us can''t play this time. Thankyou!');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('fb257b82-2010-45f5-a043-00672e52376f', '348ea26b-b6e1-493c-bae0-31ca88180d1b', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:27:12.757401', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('680f702e-eb79-4c2c-9f74-b86881ab398f', '9325812d-9d01-44ef-94f1-8fb7009b52fd', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:27:29.444641', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('44661d10-bfee-4042-b9e8-267fcc98619f', '5ac50e2a-bec8-4b9d-a8a9-580c5a747662', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:28:07.896182', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('31652ecb-676a-4aa9-a995-0e9d54d4e503', 'ac033e8d-f019-458a-9a15-4b529ab3aff8', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:28:26.271865', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('ebdbc577-372a-4383-8eb4-517a0c2e473e', '8b01d771-171e-4025-ac55-7a3b1e163551', 'edd30412-c1ea-4fc3-b1b5-ceb96a22c5e5', 'SUCCESS', '2022-07-31 17:28:46.547590', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('3225df83-89b4-47db-a1e3-be4431e0d4f0', '13871683-dfc6-4fc9-8c4e-61d5f08cfcea', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 01:06:38.464795', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('85b359af-cbb2-4f72-80ad-209bbfe45eb5', '806c6150-33fd-4342-a098-b765b372e476', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 01:06:38.483246', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('4a345cce-a094-4fcd-9104-c6cce1ef4011', 'accfa1bc-a4f3-4107-8bab-cfe66b6f931e', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 01:56:27.328977', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('cce8e6ee-da9a-49df-8ff4-aec1b40f8f30', 'c12d1863-3d4b-45a4-9f3f-9053dce61bc6', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 01:56:27.341562', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('680c356a-53d1-48f5-a913-4d942ed9aadd', 'e1814b4c-5eb8-48e4-9e19-9d05eb19be9a', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 02:51:50.271522', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('799e2a11-cddb-4ed9-bf90-4e88333eef24', '2d9aafcd-32bd-4052-9562-70a593951c0f', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'SUCCESS', '2022-08-01 02:53:06.561536', '');
INSERT INTO booking_history (id, booking_id, created_by, booking_status, created_at, note) VALUES ('b8b163de-dbe3-484b-a60b-63c8a49426aa', '879c7a2d-927d-4970-9eb9-648d816b2d14', '8025ca69-50eb-4df7-8a28-a1416439d7e6', 'FAILED', '2022-08-01 02:53:11.786020', 'Slot is busy.');

INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('899aa2be-4baa-4a06-8dcb-0b856d97b447', '370d93ae-cf5b-420f-93c3-510fd17cd6e8', 100, 'Awesome.', '2022-07-31 07:22:43.130963', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('414b19c4-6f9d-4c75-b2ce-d8b56812c753', '129482dd-8582-4be5-8967-c255e95e26a8', 100, 'Very good!', '2022-07-31 07:23:09.824309', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('5624e2c6-d311-4689-a0e7-3df97d3d2d90', '46b2e8e9-03d8-46b8-819d-3aa8a97d2b0f', 70, 'That ok.', '2022-07-31 07:23:33.508921', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('0a166996-ff6f-4aab-8886-7a88672133bb', 'b4f87d85-04f0-47df-8219-6aca6f6d6efc', 70, 'Excellent.', '2022-07-31 07:24:41.057050', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('f1c633b2-a4df-4a1a-a9d8-21abaec9fe50', 'e0ebe3ad-8006-4eae-bc47-32f9c10c4930', 80, 'So good.', '2022-07-31 07:24:57.205612', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('3fe60487-1c6f-4a06-9743-e2bc90c4ccc1', '734c3dd2-34b8-4d36-a85e-78c8b1a11a94', 90, 'Well!', '2022-07-31 07:25:10.676073', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('819cb891-ea33-4fa3-b3f3-45776438b570', '3ad407ac-6e6e-4f3e-a17b-02818d08f89c', 100, 'So good', '2022-07-31 08:48:45.077894', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('cbe3a0d1-09c3-4de5-b912-25ca15d0cb0d', 'caff4c5f-d5ad-4713-a2c2-bb2381a07554', 90, 'I like this yard', '2022-07-31 18:52:38.944401', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('3be1588c-b32d-4453-9f97-6a19cbba7f4c', 'dd1ed690-d354-497c-8e8c-971d36df9616', 60, 'The quality is good, however the light of yard need to be improved', '2022-07-31 18:53:05.026356', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('975480ca-4a48-4d2d-9957-3bc1878bf2bd', '55253a44-826a-4ff5-a8fb-c165dd40d6e4', 80, 'The owner of this yard is so friendly', '2022-07-31 18:53:38.999897', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('8c3929d2-7601-4b4b-b98b-de20b1cad459', 'a54f4633-41cb-4625-801d-2b1a588cfacd', 100, 'The yard is so clean', '2022-07-31 18:53:54.340434', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('656229d4-b908-4b71-8883-9112bc7bf0f0', 'cf70a30d-7f88-4f6f-888b-d6204b847de9', 100, 'Cheap yard! I like it', '2022-07-31 18:54:07.991312', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('ef043413-e0c4-4a78-8cea-512a14caa3bc', '41e43529-169a-4da9-9f44-dcdb68390faf', 100, 'The yard has free water, that''s amazing.', '2022-07-31 18:55:03.553350', false);
INSERT INTO votes (id, booking_id, score, comment, date, is_deleted) VALUES ('ad8384da-e73a-482e-8b50-8cd1903966dd', 'fbaf04b9-f90a-46a9-87ad-46e90d68ec84', 60, 'It''s good, but quite small.', '2022-07-31 18:55:34.053243', false);

INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/84ad02b9-2052-445a-bb5f-cba0dea885a9?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4f258f4d-ea7c-46b5-a79e-14b91903d3ed?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3b39a7fb-dd58-4259-98f7-d7cdc82a30f9', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/53a254ec-c7fa-4ed5-b2f2-99ca32c3a0db?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/b368a5ff-ce21-49eb-bbb8-4cf918ca8ff3?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/9c50f7d4-f1ba-4d60-ace9-a98b8ae8709d?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4c2a3a0f-4f8a-441c-ba25-8e6bf2878ce7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('95681556-4fd5-4504-9fbe-2471fd322702', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/d4a51be7-e477-4bd8-9bd2-c60ca6acc18f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/e425b549-3f8e-4d4a-928a-c1a136c5e358?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/1dfaa749-1317-4824-a668-18f5e8aca605?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('62ab23a5-aae4-4f83-9f4d-8e8c324e1897', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/2acc32fd-e0af-465f-bbc8-551b481e670a?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/7f488ae9-c82b-4bec-a036-1ee814638b48.jpg?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/761eca26-ba5b-4103-8e87-c2327d103892.jpg?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('87e4a35e-5f39-4d0c-b9d5-3a544fb550ca', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a3892ac2-b3c9-481a-a912-8f10a62cd243.jpg?alt=media&token=4059c770-38be-427f-8d69-332d85bcc7e6');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/4402fdc8-4f9d-4841-a0f3-ba37af05cbf7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/3592fb3e-fd32-4342-b021-555880834336?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('42bbac1a-112a-4a1a-b071-ba0bffad71cf', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/9c2ee165-d32c-4ba3-b191-d02e89bcd9a3?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/ba3b477d-22f5-4a59-aa70-e54a9900d4c8?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/73caa268-852f-4940-9972-cafd35879212?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('61dbf913-9b02-4e47-bbc0-73bad1b5238b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/5c811291-c66e-450b-9c3c-f34f5eb2c373?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/bcf7e038-2a75-479d-88e2-67612cb1c93b?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a6a8c75d-01f0-4e4d-8d5d-4f6e4bff8094?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('3aa0212a-a656-4aa5-9725-9bb6818417ec', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/583cdf85-014c-4a2a-9f54-7c9963118f6f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/f91dfc19-7b56-4454-bacf-27c0543e426e?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('8474dea7-64f4-4ab6-9c6a-89e3af549c4b', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/77d5d692-f2c2-4506-893d-dc9f1699153f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a028fde6-aa86-49b7-a912-0418e35435d7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/1a81c14e-3973-4fbb-b2a2-d95e632db38f?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('7db1754b-23f0-41f6-9e18-08bc904c1881', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/6a1c3841-2df3-419a-90fc-2c43eb626895?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/a256a55a-20fa-49c0-a095-d690dbb01ec7?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/3235d742-ad38-4443-b6ef-eb3ef4acceef?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('ef782200-1289-4ff7-a91d-9e66edab015f', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/dab11c4c-485b-4a0a-9fa7-1a9d7cf10902?alt=media');
INSERT INTO yard_picture (ref_id, image) VALUES ('f1292a0c-7874-4cb9-991b-d85597b04dbe', 'https://firebasestorage.googleapis.com/v0/b/fu-swp391.appspot.com/o/20f6e2e0-2c40-4d8c-9eb8-13bfb0c74a65?alt=media');