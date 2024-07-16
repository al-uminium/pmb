package ppm.backend.Repository.mongoRepo;

import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepo {
  @Autowired
  private MongoTemplate mongoTemplate; 

  public Document insertExpense(Document expense) {
    return mongoTemplate.insert(expense, "expense");
  }

  public List<Document> getExpensesForExpenditure(UUID exid) {
    Criteria criteria = Criteria.where("exid").is(exid.toString());
    Query query = Query.query(criteria);

    return mongoTemplate.find(query, Document.class, "expense");
  }

  public List<Document> getExpense(UUID eid) {
    Criteria criteria = Criteria.where("eid").is(eid.toString());
    Query query = Query.query(criteria);
    return mongoTemplate.find(query, Document.class, "expense");
  }
}
