package telran.java47.post.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java47.post.dao.ForumRepository;
import telran.java47.post.dto.DatePeriodDto;
import telran.java47.post.dto.NewCommentDto;
import telran.java47.post.dto.NewPostDto;
import telran.java47.post.dto.PostDto;
import telran.java47.post.exceptions.PostNotFoundException;
import telran.java47.post.model.Comment;
import telran.java47.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	final ForumRepository forumRepository;
	final ModelMapper modelMapper;
	
	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		forumRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		return modelMapper.map(forumRepository.findById(id).orElseThrow(() -> new PostNotFoundException()), PostDto.class);

	}

	@Override
	public PostDto removePost(String id) {
		Post post = forumRepository.findById(id).orElseThrow(() -> new PostNotFoundException());
		forumRepository.deleteById(id);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		Post post = forumRepository.findById(id).orElseThrow(() -> new PostNotFoundException());
		if (newPostDto.getTitle() != null) {
			post.setTitle(newPostDto.getTitle());
		}
		if (newPostDto.getContent() != null) {
			post.setContent(newPostDto.getContent());
		}
		newPostDto.getTags().stream()
			.filter(s -> !post.getTags().contains(s))
			.forEach(post.getTags()::add);
		forumRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		Post post = forumRepository.findById(id).orElseThrow(() -> new PostNotFoundException());
		Comment newComment = modelMapper.map(newCommentDto, Comment.class);
		newComment.setUser(author);
		post.addComment(newComment);
		forumRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public void addLike(String id) {
		Post post = forumRepository.findById(id).orElseThrow(() -> new PostNotFoundException());
		post.addLike();
		forumRepository.save(post);
	}

	@Override
	public Iterable<PostDto> findPostByAuthor(String author) {
		return forumRepository.findAllByAuthor(author).get()
				.stream()
				.map(pst -> modelMapper.map(pst, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PostDto> findPostsByTags(List<String> tags) {
		return forumRepository.findByTagsIn(tags).get()
				.stream()
				.map(pst -> modelMapper.map(pst, PostDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
		return forumRepository.findAllByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo()).get()
				.stream()
				.map(pst -> modelMapper.map(pst, PostDto.class))
				.collect(Collectors.toList());
	}

}
