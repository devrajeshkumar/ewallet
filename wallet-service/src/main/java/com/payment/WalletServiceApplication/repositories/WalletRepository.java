package com.payment.WalletServiceApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.payment.WalletServiceApplication.models.Wallet;

import jakarta.transaction.Transactional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance + :amount where w.contact = :contact")
    void updateWallet(String contact, double amount);
}
