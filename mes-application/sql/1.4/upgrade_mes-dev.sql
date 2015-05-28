-- Added updating cost norms for products
-- last touched 28.05.2015 by kama

CREATE TABLE materialflowresources_costnormsgenerator
(
  id bigint NOT NULL,
  costssource character varying(255) DEFAULT '01mes'::character varying,
  productstoupdate character varying(255) DEFAULT '01all'::character varying,
  CONSTRAINT materialflowresources_costnormsgenerator_pkey PRIMARY KEY (id)
);

CREATE TABLE materialflowresources_costnormslocation
(
  id bigint NOT NULL,
  costnormsgenerator_id bigint,
  location_id bigint,
  CONSTRAINT materialflowresources_costnormslocation_pkey PRIMARY KEY (id),
  CONSTRAINT costnormslocation_costnormsgenerator_fkey FOREIGN KEY (costnormsgenerator_id)
      REFERENCES materialflowresources_costnormsgenerator (id) DEFERRABLE,
  CONSTRAINT costnormslocation_location_fkey FOREIGN KEY (location_id)
      REFERENCES materialflow_location (id) DEFERRABLE
);

ALTER TABLE basic_product ADD COLUMN costnormsgenerator_id bigint;
ALTER TABLE basic_product
  ADD CONSTRAINT product_costnormsgenerator_fkey FOREIGN KEY (costnormsgenerator_id)
      REFERENCES materialflowresources_costnormsgenerator (id) DEFERRABLE;

-- end