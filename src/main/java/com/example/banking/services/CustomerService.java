package com.example.banking.services;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Optional<User> optionalUser = (Optional<User>) principal;
        return optionalUser.get().getUsername();

    }

    @Transactional
    public List<Account> getCustomerAccountList() throws ResourceNotFoundException {
        String username = getLoggedInUser();
        Optional<Customer> customer = customerRepository.findCustomerAccounts(username);
        if (customer.isEmpty()) {
            logger.error("Customer not found with: {}", username);
            throw new ResourceNotFoundException("Customer not found");
        }
        return customer.get().getAccounts();
    }

    @Transactional
    public Account depositAmount(TransactionRequest depositReq) {
        Optional<Account> account = accountRepository.findByAccountNo(depositReq.getToAccountNo());
        logger.error("Account: {}", depositReq.getToAccountNo());
        if (account.isEmpty()) {
            logger.error("Account not found with: {}", depositReq.getToAccountNo());
            throw new ResourceNotFoundException("Account not found");
        }
        if (!account.get().getCustomer().getUser().getUsername().equals(getLoggedInUser())) {
            throw new IllegalArgumentException("Deposit not allowed: Cannot deposit funds into another customer's account");
        }
        if (depositReq.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account updatedAccount = account.get();
        BigDecimal balance = updatedAccount.getBalance();
        updatedAccount.setBalance(balance.add(depositReq.getAmount()));
        accountRepository.save(updatedAccount);

        return updatedAccount;

    }

    @Transactional
    public Account withdrawAmount(TransactionRequest withdrawReq) {
        Optional<Account> account = accountRepository.findByAccountNo(withdrawReq.getFromAccountNo());
        if (account.isEmpty()) {
            logger.error("Account not found with: {}", withdrawReq.getFromAccountNo());
            throw new ResourceNotFoundException("Account not found");
        }
        if (!account.get().getCustomer().getUser().getUsername().equals(getLoggedInUser())) {
            throw new InvalidTransactionException("Withdrawal denied: Unauthorized access to account");
        }
        if (account.get().getAccountType() == AccountType.SAVING) {
            throw new InvalidTransactionException("Withdrawal denied: Not permitted from savings account");
        }
        Account updatedAccount = account.get();
        BigDecimal currentBalance = updatedAccount.getBalance();

        if (currentBalance.compareTo(withdrawReq.getAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient funds");
        }
        updatedAccount.setBalance(currentBalance.subtract(withdrawReq.getAmount()));
        accountRepository.save(updatedAccount);

        return updatedAccount;

    }

    @Transactional
    public BigDecimal transferAmount(TransactionRequest transferReq) {

        Optional<Account> senderAccount = accountRepository.findByAccountNo(transferReq.getFromAccountNo());
        if (senderAccount.isEmpty()) {
            logger.error("Account not found with: {}", transferReq.getFromAccountNo());
            throw new ResourceNotFoundException("Sender's account not found");
        }
        Optional<Account> receiverAccount = accountRepository.findByAccountNo(transferReq.getToAccountNo());

        if (receiverAccount.isEmpty()) {
            logger.error("Account not found with: {}", transferReq.getToAccountNo());
            throw new ResourceNotFoundException("Receiver's account not found");
        }

        Account fromAccount = senderAccount.get();
        Account toAccount = receiverAccount.get();

        if (fromAccount.getAccountType() == AccountType.SAVING) {
            throw new InvalidTransactionException("Transfer not allowed: Transfers from a savings account are not permitted");
        }

        boolean isSameCustomer = fromAccount.getCustomer().getId()
                .equals(toAccount.getCustomer().getId());

        if (isSameCustomer && transferReq.getAmount().compareTo(new BigDecimal("100000")) > 0) {
            throw new InvalidTransactionException("Transfer limit exceeded: Transfers between your own accounts cannot exceed 100,000 EUR");
        }

        if (!isSameCustomer && transferReq.getAmount().compareTo(new BigDecimal("15000")) > 0) {
            throw new InvalidTransactionException("Transfer limit exceeded: Transfers to another customer's account cannot exceed 15,000 EUR");
        }
        if (!fromAccount.getCustomer().getUser().getUsername().equals(getLoggedInUser())) {
            throw new InvalidTransactionException("Unauthorized transfer: Cannot transfer funds from another customer's account");
        }
        if (fromAccount.getBalance().compareTo(transferReq.getAmount()) < 0) {
            throw new InvalidTransactionException("Insufficient balance in account");
        }


        fromAccount.setBalance(fromAccount.getBalance().subtract(transferReq.getAmount()));

        toAccount.setBalance(toAccount.getBalance().add(transferReq.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return transferReq.getAmount();

    }

    public void validate(TransactionRequest transactionRequest, String action) {

        if (action.equalsIgnoreCase("deposit") && isInvalidAccount(transactionRequest.getToAccountNo())) {
            throw new InvalidTransactionException("Account number for deposit is invalid");
        }
        if (action.equalsIgnoreCase("withdraw") && isInvalidAccount(transactionRequest.getFromAccountNo())) {
            throw new InvalidTransactionException("Account number for withdrawal is invalid");
        }
        if (action.equalsIgnoreCase("transfer") && (isInvalidAccount(transactionRequest.getToAccountNo()) || isInvalidAccount(transactionRequest.getFromAccountNo()))) {
            throw new InvalidTransactionException("Aaccount number for transfer is invalid");
        }
    }

    private boolean isInvalidAccount(String accountNo) {
        return accountNo == null || accountNo.isEmpty();
    }
}
