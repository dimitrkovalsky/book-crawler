package com.liberty.book.crawler.service;

import com.liberty.book.crawler.entity.TagBookEntity;
import com.liberty.book.crawler.entity.TagEntity;
import com.liberty.book.crawler.entity.TagSelectionEntity;
import com.liberty.book.crawler.repository.TagBookRepository;
import com.liberty.book.crawler.repository.TagRepository;
import com.liberty.book.crawler.repository.TagSelectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 15.06.2017.
 */
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagSelectionRepository tagSelectionRepository;

    @Autowired
    private TagBookRepository tagBookRepository;

    private Long getTagId(String tag){
        TagEntity tagEntity = tagRepository.getFirstByTagNameEquals(tag);
        if(tagEntity==null){
            TagEntity newTag = new TagEntity();
            newTag.setTagName(tag);
            newTag = tagRepository.save(newTag);
            return newTag.getTagId();
        }else {
            return tagEntity.getTagId();
        }
    }

    public void tagSelection(Long selectionId, List<String> tagList){
        tagList.stream().distinct().forEach(s -> {
            TagSelectionEntity tagSelectionEntity = new TagSelectionEntity();
            tagSelectionEntity.setSelectionId(selectionId);
            tagSelectionEntity.setTagId(getTagId(s));
            try{
                tagSelectionRepository.save(tagSelectionEntity);
            } catch(DataIntegrityViolationException e) {
                System.out.println("Object is already exist in database");
            }
        });
    }

    public void tagBook(Long bookId, List<String> tagList){
        tagList.stream().distinct().forEach(s -> {
            TagBookEntity tagBook = new TagBookEntity();
            tagBook.setBookId(bookId);
            tagBook.setTagId(getTagId(s));
            try{

                tagBookRepository.save(tagBook);
            } catch(DataIntegrityViolationException e) {
                System.out.println("Object is already exist in database");
            }
        });
    }
}
