--liquibase formatted sql

--changeset renaissance-admin:1

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;
SELECT pg_catalog.setval('hibernate_sequence', 1, false);

CREATE TABLE revinfo
(
    rev      integer NOT NULL,
    revtstmp bigint,
    CONSTRAINT revinfo_pkey PRIMARY KEY (rev)
);
--changeset renaissance-admin:2
CREATE TABLE property
(
    id                      varchar(255) NOT NULL,
    name                    varchar(255) NOT NULL,
    address                 varchar(255),
    zipcode                 varchar(6),
    email                   varchar(255),
    secondary_email         varchar(255),
    phone                   varchar(12),
    office_hours            varchar(255),
    facebook_link           varchar(255),
    twitter_link            varchar(255),
    logo                    varchar(255),
    cover_image             varchar(255),
    property_folder_id      varchar(255),
    photos_folder_id        varchar(255),
    youtube_link            varchar(255),
    description             varchar(255),
    vanity_link             varchar(255),
    html_title              varchar(70),
    meta_description        varchar(160),
    conversion_tracking_id1 varchar(255),
    conversion_tracking_id2 varchar(255),
    analytics_code          text,
    tawk_code               text,
    pixel_code              text,
    active                  boolean      NOT NULL DEFAULT TRUE,
    last_modified_by        varchar(255),
    last_modified_date      timestamp without time zone,
    version                 bigint,
    CONSTRAINT property_pkey PRIMARY KEY (id),
    CONSTRAINT uk_property_name UNIQUE (name)
);

CREATE TABLE property_amenity
(
    id                 bigint       NOT NULL,
    name               varchar(255) NOT NULL,
    type               varchar(255) NOT NULL,
    featured           boolean      NOT NULL DEFAULT FALSE,
    property_id        varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT property_amenity_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_property_property_amenity_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE

);

CREATE TABLE property_key_log
(
    id                 bigint NOT NULL,
    box                varchar(255),
    tag                varchar(255),
    key_description    varchar(255),
    notes              varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    property_id        varchar(255),
    CONSTRAINT property_key_log_pkey PRIMARY KEY (id),
    CONSTRAINT fk_property_key_log_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE

);

--changeset renaissance-admin:3
CREATE TABLE floorplan
(
    id                          varchar(255) NOT NULL,
    name                        varchar(255) NOT NULL,
    bedroom                     integer,
    bathroom                    real,
    style                       varchar(255),
    featured                    boolean               DEFAULT FALSE,
    garages                     integer,
    patio_included              boolean               DEFAULT FALSE,
    green_certified             boolean               DEFAULT FALSE,
    description                 varchar(1024),
    html_title                  varchar(70),
    meta_description            varchar(160),
    conversion_tracking_id1     varchar(255),
    conversion_tracking_id2     varchar(255),
    custom_code                 text,
    vanity_link                 varchar(255),
    video_tour_link             varchar(255),
    three_sixty_video_tour_link varchar(255),
    virtual_tour_link           varchar(255),
    photo                       varchar(255),
    cover_image                 varchar(255),
    floor_plan_folder_id        varchar(255),
    photos_folder_id            varchar(255),
    property_id                 varchar(255),
    active                      boolean      NOT NULL DEFAULT TRUE,
    last_modified_by            varchar(255),
    last_modified_date          timestamp without time zone,
    version                     bigint,
    CONSTRAINT floorplan_pkey PRIMARY KEY (id),
    CONSTRAINT fk_property_floorplan_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE
);

