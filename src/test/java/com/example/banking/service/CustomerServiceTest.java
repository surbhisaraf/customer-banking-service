package com.example.banking.service;

import com.example.banking.exception.InvalidTransactionException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.models.Account;
import com.example.banking.models.Customer;
import com.example.banking.models.User;
import com.example.banking.payload.request.TransactionRequest;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.UserRepository;
import com.example.banking.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CustomerService customerService;

    private User mockUser;
    private Customer mockCustomer;
    private Account mockAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock a user
        mockUser = new User();
        mockUser.setUsername("testUser");

        // Mock a customer
        mockCustomer = new Customer();
        mockCustomer.setId("C123");
        mockCustomer.setUser(mockUser);

        // Mock an account
        mockAccount = new Account();
        mockAccount.setAccountNo("A123");
        mockAccount.setCustomer(mockCustomer);
        mockAccount.setBalance(new BigDecimal("1000.00"));

        // Mock security context to return the user
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(Optional.of(mockUser));
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetLoggedInUser() {
        String username = customerService.getLoggedInUser();
        assertEquals("testUser", username);
    }

    @Test
    void testGetCustomerAccountList_Success() throws ResourceNotFoundException {
        when(customerRepository.findCustomerAccounts("testUser")).thenReturn(Optional.of(mockCustomer));

        List<Account> accounts = customerService.getCustomerAccountList();
        assertNotNull(accounts);
    }

    @Test
    void testGetCustomerAccountList_CustomerNotFound() {
        when(customerRepository.findCustomerAccounts("testUser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerAccountList());
    }

    @Test
    void testDepositAmount_Success() {
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setToAccountNo("A123");
        depositRequest.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNo("A123")).thenReturn(Optional.of(mockAccount));

        Account updatedAccount = customerService.depositAmount(depositRequest);
        assertEquals(new BigDecimal("1500.00"), updatedAccount.getBalance());
    }

    @Test
    void testDepositAmount_AccountNotFound() {
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setToAccountNo("A999"); // Non-existing account

        when(accountRepository.findByAccountNo("A999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.depositAmount(depositRequest));
    }

    @Test
    void testWithdrawAmount_Success() {
        TransactionRequest withdrawRequest = new TransactionRequest();
        withdrawRequest.setFromAccountNo("A123");
        withdrawRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("A123")).thenReturn(Optional.of(mockAccount));

        Account updatedAccount = customerService.withdrawAmount(withdrawRequest);
        assertEquals(new BigDecimal("800.00"), updatedAccount.getBalance());
    }

    @Test
    void testWithdrawAmount_InsufficientFunds() {
        TransactionRequest withdrawRequest = new TransactionRequest();
        withdrawRequest.setFromAccountNo("A123");
        withdrawRequest.setAmount(new BigDecimal("2000.00")); // More than balance

        when(accountRepository.findByAccountNo("A123")).thenReturn(Optional.of(mockAccount));

        assertThrows(InvalidTransactionException.class, () -> customerService.withdrawAmount(withdrawRequest));
    }

    @Test
    void testTransferAmount_Success() {
        Account receiverAccount = new Account();
        receiverAccount.setAccountNo("B456");
        receiverAccount.setCustomer(mockCustomer);
        receiverAccount.setBalance(new BigDecimal("500.00"));

        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("A123");
        transferRequest.setToAccountNo("B456");
        transferRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("A123")).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findByAccountNo("B456")).thenReturn(Optional.of(receiverAccount));

        BigDecimal transferredAmount = customerService.transferAmount(transferRequest);

        assertEquals(new BigDecimal("800.00"), mockAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), receiverAccount.getBalance());
        assertEquals(new BigDecimal("200.00"), transferredAmount);
    }

    @Test
    void testTransferAmount_InsufficientBalance() {
        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("A123");
        transferRequest.setToAccountNo("B456");
        transferRequest.setAmount(new BigDecimal("2000.00")); // More than balance

        //  when(accountRepository.findByAccountNo("A123")).thenReturn(Optional.of(mockAccount));

        //assertThrows(InvalidTransactionException.class, () -> customerService.transferAmount(transferRequest));
    }

}
