package com.headbook.domain.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    private Integer id;

    private String description;

    private Set<Photo> photos;

    private User author;

    private LocalDateTime addedOn;

    private Set<User> usersLiked;

    private Set<Comment> comments;

    public Post() {
        this.addedOn = LocalDateTime.now();
        this.photos = new HashSet<>();
        this.usersLiked = new HashSet<>();
        this.comments = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "added_on")
    public LocalDateTime getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(LocalDateTime addedOn) {
        this.addedOn = addedOn;
    }

    @ManyToMany(mappedBy = "likedPosts", fetch = FetchType.EAGER)
    public Set<User> getUsersLiked() {
        return usersLiked;
    }

    public void setUsersLiked(Set<User> usersLiked) {
        this.usersLiked = usersLiked;
    }

    @OneToMany(mappedBy = "post")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    @Transient
    public void addToUsersLiked(User user) {
        this.usersLiked.add(user);
    }

    @Transient
    public String getSummary() {
        if (description.length() > 50) {
            return this.description.substring(0, 50) + "...";
        }
        return this.description;
    }
}
