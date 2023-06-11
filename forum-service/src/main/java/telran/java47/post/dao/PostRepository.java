package telran.java47.post.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java47.post.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {

}
