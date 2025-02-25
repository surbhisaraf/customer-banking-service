package com.example.banking.controllers;

import com.example.banking.models.Account;
import com.example.banking.payload.request.TransactionRequest;
import com.example.banking.payload.response.GenericResponse;
import com.example.banking.services.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {


    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;


    @GetMapping("/accounts")
    public ResponseEntity<?> getCustomerAccountList() {
        List<Account> accountList = customerService.getCustomerAccountList();
        return ResponseEntity.status(200).body(new GenericResponse<>("Customer account fetched successfully", accountList));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequest depositReq) {
        customerService.validate(depositReq, "deposit");
        logger.error("validation pass");
        Account depositedAmount = customerService.depositAmount(depositReq);
        return ResponseEntity.status(201).body(new GenericResponse<>("Amount deposited successfully", depositedAmount));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequest withdrawReq) {
        customerService.validate(withdrawReq, "withdraw");
        Account withdrawAmount = customerService.withdrawAmount(withdrawReq);
        return ResponseEntity.status(200).body(new GenericResponse<>("Successfully withdraw amount", withdrawAmount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest transferReq) {
        customerService.validate(transferReq, "transfer");
        BigDecimal amountTransfer = customerService.transferAmount(transferReq);
        return ResponseEntity.status(200).body(new GenericResponse<>("Successfully transfer", amountTransfer));
    }

}
