package com.blitzar.banktransfer.repository;

import com.blitzar.banktransfer.domain.BankTransaction;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface BankTransferRepository extends JpaRepository<BankTransaction, Long> {
}
