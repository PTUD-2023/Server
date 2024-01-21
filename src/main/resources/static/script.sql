create database health_insurance;
use health_insurance;

DROP TABLE IF EXISTS `health_insurance`.`refresh_token`;
DROP TABLE IF EXISTS`health_insurance`.`user_accounts`;
DROP TABLE IF EXISTS `health_insurance`.`confirm_code`;
drop table if exists `health_insurance`.`health_information`;
drop table if exists `health_insurance`.`insurance_plan_price`;
drop table if exists `health_insurance`.`insurance_plan`;
drop table if exists `health_insurance`.`insurance_payment`;
drop table if exists `health_insurance`.`registration_form`;
drop table if exists `health_insurance`.`insurance_contract`;
drop table if exists `health_insurance`.`insurance_benefit`;
drop table if exists `health_insurance`.`insurance_information`;
drop table if exists `health_insurance`.`insured_person`;
drop table if exists `health_insurance`.`claim_requests`;

create table health_information
(
    id         bigint auto_increment primary key,
    injured text,           -- Người được bảo hiểm có bị thương tật gì không 
	medical_treatment text,    -- có đang được điều trị y tế
    radiation_treatment VARCHAR(5),  -- điều trị bằng tia xạ hay hóa chất
	neurological_treatment VARCHAR(5),  -- điều trị các bệnh về thần kinh
	cardiovascular_treatment VARCHAR(5),  -- điều trị các bệnh về tim mạch hay cao huyết áp
	metabolic_treatment VARCHAR(5),  -- điều trị bệnh tiểu đường hay các bệnh chuyển hoá
	infectious_disease_treatment VARCHAR(5),  -- điều trị các bệnh truyền nhiễm
	disability VARCHAR(5), -- đang mắc bệnh mãn tính hoặc di chứng do các vụ tai nạn hay bị các khuyết tật
	stroke_or_asthma VARCHAR(5), -- bị mắc hoặc được bác sĩ thông báo cho biết mình bị đột quỵ/hen suyễn
	pregnant VARCHAR(5), -- có đang mang thai không(ony female)
	Complication_history VARCHAR(5), -- bị sẩy thai, sinh khó, mổ lấy thai hay biến chứng thai sản chưa(female)
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null
); 

