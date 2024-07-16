package ppm.backend.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.openidconnect.Userinfo;

import ppm.backend.Model.Expenditure;
import ppm.backend.Model.Expense;
import ppm.backend.Model.User;
import ppm.backend.Repository.MySQLRepo.ExpenditureRepo;
import ppm.backend.Repository.MySQLRepo.SQLColumns;
import ppm.backend.Repository.mongoRepo.ExpenseRepo;

@Service
public class DataService implements SQLColumns{

  @Autowired
  private ExpenditureRepo sqlRepo;

  @Autowired
  private ExpenseRepo mongoRepo;

  @Autowired
  private UtilService utilSvc;

  private ObjectMapper mapper = new ObjectMapper();

  public UUID createExpenditure(String name, String curr) {
    UUID exid = UUID.randomUUID();
    sqlRepo.insertToExpenditures(exid, name, curr);
    return exid;
  }

  public JsonNode createUsers(List<User> users) {
    Map<String, UUID> insertedUsers = new HashMap<>();
    for (User user : users) {
      UUID userId = UUID.randomUUID();
      sqlRepo.insertToUser(userId, user.getUserName());
      insertedUsers.put(user.getUserName(), userId);
    }
    System.out.println(insertedUsers.toString());
    return mapper.valueToTree(insertedUsers);
  }

  public User createLoginUser(User user) {
    UUID userId = UUID.randomUUID();
    user.setUserId(userId);
    sqlRepo.insertToUserViaRegister(user);
    return user;
  }

  public User attemptLogin(User user) {
    SqlRowSet rs = sqlRepo.getAttemptLogin(user);
    if (rs.next()) {
      User loginUser = new User(rs, true);
      return loginUser;
    } else {
      return new User();
    }
  }

  public void insertIntoPaypalInfo(UUID uid, Userinfo userinfo) {
    sqlRepo.updatePaypalEmail(uid, userinfo);
  }

  public void insertUserToExpenditure(UUID uid, UUID eid){
    sqlRepo.insertToExpenditureUsers(eid, uid);
  }

  public Document createExpenseInMongo(Map<String, Double> map, UUID eid, UUID exid) {
    // https://stackoverflow.com/questions/73915391/rounding-down-a-float-number-to-2-decimal-points-in-java
    // Using BigDecimal method:
    for (Map.Entry<String, Double> entry : map.entrySet()) {
      entry.setValue(utilSvc.roundToTwoDecimals(entry.getValue()));
    }

    Document doc = new Document(map);
    doc.append("eid", eid.toString());
    doc.append("exid", exid.toString());
    Document createdDoc = mongoRepo.insertExpense(doc);
    return createdDoc;
  }

  @Transactional(rollbackFor = SQLException.class)
  public void createExpenseInDB(Expense expense) {
    // Make sure it's rounded to 2 decimal places for total cost
    Double totalCost = expense.getTotalCost();
    expense.setTotalCost(utilSvc.roundToTwoDecimals(totalCost));
    sqlRepo.insertToExpenses(expense.getEid(), expense.getExpenseOwner().getUserId(), expense.getExid(), expense.getExpenseName(), expense.getTotalCost());

    Double totalOwed = 0.0;
    for (User user : expense.getUsersInvolved()) {
      if (!user.getUserId().equals(expense.getExpenseOwner().getUserId())) {
      Double balance = expense.getExpenseSplit().get(user.getUserName());
      totalOwed += balance;
      sqlRepo.insertToExpenseUsers(expense.getEid(), user.getUserId());
        sqlRepo.updateBalance(-balance, user.getUserId());
      }
    }
    sqlRepo.insertToExpenseUsers(expense.getEid(), expense.getExpenseOwner().getUserId());
    sqlRepo.updateBalance(totalOwed, expense.getExpenseOwner().getUserId());
    // return eid;
  }

  @Transactional(rollbackFor = SQLException.class)
  public void initializeNewExpenditure(String expenditureName, String currency, List<User> users, String inviteToken) throws SQLException {
    UUID eid = createExpenditure(expenditureName, currency);
    JsonNode createdUsers = createUsers(users);
    for (JsonNode user : createdUsers) {
      insertUserToExpenditure(UUID.fromString(user.asText()), eid);
    }
    sqlRepo.insertToInvites(eid, inviteToken);
  }

  public UUID getExpenditureFromPath(String path) {
    SqlRowSet rs = sqlRepo.getExpenditureFromPath(path);
    if (rs.next()) {
      UUID exid = UUID.fromString(rs.getString(EXPENDITURE_ID));
      return exid;
    }
    return null;
  }

  public Expenditure getExpenditureDetails(String path) {
    Expenditure expenditure = new Expenditure();
    SqlRowSet rs = sqlRepo.getExpenditureDetails(path);
    List<User> userList = getUsersForExpenditure(path);
    List<Expense> expenseList = getExpenseDetails(path);
    if (rs.next()) {
      UUID exid = UUID.fromString(rs.getString(EXPENDITURE_ID));
      String eName = rs.getString(EXPENDITURE_NAME);
      String defCurrency = rs.getString(DEFAULT_CURRENCY);
      expenditure.setDefaultCurrency(defCurrency);
      expenditure.setExid(exid);
      expenditure.setExpenditureName(eName);
      expenditure.setUsers(userList);
      expenditure.setExpenses(expenseList);
    }
    return expenditure;
  }

