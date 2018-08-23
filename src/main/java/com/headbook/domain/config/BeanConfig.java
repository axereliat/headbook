package com.headbook.domain.config;

import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Cloudinary cloudinary() {
        Map<String, Object> map = new HashMap<>();
        map.put("cloud_name", "dr8ovbzd2");
        map.put("api_key", "562999287916593");
        map.put("api_secret", "B7oPT9o7Dk0W2E1fOe3e7PS2ZIY");

        return new Cloudinary(map);
    }
}
