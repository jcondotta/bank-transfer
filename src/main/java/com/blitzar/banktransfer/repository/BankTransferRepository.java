package com.blitzar.banktransfer.repository;

import com.blitzar.banktransfer.domain.BankTransfer;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface BankTransferRepository extends JpaRepository<BankTransfer, Long> {
}
