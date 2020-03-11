package main.services.Impl;

import main.model.repositories.TagToPostRepository;
import main.services.interfaces.TagToPostRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagToPostRepositoryServiceImpl implements TagToPostRepositoryService {

    @Autowired
    private TagToPostRepository tagToPostRepository;
}
