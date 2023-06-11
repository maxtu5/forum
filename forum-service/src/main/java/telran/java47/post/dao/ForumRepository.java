package telran.java47.post.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java47.post.model.Post;

public interface ForumRepository extends MongoRepository<Post, String> {

	Optional<List<Post>> findAllByAuthor(String author);

	Optional<List<Post>> findAllByDateCreatedBetween(LocalDate dateFrom, LocalDate dateTo);

	Optional<List<Post>> findByTagsIn(List<String> tags);

}
