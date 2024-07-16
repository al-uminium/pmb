package ppm.backend.Repository.MySQLRepo;

public interface SQLColumns {
  // user
  public static final String USER_ID = "user_id";
  public static final String USERNAME = "username";
  public static final String EMAIL = "email";
  public static final String HASH_PW = "hashpw";
  public static final String REGISTERED_AT = "registered_at";

  // expense
  public static final String EXPENSE_ID = "expense_id";
  public static final String EXPENSE_NAME = "expense_name";
  public static final String MONGO_SPLIT_ID = "mongo_split_id";
  public static final String TOTAL_COST = "total_cost";
  public static final String EXPENDITURE_ID = "expenditure_id";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String EXPENSE_OWNER_ID = "owner_id";
  public static final String EXPENSE_OWNER_USERNAME = "owner_username";
  
  // expenditures
  public static final String DEFAULT_CURRENCY = "default_currency";
  public static final String EXPENDITURE_NAME = "expenditure_name";
  public static final String BALANCE = "balance";

  // invites
  public static final String INVITE_TOKEN = "invite_token";
  public static final String EXPIRE_AT = "expire_at";

  // paypal
  public static final String PAYPAL_EMAIL = "paypalEmail";
}
