package com.sensedia.demo.adapters.repository;

import com.sensedia.commons.errors.exceptions.PreConditionException;
import com.sensedia.demo.domains.User;
import com.sensedia.demo.domains.search.UserSearch;
import com.sensedia.demo.domains.search.UserSearchResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class AdvancedUserSearchImpl implements AdvancedUserSearch {

  private final MongoTemplate mongoTemplate;

  @Value("${app.repository.maximumLimit}")
  private int maximumLimit;

  @Value("${app.repository.defaultLimit}")
  private int defaultLimit;

  @Autowired
  public AdvancedUserSearchImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public UserSearchResponse findAll(UserSearch userSearch) {
    int limit = userSearch.getLimit() == null ? defaultLimit : userSearch.getLimit();

    if (limit > maximumLimit) {
      throw new PreConditionException(
          "The 'limit' field is greater than the configured maximum limit [" + maximumLimit + "]");
    }

    Query query = buildQuery(userSearch);

    Integer total = Math.toIntExact(mongoTemplate.count(query, User.class));

    int page = userSearch.getPage() > 0 ? userSearch.getPage() - 1 : 0;

    Sort.Direction direction = Sort.Direction.fromString(userSearch.getSortType().getValue());

    query.with(
        PageRequest.of(page, limit, Sort.by(direction, userSearch.getSort().getFieldName())));

    List<User> users = mongoTemplate.find(query, User.class);

    return new UserSearchResponse(users, total, maximumLimit);
  }

  private Query buildQuery(UserSearch userSearch) {
    Query query = new Query();

    if (StringUtils.isNotBlank(userSearch.getEmail()))
      query.addCriteria(Criteria.where("email").regex(".*" + userSearch.getEmail() + ".*", "i"));

    if (StringUtils.isNotBlank(userSearch.getName()))
      query.addCriteria(Criteria.where("name").regex(".*" + userSearch.getName() + ".*", "i"));

    if (userSearch.getStatus() != null)
      query.addCriteria(Criteria.where("status").is(userSearch.getStatus()));

    if (userSearch.getCreateDateStart() != null || userSearch.getCreateDateEnd() != null) {
      Criteria criteria = Criteria.where("creationDate");

      if (userSearch.getCreateDateStart() != null) criteria.gte(userSearch.getCreateDateStart());

      if (userSearch.getCreateDateEnd() != null) criteria.lt(userSearch.getCreateDateEnd());

      query.addCriteria(criteria);
    }

    return query;
  }
}
