package com.payment.TransactionServiceApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.TransactionServiceApplication.models.Txn;

public interface TxnRepository extends JpaRepository<Txn, Integer> {

}
