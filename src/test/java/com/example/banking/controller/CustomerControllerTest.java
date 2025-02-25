package com.example.banking.controller;

import com.example.banking.controllers.CustomerController;
import com.example.banking.models.Account;
import com.example.banking.models.AccountType;
import com.example.banking.models.User;
import com.example.banking.payload.request.TransactionRequest;
import com.example.banking.payload.response.GenericResponse;
import com.example.banking.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerService customerService;


    @InjectMocks
    private CustomerController customerController;

    private Account mockAccount;

    private TransactionRequest mockTransaction;

    @BeforeEach
    void setUp() {
        mockAccount = new Account();
        mockAccount.setAccountNo("111684447");
        mockAccount.setBalance(new BigDecimal("1000.00"));
        mockAccount.setAccountType(AccountType.REGULAR);
        mockAccount.setCurrency("EUR");


        mockTransaction = new TransactionRequest();
        mockTransaction.setToAccountNo("111684447");
        mockTransaction.setFromAccountNo("2222311344");
        mockTransaction.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void testGetCustomerAccountList_Success() {
        when(customerService.getCustomerAccountList()).thenReturn(List.of(mockAccount));

        ResponseEntity<?> response = customerController.getCustomerAccountList();

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GenericResponse<?> body = (GenericResponse<?>) response.getBody();
        assertEquals("Successfully fetched customer accounts", body.getMessage());
    }

    @Test
    void testDeposit_Success() {
        when(customerService.depositAmount(any())).thenReturn(mockAccount);

        ResponseEntity<?> response = customerController.deposit(mockTransaction);

        assertEquals(CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        GenericResponse<?> body = (GenericResponse<?>) response.getBody();
        assertEquals("Deposit successful", body.getMessage());
        assertEquals("111684447", ((Account) body.getData()).getAccountNo());
    }

    @Test
    void testWithdraw_Success() {
        when(customerService.withdrawAmount(any())).thenReturn(mockAccount);

        ResponseEntity<?> response = customerController.withdraw(mockTransaction);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GenericResponse<?> body = (GenericResponse<?>) response.getBody();
        assertEquals("Withdrawal successful", body.getMessage());
        assertEquals("111684447", ((Account) body.getData()).getAccountNo());
    }

    @Test
    void testTransfer_Success() {
        when(customerService.transferAmount(any())).thenReturn(mockTransaction.getAmount());

        ResponseEntity<?> response = customerController.transfer(mockTransaction);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        GenericResponse<?> body = (GenericResponse<?>) response.getBody();
        assertEquals("Transfer successful", body.getMessage());
        assertEquals(new BigDecimal("500.00"), body.getData());
    }

}
