package com.ing.hubs.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(name = "unique_user_currency", columnNames = {"currency", "userId"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private Double balance;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "senderAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> senderRequests;
    @OneToMany(mappedBy = "receiverAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Request> receiverRequests;

    public void updateBalance(double amount) {
        this.balance = this.balance + amount;
    }


}
