package com.headbook.service;

import com.headbook.domain.entities.Photo;
import com.headbook.domain.entities.Post;
import com.headbook.domain.exceptions.ResourceNotFoundException;
import com.headbook.domain.models.PostCreateBindingModel;
import com.headbook.repository.PhotoRepository;
import com.headbook.repository.PostRepository;
import com.headbook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final PhotoRepository photoRepository;

    private final UserRepository userRepository;

    private final CloudService cloudService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PhotoRepository photoRepository, UserRepository userRepository, CloudService cloudService) {
        this.postRepository = postRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
        this.cloudService = cloudService;
    }

    @Override
    public void create(PostCreateBindingModel postCreateBindingModel, List<MultipartFile> photos, Principal principal) throws IOException {
        Post post = new Post();
        post.setDescription(postCreateBindingModel.getDescription());
        post.setAuthor(this.userRepository.findByEmail(principal.getName()));
        this.postRepository.saveAndFlush(post);

        for (MultipartFile multipartFile : photos) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setUrl(this.cloudService.upload(multipartFile));
            this.photoRepository.saveAndFlush(photo);
        }
    }

    @Override
    public void delete(Post post) {
        this.postRepository.deleteById(post.getId());
    }

    @Override
    public void create2(PostCreateBindingModel bindingModel, Principal principal) {
        Post post = new Post();
        post.setDescription(bindingModel.getDescription());
        post.setAuthor(this.userRepository.findByEmail(principal.getName()));
        this.postRepository.saveAndFlush(post);
    }

    @Override
    public void edit(PostCreateBindingModel bindingModel, List<MultipartFile> photos, Integer id) throws IOException {
        Post post = this.findById(id);
        post.setDescription(bindingModel.getDescription());
        for (MultipartFile multipartFile : photos) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setUrl(this.cloudService.upload(multipartFile));
            this.photoRepository.saveAndFlush(photo);
        }
    }

    @Override
    public Post findById(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);

        if (!post.isPresent()) throw new ResourceNotFoundException();

        return post.get();
    }
}
