package com.headbook.domain.entities;

import java.io.Serializable;

public class RelationshipId implements Serializable {

    private Integer userOne;

    private Integer userTwo;

    public Integer getUserOne() {
        return userOne;
    }

    public void setUserOne(Integer userOne) {
        this.userOne = userOne;
    }

    public Integer getUserTwo() {
        return userTwo;
    }

    public void setUserTwo(Integer userTwo) {
        this.userTwo = userTwo;
    }
}
