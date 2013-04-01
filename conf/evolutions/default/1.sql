# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table commentaire_entreprise (
  id                        bigint not null,
  entreprise_id             bigint not null,
  contenu                   varchar(255),
  date                      timestamp,
  categorie                 integer,
  auteur_id                 bigint,
  constraint pk_commentaire_entreprise primary key (id))
;

create table entreprise (
  id                        bigint not null,
  nom                       varchar(35),
  description               varchar(255),
  secteur                   varchar(50),
  constraint pk_entreprise primary key (id))
;

create table mail (
  id                        bigint not null,
  proprietaire_id           bigint,
  intitule                  varchar(15),
  email                     varchar(50),
  priorite                  integer,
  constraint pk_mail primary key (id))
;

create table personne (
  DTYPE                     integer(31) not null,
  id                        bigint not null,
  nom                       varchar(35),
  prenom                    varchar(35),
  premiere_connexion        boolean,
  role                      integer,
  fonction                  varchar(35),
  priorite                  integer,
  commentaire               varchar(255),
  ville_id                  bigint,
  entreprise_id             bigint,
  login                     varchar(35),
  passwd                    varchar(50),
  auth_service              integer,
  banni                     boolean,
  constraint ck_personne_role check (role in (0,1,2,3,4)),
  constraint ck_personne_auth_service check (auth_service in (0,1)),
  constraint pk_personne primary key (id))
;

create table stage (
  id                        bigint not null,
  titre                     varchar(255),
  annee                     integer,
  duree                     integer,
  lieu                      varchar(255),
  entreprise                varchar(255),
  contact                   varchar(255),
  lien_fichier              varchar(255),
  description               varchar(255),
  constraint pk_stage primary key (id))
;

create table telephone (
  id                        bigint not null,
  proprietaire_id           bigint,
  intitule                  varchar(15),
  numero                    varchar(15),
  priorite                  integer,
  constraint pk_telephone primary key (id))
;

create table ville (
  id                        bigint not null,
  libelle                   varchar(75),
  code_postal               varchar(8),
  pays                      varchar(30),
  constraint pk_ville primary key (id))
;

create sequence commentaire_entreprise_seq;

create sequence entreprise_seq;

create sequence mail_seq;

create sequence personne_seq;

create sequence stage_seq;

create sequence telephone_seq;

create sequence ville_seq;

alter table commentaire_entreprise add constraint fk_commentaire_entreprise_entr_1 foreign key (entreprise_id) references entreprise (id) on delete restrict on update restrict;
create index ix_commentaire_entreprise_entr_1 on commentaire_entreprise (entreprise_id);
alter table commentaire_entreprise add constraint fk_commentaire_entreprise_aute_2 foreign key (auteur_id) references personne (id) on delete restrict on update restrict;
create index ix_commentaire_entreprise_aute_2 on commentaire_entreprise (auteur_id);
alter table mail add constraint fk_mail_proprietaire_3 foreign key (proprietaire_id) references personne (id) on delete restrict on update restrict;
create index ix_mail_proprietaire_3 on mail (proprietaire_id);
alter table personne add constraint fk_personne_ville_4 foreign key (ville_id) references ville (id) on delete restrict on update restrict;
create index ix_personne_ville_4 on personne (ville_id);
alter table personne add constraint fk_personne_entreprise_5 foreign key (entreprise_id) references entreprise (id) on delete restrict on update restrict;
create index ix_personne_entreprise_5 on personne (entreprise_id);
alter table telephone add constraint fk_telephone_proprietaire_6 foreign key (proprietaire_id) references personne (id) on delete restrict on update restrict;
create index ix_telephone_proprietaire_6 on telephone (proprietaire_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists commentaire_entreprise;

drop table if exists entreprise;

drop table if exists mail;

drop table if exists personne;

drop table if exists stage;

drop table if exists telephone;

drop table if exists ville;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists commentaire_entreprise_seq;

drop sequence if exists entreprise_seq;

drop sequence if exists mail_seq;

drop sequence if exists personne_seq;

drop sequence if exists stage_seq;

drop sequence if exists telephone_seq;

drop sequence if exists ville_seq;

