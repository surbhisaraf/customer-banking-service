package com.example.banking.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "customer")
public class Customer {

    @Id
    private String id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name")
    private String name;

    @NotBlank
    @Size(max = 120)
    @Column(name = "address")
    private String address;

     @Column(name = "phone_no")
    private String phoneNo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Account> accounts = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotBlank @Size(max = 120) String getName() {
        return name;
    }

    public void setName(@NotBlank @Size(max = 120) String name) {
        this.name = name;
    }

    public @NotBlank @Size(max = 120) String getAddress() {
        return address;
    }

    public void setAddress(@NotBlank @Size(max = 120) String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }


}
