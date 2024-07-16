package ppm.backend.Model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import lombok.Data;
import ppm.backend.Repository.MySQLRepo.SQLColumns;

@Data
public class Expense implements SQLColumns {
  String expenseName;
  User expenseOwner;
  // UUID expenseOwnerID;
  Double totalCost;
  Map<String, Double> expenseSplit;
  List<User> usersInvolved;
  UUID exid;
  UUID eid;

  public Expense creatExpense(SqlRowSet rs) {
    Expense exp = new Expense();
    UUID ownerId = UUID.fromString(rs.getString(EXPENSE_OWNER_ID));
    UUID eid = UUID.fromString(rs.getString(EXPENSE_ID));
    UUID exid = UUID.fromString(rs.getString(EXPENDITURE_ID));
    String ownerUserName = rs.getString(EXPENSE_OWNER_USERNAME);
    String expenseName = rs.getString(EXPENSE_NAME);
    Double totalCost = rs.getDouble(TOTAL_COST);

    User owner = new User(ownerUserName, ownerId);
    exp.setExpenseOwner(owner);
    exp.setEid(eid);
    exp.setExid(exid);
    exp.setTotalCost(totalCost);
    exp.setExpenseName(expenseName);
    exp.setExpenseSplit(expenseSplit);
    return exp;
  }
}
