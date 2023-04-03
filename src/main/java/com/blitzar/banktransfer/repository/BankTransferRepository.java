package com.blitzar.banktransfer.repository;

import com.blitzar.banktransfer.domain.BankTransfer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransferRepository extends CrudRepository<BankTransfer, Long> {
}
