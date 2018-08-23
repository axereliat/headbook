package com.headbook.repository;

import com.headbook.domain.entities.Relationship;
import com.headbook.domain.entities.RelationshipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, RelationshipId> {
    @Query("SELECT r FROM Relationship r WHERE (r.userOne = :userIdOne AND r.userTwo = :userIdTwo) OR (r.userOne = :userIdTwo AND r.userTwo = :userIdOne)")
    Relationship findByUserOneAndUserTwo(@Param("userIdOne") Integer userIdOne, @Param("userIdTwo") Integer userIdTwo);

    //@Query("SELECT r FROM Relationship r WHERE r.userOne = :id OR r.userTwo = :id")
    //Relationship findByUserOneOrUserTwo(@Param("id") Integer id);
}
