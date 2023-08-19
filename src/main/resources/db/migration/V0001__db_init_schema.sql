create table bank_transfer (
    bank_transfer_id bigint not null auto_increment primary key,
    account_from_iban varchar(40) not null,
    transfer_value numeric(10,2),
    account_to_iban varchar(40) not null,
    transfer_date datetime not null,
    reference varchar(30) not null
) engine=InnoDB;