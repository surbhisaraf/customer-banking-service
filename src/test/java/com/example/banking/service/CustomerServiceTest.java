package com.example.banking.service;

import com.example.banking.exception.InvalidTransactionException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.models.Account;
import com.example.banking.models.AccountType;
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
import java.util.ArrayList;
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

        mockUser = new User();
        mockUser.setUsername("testUser");

        mockCustomer = new Customer();
        mockCustomer.setId("C2232324");
        mockCustomer.setUser(mockUser);

        mockAccount = new Account();
        mockAccount.setAccountNo("2222311344");
        mockAccount.setCustomer(mockCustomer);
        mockAccount.setBalance(new BigDecimal("1000.00"));
        mockAccount.setAccountType(AccountType.REGULAR);
        mockAccount.setCurrency("EUR");

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(Optional.of(mockUser));
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testGetCustomerAccountList_Success() throws ResourceNotFoundException {
        List<Account> accounts = new ArrayList<>();
        accounts.add(mockAccount);
        mockCustomer.setAccounts(accounts);
        when(customerRepository.findCustomerAccounts("testUser")).thenReturn(Optional.of(mockCustomer));

        List<Account> customerAccounts = customerService.getCustomerAccountList();
        assertNotNull(customerAccounts);
        assertEquals("2222311344", customerAccounts.get(0).getAccountNo());
    }

    @Test
    void testGetCustomerAccountList_CustomerNotFound() {
        when(customerRepository.findCustomerAccounts("testUser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerAccountList());
    }

    @Test
    void testDepositAmount_Success() {
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setToAccountNo("2222311344");
        depositRequest.setAmount(new BigDecimal("500.00"));

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));

        Account updatedAccount = customerService.depositAmount(depositRequest);
        assertEquals(new BigDecimal("1500.00"), updatedAccount.getBalance());
    }

    @Test
    void testDepositAmount_AccountNotFound() {
        TransactionRequest depositRequest = new TransactionRequest();
        depositRequest.setToAccountNo("64345825"); // Non-existing account

        when(accountRepository.findByAccountNo("64345825")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.depositAmount(depositRequest));
    }

    @Test
    void testWithdrawAmount_Success() {
        TransactionRequest withdrawRequest = new TransactionRequest();
        withdrawRequest.setFromAccountNo("2222311344");
        withdrawRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));

        Account updatedAccount = customerService.withdrawAmount(withdrawRequest);
        assertEquals(new BigDecimal("800.00"), updatedAccount.getBalance());
    }

    @Test
    void testWithdrawAmount_InsufficientFunds() {
        TransactionRequest withdrawRequest = new TransactionRequest();
        withdrawRequest.setFromAccountNo("2222311344");
        withdrawRequest.setAmount(new BigDecimal("2000.00")); // More than balance

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));

        assertThrows(InvalidTransactionException.class, () -> customerService.withdrawAmount(withdrawRequest));
    }

    @Test
    void testTransferAmount_OwnAccounts_Success() {
        //between same customer from regular -> saving
        Account receiverAccount = new Account();
        receiverAccount.setAccountNo("111684447");
        receiverAccount.setCustomer(mockCustomer);
        receiverAccount.setBalance(new BigDecimal("500.00"));
        receiverAccount.setAccountType(AccountType.SAVING);

        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("2222311344");
        transferRequest.setToAccountNo("111684447");
        transferRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findByAccountNo("111684447")).thenReturn(Optional.of(receiverAccount));

        BigDecimal transferredAmount = customerService.transferAmount(transferRequest);

        assertEquals(new BigDecimal("800.00"), mockAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), receiverAccount.getBalance());
        assertEquals(new BigDecimal("200.00"), transferredAmount);
    }

    @Test
    void testTransferAmount_OtherCustomerAccount_Success() {
        //between different customer from regular -> saving
        User user = new User();
        user.setUsername("user");

        Customer customer = new Customer();
        customer.setId("C2232324");
        customer.setUser(user);

        Account receiverAccount = new Account();
        receiverAccount.setAccountNo("333684447");
        receiverAccount.setCustomer(customer);
        receiverAccount.setBalance(new BigDecimal("500.00"));
        receiverAccount.setAccountType(AccountType.SAVING);

        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("2222311344");
        transferRequest.setToAccountNo("333684447");
        transferRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findByAccountNo("333684447")).thenReturn(Optional.of(receiverAccount));

        BigDecimal transferredAmount = customerService.transferAmount(transferRequest);

        assertEquals(new BigDecimal("800.00"), mockAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), receiverAccount.getBalance());
        assertEquals(new BigDecimal("200.00"), transferredAmount);
    }

    @Test
    void testTransferAmount_OwnAccounts_InvalidTransaction() {
        //between same customer from saving -> regular
        Account senderAccount = new Account();
        senderAccount.setAccountNo("111684447");
        senderAccount.setCustomer(mockCustomer);
        senderAccount.setBalance(new BigDecimal("500.00"));
        senderAccount.setAccountType(AccountType.SAVING);

        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("111684447");
        transferRequest.setToAccountNo("2222311344");
        transferRequest.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNo("111684447")).thenReturn(Optional.of(senderAccount));
        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));

        Exception exception = assertThrows(InvalidTransactionException.class, () -> customerService.transferAmount(transferRequest));
        assertEquals("Transfer not allowed: Transfers from a savings account are not permitted", exception.getMessage());
    }


    @Test
    void testTransferAmount_InsufficientBalance() {

        Account receiverAccount = new Account();
        receiverAccount.setAccountNo("111684447");
        receiverAccount.setCustomer(mockCustomer);
        receiverAccount.setBalance(new BigDecimal("500.00"));
        receiverAccount.setAccountType(AccountType.SAVING);

        TransactionRequest transferRequest = new TransactionRequest();
        transferRequest.setFromAccountNo("2222311344");
        transferRequest.setToAccountNo("111684447");
        transferRequest.setAmount(new BigDecimal("2000.00")); // More than balance

        when(accountRepository.findByAccountNo("2222311344")).thenReturn(Optional.of(mockAccount));
        when(accountRepository.findByAccountNo("111684447")).thenReturn(Optional.of(receiverAccount));

        assertThrows(InvalidTransactionException.class, () -> customerService.transferAmount(transferRequest));
    }

}
