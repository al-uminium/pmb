package ppm.backend.Model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import lombok.Data;
import ppm.backend.Repository.MySQLRepo.SQLColumns;

@Data
public class Expenditure {
  UUID exid;
  String defaultCurrency;
  String expenditureName;
  List<User> users;
  String inviteToken;
  List<Expense> expenses;
  Instant createdDate;
  Instant updatedDate;

  public Expenditure() {}

  public Expenditure createExpenditure(SqlRowSet rs) {
    String expenditureName = rs.getString(SQLColumns.EXPENDITURE_NAME);
    UUID expenditureId = UUID.fromString(rs.getString(SQLColumns.EXPENDITURE_ID));
    String inviteToken = rs.getString(SQLColumns.INVITE_TOKEN);

    Expenditure expenditure = new Expenditure();
    expenditure.setExpenditureName(expenditureName);
    expenditure.setExid(expenditureId);
    expenditure.setInviteToken(inviteToken);
    return expenditure;
  }

}
