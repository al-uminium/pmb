package ppm.backend.Repository.MySQLRepo;

public interface SQLQueries {
  public static final String INSERT_INTO_EXPENDITURES = 
    """
      insert into Expenditures (expenditure_id, expenditure_name, default_currency) VALUES (?, ?, ?)
    """;

  // public static final String INSERT_INTO_EXPENSES = """
  // insert into Expense (expense_id, owner_id, expenditure_id, expense_name,
  // total_cost) VALUES (?, ?, ?, ?, ?, ?)
  // """;

  public static final String INSERT_INTO_EXPENSES = 
    """
      insert into Expense (expense_id, owner_id, expenditure_id, total_cost, expense_name) VALUES (?, ?, ?, ?, ?)
    """;

  public static final String INSERT_INTO_EXPENSE_USERS = 
    """
      insert into Expense_Users (expense_id, user_id) VALUES (?, ?)
    """;
  
  public static final String INSERT_INTO_USER_VIA_LOGIN = 
    """
      insert into User (user_id, username, hashpw, email) VALUES (?, ?, ?, ?);
    """;

  public static final String INSERT_INTO_USER =
    """
      insert into User (user_id, username) VALUES (?, ?);
    """;

  public static final String INSERT_INTO_EXPENDITURE_USERS = 
    """
      insert into Expenditure_User (expenditure_id, user_id) VALUES (?, ?)
    """;

  public static final String INSERT_INTO_INVITES = 
    """
      insert into Invites (expenditure_id, invite_token) VALUES (?, ?)
    """;

  // FOR UPDATE QUERIES
  public static final String UPDATE_BALANCE =
    """
      UPDATE Expenditure_User SET balance = balance + ? where user_id = ? 
    """;

  // FOR GET QUERIES
  public static final String GET_EXPENDITURE_ID_FROM_PATH = 
    """
      SELECT expenditure_id FROM Invites WHERE invite_token = ?;
    """;

  public static final String GET_EXPENSES_FOR_EXPENDITURE = 
  """
    SELECT
      e.expense_id, 
      e.expenditure_id, 
      e.expense_name, 
      e.total_cost,
      e.owner_id,
      u_owner.username AS owner_username,
      eu.user_id,
      u_user.username AS username,
      u_user.paypalEmail AS paypalEmail
    FROM Expense e
    JOIN Invites i ON e.expenditure_id = i.expenditure_id
    JOIN Expense_Users eu ON e.expense_id = eu.expense_id
    JOIN User u_owner ON e.owner_id = u_owner.user_id
    JOIN User u_user ON eu.user_id = u_user.user_id
    WHERE i.invite_token = ?
    ORDER BY e.created_at DESC;
  """;

  public static final String GET_EXPENSES_FOR_EXPENDITURE_FOR_OWNER = 
    """
      SELECT
        e.expense_id, 
        e.expenditure_id, 
        e.expense_name, 
        e.total_cost,
        e.owner_id,
        u_owner.username AS owner_username,
        eu.user_id,
        u_user.username AS username
      FROM Expense e
      JOIN Invites i ON e.expenditure_id = i.expenditure_id
      JOIN Expense_Users eu ON e.expense_id = eu.expense_id
      JOIN User u_owner ON e.owner_id = u_owner.user_id
      JOIN User u_user ON eu.user_id = u_user.user_id
      WHERE i.invite_token = ? AND e.owner_id = ?
      ORDER BY e.created_at DESC;
    """;
  
  public static final String GET_EXPENSES_WHERE_USER_OWES = 
  """
    SELECT
      e.expense_id, 
      e.expenditure_id, 
      e.expense_name, 
      e.total_cost,
      e.owner_id,
      u_owner.username AS owner_username,
      eu.user_id,
      u_user.username AS username
    FROM Expense e
    JOIN Invites i ON e.expenditure_id = i.expenditure_id
    JOIN Expense_Users eu ON e.expense_id = eu.expense_id
    JOIN User u_owner ON e.owner_id = u_owner.user_id
    JOIN User u_user ON eu.user_id = u_user.user_id
    WHERE i.invite_token = ?
    AND e.owner_id <> ?
    AND eu.user_id = ?
    ORDER BY e.created_at DESC; 
  """;

  public static final String GET_USERS_FOR_EXPENDITURE = 
    """
      SELECT u.username, eu.user_id, eu.balance, u.email, u.paypalEmail
      FROM Expenditure_User eu
      JOIN Invites i ON eu.expenditure_id = i.expenditure_id
      JOIN User u ON u.user_id = eu.user_id
      WHERE i.invite_token = ?;
    """;

  public static final String GET_EXPENDITURE_DETAILS = 
  """
    SELECT ex.expenditure_id, ex.expenditure_name, ex.default_currency, ex.created_at, ex.updated_at
    FROM Expenditures ex
    JOIN Invites i on ex.expenditure_id = i.expenditure_id
    WHERE i.invite_token = ?;    
  """;

  public static final String ATTEMPT_LOGIN = 
    """
      SELECT * 
        FROM User
      WHERE email = ? AND hashpw = ?;
    """;

  public static final String PATCH_LINKED_USER_ID = 
  """
    UPDATE Expenditure_User
    SET linked_user_id = ?
    WHERE user_id = ?;    
  """;

  public static final String GET_EXPENDITURES_FOR_AUTH_USER = 
    """
      SELECT e.expenditure_id, e.expenditure_name, i.invite_token
      FROM Expenditures e
      JOIN Expenditure_User eu ON eu.expenditure_id = e.expenditure_id
      JOIN Invites i on i.expenditure_id = e.expenditure_id
      WHERE eu.linked_user_id = ?;
    """;

  // for some reason when i get user info from paypal i don't have the id? 
  // adjusting the sql until i can find the fix, but it's not an issue. 
  public static final String UPDATE_PAYPAL_EMAIL =
  """
    UPDATE User
    SET paypalEmail = ?
    WHERE user_id = ?;
  """;

  public static final String GET_PAYPAL_EMAIL =
  """
    SELECT * from User
    WHERE user_id = ?;  
  """;
}
