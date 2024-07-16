package ppm.backend.Repository.MySQLRepo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.paypal.api.openidconnect.Userinfo;

import ppm.backend.Model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@Repository
public class ExpenditureRepo implements SQLQueries, SQLColumns {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void insertToExpenditures(UUID expid, String exName, String currency) {
    jdbcTemplate.update(INSERT_INTO_EXPENDITURES, expid.toString(), exName, currency);
  }

  // insert into Expense (expense_id, owner_id, expenditure_id, expense_name, total_cost) VALUES (?, ?, ?, ?, ?)
  public void insertToExpenses(UUID eid, UUID uid, UUID exid, String eName, Double totalCost) {
    jdbcTemplate.update(INSERT_INTO_EXPENSES, eid.toString(), uid.toString(), exid.toString(), totalCost, eName);
  }

  public void updatePaypalEmail (UUID uid, Userinfo userinfo) {
    jdbcTemplate.update(UPDATE_PAYPAL_EMAIL, userinfo.getEmail(), uid.toString());
  }

  public void updatePaypalEmail(UUID uid, String email) {
    jdbcTemplate.update(UPDATE_PAYPAL_EMAIL, email, uid.toString());
  }

  public void updateBalance(Double balance, UUID uid) {
    jdbcTemplate.update(UPDATE_BALANCE, balance, uid.toString());
  }

  public void insertToExpenseUsers(UUID eid, UUID uid) {
    jdbcTemplate.update(INSERT_INTO_EXPENSE_USERS, eid.toString(), uid.toString());
  }

  public void insertToUser(UUID uid, String username) {
    jdbcTemplate.update(INSERT_INTO_USER, uid.toString(), username);
  }

  public void insertToExpenditureUsers(UUID exid, UUID uid) {
    jdbcTemplate.update(INSERT_INTO_EXPENDITURE_USERS, exid.toString(), uid.toString());
  }

  public void insertToInvites(UUID exid, String inviteToken) {
    jdbcTemplate.update(INSERT_INTO_INVITES, exid.toString(), inviteToken);
  }

  public void insertToUserViaRegister(User user) {
    jdbcTemplate.update(INSERT_INTO_USER_VIA_LOGIN, user.getUserId().toString(), user.getUserName(), user.getPw(), user.getEmail());
  }

  public void patchLinkedUser(UUID loginUid, UUID selectedUid) {
    jdbcTemplate.update(PATCH_LINKED_USER_ID, loginUid.toString(), selectedUid.toString());
  }

  public SqlRowSet getExpenditureFromPath(String path) {
    return jdbcTemplate.queryForRowSet(GET_EXPENDITURE_ID_FROM_PATH, path);
  }
  
  public SqlRowSet getExpensesForExpenditure(String path) {
    return jdbcTemplate.queryForRowSet(GET_EXPENSES_FOR_EXPENDITURE, path);
  }

  public SqlRowSet getExpensesForOwner(String path, UUID uid) {
    return jdbcTemplate.queryForRowSet(GET_EXPENSES_FOR_EXPENDITURE_FOR_OWNER, path, uid.toString());
  }

  public SqlRowSet getExpensesWhereUserOwes(String path, UUID uid) {
    return jdbcTemplate.queryForRowSet(GET_EXPENSES_WHERE_USER_OWES, path, uid.toString(), uid.toString());
  }

  public SqlRowSet getUsersOfExpenditure(String path) {
    return jdbcTemplate.queryForRowSet(GET_USERS_FOR_EXPENDITURE, path);
  }

  public SqlRowSet getExpenditureDetails(String path) {
    return jdbcTemplate.queryForRowSet(GET_EXPENDITURE_DETAILS, path);
  }

  public SqlRowSet getAttemptLogin(User user) {
    return jdbcTemplate.queryForRowSet(ATTEMPT_LOGIN, user.getEmail(), user.getPw());
  }

  public SqlRowSet getExpenditureForUser(UUID uid) {
    return jdbcTemplate.queryForRowSet(GET_EXPENDITURES_FOR_AUTH_USER, uid.toString());
  }

  public SqlRowSet getPaypalEmail(UUID uid) {
    return jdbcTemplate.queryForRowSet(GET_PAYPAL_EMAIL, uid.toString());
  }
}
