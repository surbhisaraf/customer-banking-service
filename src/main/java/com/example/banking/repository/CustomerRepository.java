package com.example.banking.repository;

import com.example.banking.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c FROM customer c LEFT JOIN c.accounts a JOIN c.user u WHERE u.username = :username")
    Optional<Customer> findCustomerAccounts(@Param("username") String username);

}
