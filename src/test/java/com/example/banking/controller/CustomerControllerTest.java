package com.example.banking.controller;

import com.example.banking.controllers.CustomerController;
import com.example.banking.models.Account;
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
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {


        @Mock
        private CustomerService customerService;

        @Mock
        private Authentication authentication;

        @InjectMocks
        private CustomerController customerController;

        private Account mockAccount;
        private TransactionRequest mockTransaction;

        @BeforeEach
        void setUp() {
            mockAccount = new Account();
            mockAccount.setAccountNo("A123");
            mockAccount.setBalance(new BigDecimal("1000.00"));

            mockTransaction = new TransactionRequest();
            mockTransaction.setToAccountNo("A123");
            mockTransaction.setFromAccountNo("B456");
            mockTransaction.setAmount(new BigDecimal("500.00"));
        }

        @Test
        void testGetCustomerAccountList_Success() {
            when(customerService.getCustomerAccountList()).thenReturn(List.of(mockAccount));

            ResponseEntity<?> response = customerController.getCustomerAccountList();

            assertEquals(OK, response.getStatusCode());
            assertNotNull(response.getBody());
            GenericResponse<?> body = (GenericResponse<?>) response.getBody();
            assertEquals("Customer account fetched successfully", body.getMessage());
            assertFalse(((List<Account>) body.getData()).isEmpty());
        }

        @Test
        void testDeposit_Success() {
            when(customerService.depositAmount(any())).thenReturn(mockAccount);

            ResponseEntity<?> response = customerController.deposit(mockTransaction);

            assertEquals(CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            GenericResponse<?> body = (GenericResponse<?>) response.getBody();
            assertEquals("Amount deposited successfully", body.getMessage());
            assertEquals("A123", ((Account) body.getData()).getAccountNo());
        }

        @Test
        void testWithdraw_Success() {
            when(customerService.withdrawAmount(any())).thenReturn(mockAccount);

            ResponseEntity<?> response = customerController.withdraw(mockTransaction);

            assertEquals(OK, response.getStatusCode());
            assertNotNull(response.getBody());
            GenericResponse<?> body = (GenericResponse<?>) response.getBody();
            assertEquals("Successfully withdraw amount", body.getMessage());
            assertEquals("A123", ((Account) body.getData()).getAccountNo());
        }

        @Test
        void testTransfer_Success() {
            when(customerService.transferAmount(any())).thenReturn(mockTransaction.getAmount());

            ResponseEntity<?> response = customerController.transfer(mockTransaction);

            assertEquals(OK, response.getStatusCode());
            assertNotNull(response.getBody());
            GenericResponse<?> body = (GenericResponse<?>) response.getBody();
            assertEquals("Successfully transfer", body.getMessage());
            assertEquals(new BigDecimal("500.00"), body.getData());
        }

}