  public List<Expenditure> getExpendituresForUser(UUID uid) {
    SqlRowSet rs = sqlRepo.getExpenditureForUser(uid);
    List<Expenditure> expenditureList = new LinkedList<>();
    while (rs.next()) {
      Expenditure expenditure = new Expenditure().createExpenditure(rs);
      expenditureList.add(expenditure);
    }
    return expenditureList;
  }

  public List<Expense> getExpenseDetails(String path){
    SqlRowSet rs = sqlRepo.getExpensesForExpenditure(path);
    List<Expense> expenseList = convertRowSetToExpenseList(rs);
    return expenseList;
  }

  public List<Expense> getExpensesForOwner(String path, UUID ownerId) {
    SqlRowSet rs = sqlRepo.getExpensesForOwner(path, ownerId);
    List<Expense> expenseList = convertRowSetToExpenseList(rs);
    return expenseList;
  }

  public List<User> getExpenseSummaryOfAllUsers(String path) {
    List<User> userList = getUsersForExpenditure(path);
    
    for (User user : userList) {
      System.out.println("Adding current user: " + user.getUserName());
      SqlRowSet rs = sqlRepo.getExpensesForOwner(path, user.getUserId());
      List<Expense> expenseList = convertRowSetToExpenseList(rs);
      Double accTotalCost = utilSvc.calcAccumulatedTotalCost(expenseList);
      System.out.println("Current accTotalCost: " + accTotalCost);
      Map<String, Double> accumulatedCredit = utilSvc.calcCostIncurredPerUser(expenseList);
      System.out.println("Current accumulatedCredit: " + accumulatedCredit.toString());
      user.setAccumulatedCredit(accumulatedCredit);
      user.setAccumulatedTotalCost(accTotalCost);
    }
    return userList;
  }

  public List<Expense> getExpensesWhereUserOwes(String path, UUID uid) {
    SqlRowSet rs = sqlRepo.getExpensesWhereUserOwes(path, uid);
    List<Expense> expenseList = convertRowSetToExpenseList(rs);
    return expenseList;
  }

  public List<User> getUsersForExpenditure(String path) {
    List<User> usersList = new ArrayList<>();
    SqlRowSet rs = sqlRepo.getUsersOfExpenditure(path);

    while(rs.next()) {
      User user = new User(rs, false);
      usersList.add(user);
    }

    return usersList;
  }

  public void patchLinkedUserId(UUID loginUid, UUID selectedUid) {
    sqlRepo.patchLinkedUser(loginUid, selectedUid);
  }

  public void updateLinkedPaypalEmail(User loginUser, User selectedUser) {
    SqlRowSet rs = sqlRepo.getPaypalEmail(loginUser.getUserId());
    rs.next();
    if (!(rs.getString(PAYPAL_EMAIL) == null)) {
      String paypalEmail = rs.getString(PAYPAL_EMAIL);
      sqlRepo.updatePaypalEmail(selectedUser.getUserId(), paypalEmail);
    }
  }

  // Not putting it under Expense bc there's a mongo call inside ):
  public List<Expense> convertRowSetToExpenseList(SqlRowSet rs) {
    List<Expense> expenseList = new LinkedList<>();
    List<User> userList = new LinkedList<>();
    UUID eidTracker = null;
    Expense exp = new Expense();

    while (rs.next()) {
      UUID eid = UUID.fromString(rs.getString(EXPENSE_ID));
      
      
      // first if is to initialize
      if (eidTracker == null) {
        eidTracker = eid;
        exp = exp.creatExpense(rs);
      } 
      
      // if change expense changed, add users and expense split to exp before reset
      if (!eidTracker.equals(eid)) {
        List<Document> expenseSplitDoc = mongoRepo.getExpense(eidTracker);
        Map<String, Double> expenseSplit = utilSvc.convertDocumentToMap(expenseSplitDoc.getFirst());
        eidTracker = eid;
        exp.setUsersInvolved(userList);
        exp.setExpenseSplit(expenseSplit);
        expenseList.add(exp);

        // change to new expense
        exp = exp.creatExpense(rs);
        // reset userlist
        userList = new LinkedList<>();
      }
      User user = new User(rs.getString(USERNAME), UUID.fromString(rs.getString(USER_ID)));
      userList.add(user);

      if (rs.isLast()){
        exp = exp.creatExpense(rs);
        expenseList.add(exp);
        List<Document> expenseSplitDoc = mongoRepo.getExpense(eidTracker);
        Map<String, Double> expenseSplit = utilSvc.convertDocumentToMap(expenseSplitDoc.getFirst());
        exp.setExpenseSplit(expenseSplit);
        exp.setUsersInvolved(userList);
      }
    }

    return expenseList;
  } 

}