--changeset renaissance-admin:4
CREATE TABLE unit
(
    id                  varchar(255) NOT NULL,
    square_foot         integer,
    allowed_pet         varchar(255),
    pet_policy          varchar(255),
    rent                real,
    discounted_rent     real,
    deposit             real,
    end_unit            boolean               DEFAULT FALSE,
    furnished           boolean               DEFAULT FALSE,
    murphy_bed_provided boolean               DEFAULT FALSE,
    affordable_housing  boolean               DEFAULT FALSE,
    level               varchar(255),
    turnover_rate       varchar(255),
    address             varchar(255),
    zipcode             varchar(6),
    move_in_date        date,
    floorplan_id        varchar(255),
    active              boolean      NOT NULL DEFAULT TRUE,
    last_modified_by    varchar(255),
    last_modified_date  timestamp without time zone,
    version             bigint,
    CONSTRAINT unit_pkey PRIMARY KEY (id),
    CONSTRAINT fk_floorplan_unit_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

--changeset renaissance-admin:5
CREATE TABLE pending_note
(
    unit_id            varchar(255) NOT NULL,
    note               varchar(255),
    due_date           date,
    updated_by         varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT pending_note_pkey PRIMARY KEY (unit_id),
    CONSTRAINT fk_unit_pending_note_unit_id FOREIGN KEY (unit_id) REFERENCES unit (id) ON DELETE CASCADE
);

--changeset renaissance-admin:6
CREATE TABLE renew_note
(
    unit_id            varchar(255) NOT NULL,
    renew_type         varchar(255),
    extension_date     date,
    updated_by         varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT renew_note_pkey PRIMARY KEY (unit_id),
    CONSTRAINT fk_unit_renew_note_unit_id FOREIGN KEY (unit_id) REFERENCES unit (id) ON DELETE CASCADE
);

--changeset renaissance-admin:7
CREATE TABLE unit_key_log
(
    id                      bigint NOT NULL,
    box                     varchar(255),
    tag                     varchar(255),
    mailbox                 varchar(255),
    mail_key                varchar(255),
    garage_remote_available boolean DEFAULT FALSE,
    last_modified_by        varchar(255),
    last_modified_date      timestamp without time zone,
    unit_id                 varchar(255),
    CONSTRAINT unit_key_log_pkey PRIMARY KEY (id),
    CONSTRAINT fk_unit_key_log_unit_id FOREIGN KEY (unit_id) REFERENCES unit (id) ON DELETE CASCADE

);

--changeset renaissance-admin:8
CREATE TABLE lease
(
    id                 bigint NOT NULL,
    tenant             varchar(255),
    start_date         date,
    end_date           date,
    signed_date        date,
    unit_id            varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT lease_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_unit_lease_unit_id FOREIGN KEY (unit_id) REFERENCES unit (id) ON DELETE CASCADE
);

--changeset renaissance-admin:9
CREATE TABLE job_vacancy
(
    id                 varchar(255)                NOT NULL,
    title              varchar(255)                NOT NULL,
    description        text                        NOT NULL,
    valid_through      timestamp without time zone NOT NULL,
    employment_type    varchar(50)                 NOT NULL,
    salary             real                        NOT NULL,
    salary_type        varchar(50)                 NOT NULL,
    start_date         varchar(255)                NOT NULL,
    work_hours         varchar(255)                NOT NULL,
    date_posted        date,
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT job_vacancy_pkey PRIMARY KEY (id)
);

--changeset renaissance-admin:10
CREATE TABLE utility
(
    id                 bigint       NOT NULL,
    name               varchar(255) NOT NULL,
    type               varchar(255) NOT NULL,
    floorplan_id       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT utility_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_floorplan_utility_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

--changeset renaissance-admin:11
CREATE TABLE amenity
(
    id                 bigint       NOT NULL,
    name               varchar(255) NOT NULL,
    type               varchar(255) NOT NULL,
    featured           boolean      NOT NULL DEFAULT FALSE,
    floorplan_id       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT amenity_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_amenity_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE

);

--changeset renaissance-admin:12
CREATE TABLE similar_floorplan
(
    id                   bigint NOT NULL,
    floorplan_id         varchar(255),
    similar_floorplan_id varchar(255),
    last_modified_by     varchar(255),
    last_modified_date   timestamp without time zone,
    CONSTRAINT similar_floorplan_pkey PRIMARY KEY (id),
    CONSTRAINT uk_similar_floorplan UNIQUE (floorplan_id, similar_floorplan_id),
    CONSTRAINT fk_floorplan_similar_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE,
    CONSTRAINT fk_floorplan_similar_unit_id FOREIGN KEY (similar_floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

--changeset renaissance-admin:13
CREATE TABLE floorplan_variation
(
    id                 bigint       NOT NULL,
    variation          varchar(255) NOT NULL,
    floorplan_id       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT floorplan_variation_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_floorplan_variation_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE

);

--changeset renaissance-admin:14
CREATE TABLE testimonial
(
    id                 bigint NOT NULL,
    tenant             varchar(255),
    testimonial        varchar(1024),
    floorplan_id       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT testimonial_pkey PRIMARY KEY (id),
    CONSTRAINT fk_floorplan_testimonial_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

--changeset renaissance-admin:15
CREATE TABLE web_special
(
    id                 bigint NOT NULL,
    start_date         date,
    end_date           date,
    description        varchar(255),
    floorplan_id       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT web_special_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_floorplan_web_special_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE

);

--changeset renaissance-admin:16
CREATE TABLE team_member
(
    id                 bigint       NOT NULL,
    name               varchar(255) NOT NULL,
    job_title          varchar(255) NOT NULL,
    email              varchar(255),
    photo_link         varchar(255),
    blog_link          varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT team_member_id_pkey PRIMARY KEY (id)
);
--changeset renaissance-admin:17
CREATE TABLE team_member_property
(
    id             bigint NOT NULL,
    property_id    varchar(255),
    team_member_id bigint,
    CONSTRAINT team_member_property_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_property_team_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_member_team_member_id FOREIGN KEY (team_member_id) REFERENCES team_member (id) ON DELETE CASCADE
);
--changeset renaissance-admin:18
drop table if exists job_vacancy;
CREATE TABLE job_vacancy
(
    id                 bigint                      NOT NULL,
    title              varchar(255)                NOT NULL,
    description        text                        NOT NULL,
    valid_through      timestamp without time zone NOT NULL,
    employment_type    varchar(50)                 NOT NULL,
    salary             real                        NOT NULL,
    salary_type        varchar(50)                 NOT NULL,
    start_date         varchar(255)                NOT NULL,
    work_hours         varchar(255)                NOT NULL,
    date_posted        date,
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT job_vacancy_pkey PRIMARY KEY (id)
);
--changeset renaissance-admin:19
ALTER TABLE floorplan add column photos_count integer default 0;

--changeset renaissance-admin:20
ALTER TABLE unit
    add column unit_folder_id varchar(255);
ALTER TABLE unit
    add column photos_folder_id varchar(255);
ALTER TABLE unit
    add column photos_count integer default 0;

ALTER TABLE unit
    add column video_tour_link varchar(255);
ALTER TABLE unit
    add column three_sixty_video_tour_link varchar(255);
ALTER TABLE unit
    add column virtual_tour_link varchar(255);
ALTER TABLE unit
    add column photos_link varchar(255);

--changeset renaissance-admin:21
ALTER TABLE utility
    add column average_monthly_bill real;

--changeset renaissance-admin:22
ALTER TABLE unit
    add column garages integer;

--changeset renaissance-admin:23
ALTER TABLE floorplan
drop column garages;

--changeset renaissance-admin:24
CREATE TABLE leasing_office
(
    id                 varchar(255) NOT NULL,
    name               varchar(255) NOT NULL,
    address            varchar(255),
    zipcode            varchar(6),
    phone              varchar(12),
    office_hours       varchar(255),
    direction          varchar(1024),
    office_map         varchar(255),
    office_image       varchar(255),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    version            bigint,
    CONSTRAINT leasing_office_pkey PRIMARY KEY (id)
);

--changeset renaissance-admin:25

ALTER TABLE property
    add column leasing_office_id varchar(255);

ALTER TABLE property
    add constraint fk_property_leasing_office_id FOREIGN KEY (leasing_office_id) REFERENCES leasing_office (id) ON DELETE SET NULL;

--changeset renaissance-admin:26
ALTER TABLE property
drop column phone;

ALTER TABLE property
drop column office_hours;


--changeset renaissance-admin:27
ALTER TABLE property
    add column leasing_office_type varchar(50);

--changeset renaissance-admin:28
ALTER TABLE leasing_office
    add column office_image_description varchar(1024);

--changeset renaissance-admin:29
ALTER TABLE leasing_office
    add column office_map_landscape varchar(255);

--changeset renaissance-admin:30
ALTER TABLE property_amenity
    alter column featured drop not null;

--changeset renaissance-admin:31
ALTER TABLE amenity
    alter column featured drop not null;

ALTER TABLE property
    alter column active drop not null;

ALTER TABLE floorplan
    alter column active drop not null;

ALTER TABLE unit
    alter column active drop not null;

--changeset renaissance-admin:32
ALTER TABLE property
    add column phone varchar(12);

--changeset renaissance-admin:33
ALTER TABLE property
    add column rating real;

--changeset renaissance-admin:34
ALTER TABLE property
    rename column vanity_link to rating_link;

--changeset renaissance-admin:35
ALTER TABLE property
alter column rating_link type varchar(2048);

--changeset renaissance-admin:36
ALTER TABLE job_vacancy
    add column active boolean NOT NULL DEFAULT TRUE;

--changeset renaissance-admin:37
ALTER TABLE unit
    add column cover_image varchar(255);

--changeset renaissance-admin:38
ALTER TABLE property
    add column bus_route_link varchar(255);
ALTER TABLE property
    add column bus_route varchar(255);

--changeset renaissance-admin:39
ALTER TABLE property drop column bus_route_link;
ALTER TABLE property drop column bus_route;

CREATE TABLE property_bus_route
(
    id             bigint       NOT NULL,
    bus_route      varchar(255) NOT NULL,
    bus_route_link varchar(255) NOT NULL,
    property_id    varchar(255),
    CONSTRAINT property_bus_route_id_pkey PRIMARY KEY (id),
    CONSTRAINT fk_property_property_bus_route_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE

);

--changeset renaissance-admin:40
CREATE TABLE sublet
(
    id                 bigint       NOT NULL,
    asset_key         varchar(255) NOT NULL,
    first_name         varchar(255) NOT NULL,
    last_name          varchar(255) NOT NULL,
    email              varchar(255) NOT NULL,
    bedroom            integer      NOT NULL,
    available_bedrooms integer      NOT NULL,
    available_from     date         NOT NULL,
    available_to       date         NOT NULL,
    rent               real         NOT NULL,
    pets_allowed       boolean      NOT NULL,
    utilities_included boolean      NOT NULL,
    address            varchar(255) NOT NULL,
    zipcode            varchar(6)   NOT NULL,
    sublet_folder_id   varchar(255),
    photos_folder_id   varchar(255),
    cover_image        varchar(255),
    title              varchar(255) NOT NULL,
    description        text,
    created_date timestamp without time zone,
    active             boolean      NOT NULL,
    version            bigint,
    CONSTRAINT sublet_pkey PRIMARY KEY (id),
    CONSTRAINT uk_sublet_key UNIQUE (asset_key)
);

--changeset renaissance-admin:41
ALTER TABLE floorplan
    add column featured_on_main boolean NOT NULL DEFAULT FALSE;

--changeset renaissance-admin:42
ALTER TABLE floorplan
    add column highlights text;

--changeset renaissance-admin:43
ALTER TABLE sublet
    add column approved boolean DEFAULT FALSE;

--changeset renaissance-admin:44
ALTER TABLE unit
    add column features varchar(1024);

--changeset renaissance-admin:45
ALTER TABLE floorplan
    add column address varchar(255);

--changeset renaissance-admin:46
ALTER TABLE floorplan
    add column zipcode varchar(6);

--changeset renaissance-admin:47
ALTER TABLE property
    add column marketing_folder_id varchar(255);

ALTER TABLE floorplan
    add column marketing_folder_id varchar(255);

ALTER TABLE unit
    add column marketing_folder_id varchar(255);

--changeset renaissance-admin:48
ALTER TABLE unit
    add column patio_included boolean DEFAULT FALSE;
ALTER TABLE unit
    add column floorplan_link varchar(255);

--changeset renaissance-admin:49
CREATE TABLE mileage
(
    id                 bigint  NOT NULL,
    drive_date         date    NOT NULL,
    employee           varchar(255),
    starting_mileage   integer NOT NULL,
    ending_mileage     integer,
    notes              varchar(2048),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT mileage_pkey PRIMARY KEY (id)
);

--changeset renaissance-admin:50
ALTER TABLE property_amenity ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE property_key_log ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE unit_key_log ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE lease ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE job_vacancy ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE utility ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE amenity ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE similar_floorplan ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE floorplan_variation ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE testimonial ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE web_special ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE team_member ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE team_member_property ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE leasing_office ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE property_bus_route ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE sublet ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);
ALTER TABLE mileage ALTER COLUMN id SET DEFAULT nextval('hibernate_sequence'::regclass);

--changeset renaissance-admin:51
CREATE TABLE analytics
(
    name  varchar(255) NOT NULL,
    count bigint,
    CONSTRAINT analytics_pkey PRIMARY KEY (name)
);

--changeset renaissance-admin:52
ALTER TABLE analytics
    add column type varchar(255);
ALTER TABLE analytics
    add column sub_type varchar(255);

--changeset renaissance-admin:53
ALTER TABLE analytics
    add column created_date date;

--changeset renaissance-admin:54
ALTER TABLE unit DROP CONSTRAINT fk_floorplan_unit_floorplan_id,
ADD CONSTRAINT fk_floorplan_unit_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE utility DROP CONSTRAINT fk_floorplan_utility_floorplan_id,
ADD CONSTRAINT fk_floorplan_utility_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE similar_floorplan DROP CONSTRAINT fk_floorplan_similar_id,
ADD CONSTRAINT fk_floorplan_similar_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE floorplan_variation DROP CONSTRAINT fk_floorplan_variation_floorplan_id,
ADD CONSTRAINT fk_floorplan_variation_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE testimonial DROP CONSTRAINT fk_floorplan_testimonial_floorplan_id,
ADD CONSTRAINT fk_floorplan_testimonial_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE web_special DROP CONSTRAINT fk_floorplan_web_special_floorplan_id,
ADD CONSTRAINT fk_floorplan_web_special_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

--changeset renaissance-admin:55
ALTER TABLE amenity DROP CONSTRAINT fk_amenity_floorplan_id,
ADD CONSTRAINT fk_floorplan_amenity_floorplan_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

--changeset renaissance-admin:56
ALTER TABLE similar_floorplan DROP CONSTRAINT fk_floorplan_similar_unit_id,
ADD CONSTRAINT fk_floorplan_similar_unit_id FOREIGN KEY ("floorplan_id") REFERENCES floorplan (id)
    ON DELETE CASCADE ON UPDATE CASCADE;

--changeset renaissance-admin:57
CREATE TABLE property_check
(
    id             bigint       NOT NULL DEFAULT nextval('hibernate_sequence'::regclass),
    employee       varchar(255),
    start_date     date         NOT NULL,
    start_time     varchar(50)  NOT NULL,
    start_asset_id varchar(255) NOT NULL,
    start_address  varchar(1000),
    stop_date      date,
    stop_time      varchar(50),
    stop_asset_id  varchar(255),
    stop_address   varchar(1000),
    CONSTRAINT property_check_pkey PRIMARY KEY (id)
);

--changeset renaissance-admin:59
ALTER TABLE property_check
    ADD COLUMN notes varchar(2048);

--changeset renaissance-admin:60
ALTER TABLE property
    ADD COLUMN lease_type varchar(50) NOT NULL DEFAULT 'YEARLY';

--changeset renaissance-admin:61
CREATE TABLE short_term_floorplan
(
    id                        bigint       NOT NULL DEFAULT nextval('hibernate_sequence'::regclass),
    price_for2to4days         real,
    price_for5to13days        real,
    price_for14to29days       real,
    price_for1to4months       real,
    price_for4and_more_months real,
    square_foot               integer,
    floorplan_id              varchar(255) NOT NULL,
    CONSTRAINT short_term_unit_pricing_pkey PRIMARY KEY (id),
    CONSTRAINT fk_short_term_floorplan_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

--changeset renaissance-admin:62
ALTER TABLE floorplan
    ADD COLUMN special_rent real,
    ADD COLUMN special_rent_start_date date,
    ADD COLUMN special_rent_end_date date;

--changeset renaissance-admin:63
CREATE TABLE home_page_special
(
    id                 bigint       NOT NULL DEFAULT nextval('hibernate_sequence'::regclass),
    title              varchar(60)  NOT NULL,
    description        varchar(255) NOT NULL,
    image              varchar(255) NOT NULL,
    start_date         date         NOT NULL,
    end_date           date         NOT NULL,
    properties         text,
    information1       varchar(50),
    information2       varchar(50),
    information3       varchar(50),
    last_modified_by   varchar(255),
    last_modified_date timestamp without time zone,
    CONSTRAINT home_page_special_id_pkey PRIMARY KEY (id)
);

--changeset renaissance-admin:64
ALTER TABLE home_page_special
    ADD COLUMN details text;

--changeset renaissance-admin:65
ALTER TABLE unit
    ADD COLUMN availability_extension_months integer;

--changeset renaissance-admin:66
ALTER TABLE similar_floorplan DROP CONSTRAINT fk_floorplan_similar_unit_id,
ADD CONSTRAINT fk_floorplan_similar_floorplan_similar_floorplan_id FOREIGN KEY (similar_floorplan_id) REFERENCES floorplan (id)
    ON
DELETE
CASCADE ON UPDATE CASCADE;

--changeset renaissance-admin:67
ALTER TABLE floorplan
    ADD COLUMN allowed_pet varchar(255),
    ADD COLUMN pet_policy varchar(255);

--changeset renaissance-admin:68
ALTER TABLE property
    ADD COLUMN cover_video varchar(255);

--changeset renaissance-admin:69
ALTER TABLE short_term_floorplan
    ADD COLUMN contact_instructions varchar(1000);

--changeset renaissance-admin:70
ALTER TABLE short_term_floorplan
ALTER
COLUMN price_for2to4days TYPE varchar(500) USING price_for2to4days::varchar,
  ALTER
COLUMN price_for5to13days TYPE varchar(500) USING price_for5to13days::varchar,
  ALTER
COLUMN price_for14to29days TYPE varchar(500) USING price_for14to29days::varchar,
  ALTER
COLUMN price_for1to4months TYPE varchar(500) USING price_for1to4months::varchar,
  ALTER
COLUMN price_for4and_more_months TYPE varchar(500) USING price_for4and_more_months::varchar,
DROP
COLUMN contact_instructions;

-- changeset renaissance-admin:71
CREATE SEQUENCE IF NOT EXISTS property_amenity_id_seq OWNED BY property_amenity.id;
ALTER TABLE property_amenity ALTER COLUMN id SET DEFAULT nextval('property_amenity_id_seq');

CREATE SEQUENCE IF NOT EXISTS property_key_log_id_seq OWNED BY property_key_log.id;
ALTER TABLE property_key_log ALTER COLUMN id SET DEFAULT nextval('property_key_log_id_seq');

CREATE SEQUENCE IF NOT EXISTS unit_key_log_id_seq OWNED BY unit_key_log.id;
ALTER TABLE unit_key_log ALTER COLUMN id SET DEFAULT nextval('unit_key_log_id_seq');

CREATE SEQUENCE IF NOT EXISTS lease_id_seq OWNED BY lease.id;
ALTER TABLE lease ALTER COLUMN id SET DEFAULT nextval('lease_id_seq');

CREATE SEQUENCE IF NOT EXISTS job_vacancy_id_seq OWNED BY job_vacancy.id;
ALTER TABLE job_vacancy ALTER COLUMN id SET DEFAULT nextval('job_vacancy_id_seq');

CREATE SEQUENCE IF NOT EXISTS utility_id_seq OWNED BY utility.id;
ALTER TABLE utility ALTER COLUMN id SET DEFAULT nextval('utility_id_seq');

CREATE SEQUENCE IF NOT EXISTS amenity_id_seq OWNED BY amenity.id;
ALTER TABLE amenity ALTER COLUMN id SET DEFAULT nextval('amenity_id_seq');

CREATE SEQUENCE IF NOT EXISTS similar_floorplan_id_seq OWNED BY similar_floorplan.id;
ALTER TABLE similar_floorplan ALTER COLUMN id SET DEFAULT nextval('similar_floorplan_id_seq');

CREATE SEQUENCE IF NOT EXISTS floorplan_variation_id_seq OWNED BY floorplan_variation.id;
ALTER TABLE floorplan_variation ALTER COLUMN id SET DEFAULT nextval('floorplan_variation_id_seq');

CREATE SEQUENCE IF NOT EXISTS testimonial_id_seq OWNED BY testimonial.id;
ALTER TABLE testimonial ALTER COLUMN id SET DEFAULT nextval('testimonial_id_seq');

CREATE SEQUENCE IF NOT EXISTS web_special_id_seq OWNED BY web_special.id;
ALTER TABLE web_special ALTER COLUMN id SET DEFAULT nextval('web_special_id_seq');

CREATE SEQUENCE IF NOT EXISTS team_member_id_seq OWNED BY team_member.id;
ALTER TABLE team_member ALTER COLUMN id SET DEFAULT nextval('team_member_id_seq');

CREATE SEQUENCE IF NOT EXISTS team_member_property_id_seq OWNED BY team_member_property.id;
ALTER TABLE team_member_property ALTER COLUMN id SET DEFAULT nextval('team_member_property_id_seq');

CREATE SEQUENCE IF NOT EXISTS leasing_office_id_seq OWNED BY leasing_office.id;
ALTER TABLE leasing_office ALTER COLUMN id SET DEFAULT nextval('leasing_office_id_seq');

CREATE SEQUENCE IF NOT EXISTS property_bus_route_id_seq OWNED BY property_bus_route.id;
ALTER TABLE property_bus_route ALTER COLUMN id SET DEFAULT nextval('property_bus_route_id_seq');

CREATE SEQUENCE IF NOT EXISTS sublet_id_seq OWNED BY sublet.id;
ALTER TABLE sublet ALTER COLUMN id SET DEFAULT nextval('sublet_id_seq');

CREATE SEQUENCE IF NOT EXISTS mileage_id_seq OWNED BY mileage.id;
ALTER TABLE mileage ALTER COLUMN id SET DEFAULT nextval('mileage_id_seq');

CREATE SEQUENCE IF NOT EXISTS property_check_id_seq OWNED BY property_check.id;
ALTER TABLE property_check ALTER COLUMN id SET DEFAULT nextval('property_check_id_seq');

CREATE SEQUENCE IF NOT EXISTS short_term_floorplan_id_seq OWNED BY short_term_floorplan.id;
ALTER TABLE short_term_floorplan ALTER COLUMN id SET DEFAULT nextval('short_term_floorplan_id_seq');

CREATE SEQUENCE IF NOT EXISTS home_page_special_id_seq OWNED BY home_page_special.id;
ALTER TABLE home_page_special ALTER COLUMN id SET DEFAULT nextval('home_page_special_id_seq');

DROP SEQUENCE IF EXISTS hibernate_sequence;
DROP TABLE IF EXISTS revinfo;

-- changeset renaissance-admin:72

SELECT setval('property_amenity_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM property_amenity), 0) + 1,
              false);

SELECT setval('property_key_log_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM property_key_log), 0) + 1,
              false);

SELECT setval('unit_key_log_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM unit_key_log), 0) + 1, false);

SELECT setval('lease_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM lease), 0) + 1, false);

SELECT setval('job_vacancy_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM job_vacancy), 0) + 1, false);

SELECT setval('utility_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM utility), 0) + 1, false);

SELECT setval('amenity_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM amenity), 0) + 1, false);

SELECT setval('similar_floorplan_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM similar_floorplan), 0) + 1,
              false);

SELECT setval('floorplan_variation_id_seq'::regclass,
              COALESCE((SELECT MAX(id) ::bigint FROM floorplan_variation), 0) + 1, false);

SELECT setval('testimonial_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM testimonial), 0) + 1, false);

SELECT setval('web_special_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM web_special), 0) + 1, false);

SELECT setval('team_member_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM team_member), 0) + 1, false);

SELECT setval('team_member_property_id_seq'::regclass,
              COALESCE((SELECT MAX(id) ::bigint FROM team_member_property), 0) + 1, false);

SELECT setval('property_bus_route_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM property_bus_route), 0) + 1,
              false);

SELECT setval('sublet_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM sublet), 0) + 1, false);

SELECT setval('mileage_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM mileage), 0) + 1, false);

SELECT setval('property_check_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM property_check), 0) + 1, false);

SELECT setval('short_term_floorplan_id_seq'::regclass,
              COALESCE((SELECT MAX(id) ::bigint FROM short_term_floorplan), 0) + 1, false);

SELECT setval('home_page_special_id_seq'::regclass, COALESCE((SELECT MAX(id) ::bigint FROM home_page_special), 0) + 1,
              false);

-- Fix leasing_office.id back to varchar(255)

-- Step 1: Drop the default (sequence nextval)
ALTER TABLE leasing_office ALTER COLUMN id DROP DEFAULT;

-- Step 2: If a sequence was attached as "owned by", detach it
ALTER SEQUENCE leasing_office_id_seq OWNED BY NONE;

-- Step 3: (Optional) Drop the mistaken sequence if not used elsewhere
DROP SEQUENCE IF EXISTS leasing_office_id_seq;


-- changeset renaissance-admin:73

CREATE TABLE floorplan_faq
(
    id                 BIGSERIAL PRIMARY KEY,
    question           TEXT NOT NULL,
    answer             TEXT NOT NULL,
    sort_order         FLOAT,
    floorplan_id       VARCHAR(255),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_floorplan_floorplan_faq_floorplan_id FOREIGN KEY (floorplan_id) REFERENCES floorplan (id) ON DELETE CASCADE
);

-- changeset renaissance-admin:74

CREATE TABLE property_faq
(
    id                 BIGSERIAL PRIMARY KEY,
    question           TEXT NOT NULL,
    answer             TEXT NOT NULL,
    sort_order         FLOAT,
    property_id        VARCHAR(255),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_property_property_faq_property_id FOREIGN KEY (property_id) REFERENCES property (id) ON DELETE CASCADE
);

-- changeset renaissance-admin:75

CREATE TABLE resident_faq
(
    id                 BIGSERIAL PRIMARY KEY,
    question           TEXT NOT NULL,
    answer             TEXT NOT NULL,
    sort_order         FLOAT,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP WITHOUT TIME ZONE
);

-- changeset renaissance-admin:76

CREATE TABLE maintenance_faq
(
    id                 BIGSERIAL PRIMARY KEY,
    question           TEXT NOT NULL,
    answer             TEXT NOT NULL,
    sort_order         FLOAT,
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP WITHOUT TIME ZONE
);


--changeset renaissance-admin:77
ALTER TABLE unit
    ADD COLUMN discounted_rent_start_date date,
    ADD COLUMN discounted_rent_end_date date,
    ADD COLUMN discounted_rent_description varchar(255);

