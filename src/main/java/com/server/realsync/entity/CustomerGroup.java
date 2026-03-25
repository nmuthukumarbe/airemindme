/**
 * 
 */
package com.server.realsync.entity;

import jakarta.persistence.*;

/**
 * 
 */
@Entity
@Table(name = "customer_group", uniqueConstraints = {
        @UniqueConstraint(name = "uk_customer_group_account_name", columnNames = { "account_id", "name" })
})
public class CustomerGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    public CustomerGroup() {
    }

    public CustomerGroup(String name, Integer accountId) {
        this.name = name;
        this.accountId = accountId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}