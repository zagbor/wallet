package com.zagbor.wallet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "username")
public class User {

    private String username;
    @JsonManagedReference
    private Wallet wallet; // В этой связи кошелек пользователя

    @JsonCreator
    public User(@JsonProperty("username") String username, @JsonProperty("wallet") Wallet wallet) {
        this.username = username;
        this.wallet = wallet;
    }
}