create table user_accounts
(
    id         bigint auto_increment primary key,
    email      varchar(50)                                                                  not null,
    password   varchar(500)                                                                  not null,
    last_name  varchar(50)                                                                  not null default 'User',
    first_name varchar(50)                                                                  not null default 'New',
    phone      varchar(20)                                                                  null,
    birthday date not null,
    `gender` enum('male','female') null,
    address text,
    CMND char(9),
    `role` enum('ROLE_USER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    avatar     json                                                                         null,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'not_activated',
    unique (email)
);

create table refresh_token
(
    id bigint auto_increment primary key,
    user_account_id bigint,
    token varchar(255) not null unique,
    expiry_date timestamp not null,
    foreign key (user_account_id) references user_accounts(id)
);

create table confirm_code
(
    id bigint auto_increment primary key,
    user_account_id bigint,
    code varchar(6) not null unique,
    expiry_date timestamp not null,
    foreign key (user_account_id) references user_accounts(id)
);

create table insured_person
(
    id         bigint auto_increment primary key,
    name varchar(50) not null ,
    email      varchar(50),
    phone      varchar(20),
    birthday date not null,
    `gender` enum('male','female') not null,
    address text not null,
    CMND char(9) null,
    relationship_with_buyers text not null,
    health_infor_id bigint,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'uninsured',
    foreign key (health_infor_id) references health_information(id)
);

create table insurance_plan
(
    id         bigint auto_increment primary key,
    plan_name text,
    duration varchar(20) default '1 năm',
    accident_insurance int,
    hospitalization int,
    surgery int,
    before_admission int,
    after_discharge int,
    take_care_at_home int,
    hospitalization_allowance int,
    emergency_transport int,
    funeral_allowance int,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'activated'
);

create table insurance_information(
	id         bigint auto_increment primary key,
    plan_name text,
    plan_cost int,
    additional_cost int,
    accident_insurance int,
    hospitalization int,
    surgery int,
    before_admission int,
    after_discharge int,
    take_care_at_home int,
    hospitalization_allowance int,
    emergency_transport int,
    funeral_allowance int,
    outpatient_treatment_max int,
    outpatient_treatment int,
    dental_care int,
    Maternity int,
    Death int,
    status varchar(20) default 'activated'
);


create table registration_form
(
    id         bigint auto_increment primary key,
    apply_date date not null,
    note text,
    user_account_id bigint,
    insured_person_id bigint,
    insurance_information_id bigint,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'pending',
    foreign key (user_account_id) references user_accounts(id),
    foreign key (insured_person_id) references insured_person(id),
    foreign key (insurance_information_id) references insurance_information(id)
);

create table insurance_contract
(
    id         bigint auto_increment primary key,
    start_date date not null,
    end_date date not null,
    registration_form_id bigint not null,
    payment_status varchar(20) default 'not_paid_enough', -- completed or not_paid_enough
    payment_type varchar(20) default 'pay_one_times', -- pay_monthly hoặc pay_one_times
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'activated',
    foreign key (registration_form_id) references registration_form(id)
);

create table insurance_payment
(
    id         bigint auto_increment primary key,
    amount int not null,
    payment_date date not null,
    payment_method varchar(20) not null,
    registration_form_id bigint not null,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'unpaid',
    foreign key (registration_form_id) references registration_form(id)
);

create table insurance_plan_price
(
    id         bigint auto_increment primary key,
	min_age int not null,
    max_age int not null,
    price int not null,
    rate float,
    insurance_plan_id bigint not null,
    effective_date date,
    status varchar(20) default 'activated',
    foreign key (insurance_plan_id) references insurance_plan(id)
);

create table claim_requests
(
    id         bigint auto_increment primary key,
    amount int not null,
    request_date date not null,
    request_content text not null,
    insurance_contract_id bigint not null,
    user_account_id bigint not null,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'unpaid',
    foreign key (insurance_contract_id) references insurance_contract(id),
    foreign key (user_account_id) references user_accounts(id)
);

create table insurance_benefit
(
    id         bigint auto_increment primary key,
    benefit_name text not null,
    min_age int not null,
    max_age int not null,
    price int not null,
    max_payout int not null,
    per_times_payout int not null,
    insurance_plan_id bigint,
    created_at timestamp              default current_timestamp                             null,
    updated_at timestamp              default current_timestamp on update current_timestamp null,
    status varchar(20) default 'activated',
    foreign key (insurance_plan_id) references insurance_plan(id)
);



-- insert dữ liệu vào bảng insurance plan--

INSERT INTO insurance_plan (plan_name, accident_insurance, hospitalization, surgery, before_admission, after_discharge, take_care_at_home, hospitalization_allowance, emergency_transport, funeral_allowance)
VALUES 
    ('Essential Protection', 75000000, 35000000, 35000000, 1500000, 1500000, 1500000, 30000, 3000000, 2000000),
    ('Standard Coverage', 115000000, 50000000, 50000000, 2500000, 2500000, 2500000, 50000, 5000000, 2000000),
    ('Advanced Care', 185000000, 80000000, 80000000, 4000000, 4000000, 4000000, 80000, 8000000, 2000000),
    ('Comprehensive Shield', 230000000, 100000000, 100000000, 50000000, 50000000, 50000000, 100000, 10000000, 2000000),
    ('Premium Assurance', 415000000, 180000000, 180000000, 9000000, 9000000, 9000000, 180000, 18000000, 2000000),
	('Elite Safeguard', 580000000, 250000000, 250000000, 12500000, 12500000, 12500000, 250000, 25000000, 2000000);

-- insert dữ liệu vào bảng insurance plan price--
INSERT INTO insurance_plan_price (min_age, max_age, price, rate, insurance_plan_id, effective_date)
VALUES 
    (1, 3, 2030000, 0, 1, '2022-01-01'),  -- Essential Protection
    (4, 10, 1345000, 0.015, 1, '2022-01-01'),
    (11, 18, 1140000, 0.01, 1, '2022-01-01'),
    (19, 40, 1100000, 0.025, 1, '2022-01-01'),
    (41, 50, 1300000, 0.03, 1, '2022-01-01'),
    (51, 60, 1345000, 0.03, 1, '2022-01-01'),
    
    (1, 3, 3008750, 0, 2, '2022-01-01'),  -- Standard Coverage
    (4, 10, 1805250, 0.015, 2, '2022-01-01'),
    (11, 18, 1564550, 0.01, 2, '2022-01-01'),
    (19, 40, 1504375, 0.025, 2, '2022-01-01'),
    (41, 50, 1684900, 0.03, 2, '2022-01-01'),
    (51, 60, 1805250, 0.03, 2, '2022-01-01'),
    
	(1, 3, 4416250, 0, 3, '2022-01-01'),  -- Advanced Care
    (4, 10, 2649750, 0.015, 3, '2022-01-01'),
    (11, 18, 2296450, 0.01, 3, '2022-01-01'),
    (19, 40, 2208125, 0.025, 3, '2022-01-01'),
    (41, 50, 2473100, 0.03, 3, '2022-01-01'),
    (51, 60, 2649750, 0.03, 3, '2022-01-01'),
    
    (1, 3, 5738200, 0, 4, '2022-01-01'),  -- Comprehensive Shield
    (4, 10, 3310500, 0.015, 4, '2022-01-01'),
    (11, 18, 2869100, 0.01, 4, '2022-01-01'),
    (19, 40, 2979450, 0.025, 4, '2022-01-01'),
    (41, 50, 3089800, 0.03, 4, '2022-01-01'),
    (51, 60, 3310500, 0.03, 4, '2022-01-01'),
    
    (1, 3, 8583750, 0, 5, '2022-01-01'),  -- Premium Assurance
    (4, 10, 5150250, 0.015, 5, '2022-01-01'),
    (11, 18, 4463550, 0.01, 5, '2022-01-01'),
    (19, 40, 4291875, 0.025, 5, '2022-01-01'),
    (41, 50, 4806900, 0.03, 5, '2022-01-01'),
    (51, 60, 5150250, 0.03, 5, '2022-01-01'),
    
    (1, 3, 10680000, 0, 6, '2022-01-01'),  -- Elite Safeguard
    (4, 10, 6408000, 0.015, 6, '2022-01-01'),
    (11, 18, 5553600, 0.01, 6, '2022-01-01'),
    (19, 40, 5340000, 0.025, 6, '2022-01-01'),
    (41, 50, 5980800, 0.03, 6, '2022-01-01'),
    (51, 60, 6408000, 0.03, 6, '2022-01-01');
    
-- insert dữ liệu vào bảng insurance benefit --
INSERT INTO insurance_benefit (benefit_name,min_age,max_age, price, max_payout, per_times_payout, insurance_plan_id)
VALUES 
	('Bảo hiểm Điều trị ngoại trú', 1, 3, 2296000, 4000000, 800000, 1),  -- Essential Protection
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 1062000, 4000000, 800000, 1),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 1000000, 4000000, 800000, 1),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 900000, 4000000, 800000, 1),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 1062000, 4000000, 800000, 1),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 1162000, 4000000, 800000, 1),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 500000, 1000000, 500000, 1),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 100000, 75000000, 75000000, 1),
    
    ('Bảo hiểm Điều trị ngoại trú', 1, 3, 3296700, 6000000, 1200000, 2),  -- Standard Coverage
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 1712000, 6000000, 1200000, 2),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 1623375, 6000000, 1200000, 2),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 1545000, 6000000, 1200000, 2),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 1757250, 6000000, 1200000, 2),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 1810050, 6000000, 1200000, 2),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 750000, 1500000, 750000, 2),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 230000, 115000000, 115000000, 2),
    
    ('Bảo hiểm Điều trị ngoại trú', 1, 3, 4260000, 8000000, 1500000, 3),  -- Advanced Care
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 2215200, 8000000, 1500000, 3),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 2130000, 8000000, 1500000, 3),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 1755600, 8000000, 1500000, 3),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 1959100, 8000000, 1500000, 3),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 2219360, 8000000, 1500000, 3),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 920000, 2500000, 1250000, 3),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 370000, 185000000, 185000000, 3),
    
    ('Bảo hiểm Điều trị ngoại trú', 1, 3, 5080950, 10000000, 2000000, 4),  -- Comprehensive Shield
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 2516280, 10000000, 2000000, 4),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 2322720, 10000000, 2000000, 4),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 2032800, 10000000, 2000000, 4),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 2312700, 10000000, 2000000, 4),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 2671240, 10000000, 2000000, 4),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 1080000, 3000000, 1500000, 4),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 460000, 230000000, 230000000, 4),
    
    
    ('Bảo hiểm Điều trị ngoại trú', 1, 3, 7870600, 16000000, 3000000, 5),  -- Premium Assurance
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 3681600, 16000000, 3000000, 5),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 3256800, 16000000, 3000000, 5),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 2973600, 16000000, 3000000, 5),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 3382600, 16000000, 3000000, 5),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 3551600, 16000000, 3000000, 5),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 1200000, 4500000, 2250000, 5),
    ('Bảo hiểm Thai sản', 18, 40, 2592000, 16000000, 3200000, 5),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 830000, 415000000, 415000000, 5),
    
	('Bảo hiểm Điều trị ngoại trú', 1, 3, 9912000, 20000000, 4000000, 6),  -- Elite Safeguard
    ('Bảo hiểm Điều trị ngoại trú', 4, 10, 4602000, 20000000, 4000000, 6),
    ('Bảo hiểm Điều trị ngoại trú', 11, 18, 4425000, 20000000, 4000000, 6),
    ('Bảo hiểm Điều trị ngoại trú', 19, 40, 4337200, 20000000, 4000000, 6),
    ('Bảo hiểm Điều trị ngoại trú', 41, 50, 4878000, 20000000, 4000000, 6),
    ('Bảo hiểm Điều trị ngoại trú', 51, 60, 5259100, 20000000, 4000000, 6),
    ('Bảo hiểm Chăm sóc răng', 1 , 60, 1500000, 6000000, 3000000, 6),
    ('Bảo hiểm Thai sản', 18, 40, 3060000, 20000000, 3500000, 6),
    ('Bảo hiểm Tử vong do ốm đau, bệnh tật', 1, 60, 1160000, 580000000, 580000000, 6);




