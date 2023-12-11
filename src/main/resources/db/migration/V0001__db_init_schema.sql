create table bank_transaction (
    bank_transaction_id bigint not null auto_increment primary key,
    account_from_iban varchar(40) not null,
    transaction_amount numeric(10,2),
    account_to_iban varchar(40) not null,
    transaction_date datetime not null,
    reference varchar(30) not null
) engine=InnoDB;