package com.headbook.domain.entities;

import com.headbook.domain.enums.RelationshipStatus;

import javax.persistence.*;

@Entity
@Table(name = "relationships")
@IdClass(RelationshipId.class)
public class Relationship {

    @Id
    private Integer userOne;

    @Id
    private Integer userTwo;

    private RelationshipStatus status;

    private Integer actionUser;

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

    public RelationshipStatus getStatus() {
        return status;
    }

    public void setStatus(RelationshipStatus status) {
        this.status = status;
    }

    public Integer getActionUser() {
        return actionUser;
    }

    public void setActionUser(Integer actionUser) {
        this.actionUser = actionUser;
    }
}
