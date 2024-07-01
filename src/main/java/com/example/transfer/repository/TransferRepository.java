package com.example.transfer.repository;

import com.example.transfer.model.dto.TransferDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<TransferDTO, Long> {
}
