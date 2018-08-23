package com.headbook.service;

import com.headbook.domain.entities.Post;
import com.headbook.domain.models.PostCreateBindingModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface PostService {

    void create(PostCreateBindingModel postCreateBindingModel, List<MultipartFile> photos, Principal principal) throws IOException;

    Post findById(Integer id);

    void edit(PostCreateBindingModel bindingModel, List<MultipartFile> photos, Integer id) throws IOException;

    void delete(Post post);

    void create2(PostCreateBindingModel bindingModel, Principal principal);
}
