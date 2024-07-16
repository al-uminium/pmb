package ppm.backend.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import ppm.backend.Model.Expense;
import ppm.backend.Model.User;

@Service
public class UtilService {

  public String generateInviteToken(int length) {

    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    SecureRandom rand = new SecureRandom();

    StringBuilder result = new StringBuilder(length);
    int charactersLength = chars.length();
    
    for (int i = 0; i < length; i++) {
        int randomIndex = rand.nextInt(charactersLength);  
        result.append(chars.charAt(randomIndex));     
    }
    
    return result.toString();
  }

  public String generateExpenditureURL(String inviteToken) {
    return "https://www.paymeback.wtf/expenditure/%s".formatted(inviteToken);
  }

  public Map<String, Double> calcCostIncurredPerUser(List<Expense> expenses) {
    Map<String, Double> costMap = expenses.stream()
        .flatMap(expense -> expense.getExpenseSplit().entrySet().stream())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            Double::sum));

    return costMap;
  }

  public Expense convertJsonToExpense(JsonNode expenseJson) {
    Expense expense = new Expense(); 
    String expenseName = expenseJson.get("expenseName").asText();
    // UUID expenseOwnerId = UUID.fromString(expenseJson.get("expenseOwnerId").toString().replaceAll("\"", ""));
    UUID exid = UUID.fromString(expenseJson.get("exid").toString().replaceAll("\"", ""));
    Double totalCost = expenseJson.get("totalCost").asDouble();

    expense.setExpenseName(expenseName);
    // expense.setExpenseOwnerID(expenseOwnerId);
    expense.setExid(exid);
    expense.setTotalCost(totalCost);
    return expense;
  }

  public List<String> getListOfUsers(JsonNode data) {
    List<String> userList = new ArrayList<>(); 

    for (JsonNode user : data) {
      userList.add(user.textValue());
    }

    return userList;
  }

  public UUID getExpenditureID(List<Expense> expList) {
    return expList.getFirst().getExid();
  }

  public Map<String, Double> convertDocumentToMap(Document doc) {
    Map<String, Double> res = new HashMap<>();
    for(Map.Entry<String,Object> entry : doc.entrySet()) {
      if (entry.getValue() instanceof Double) {
        res.put(entry.getKey(), (Double) entry.getValue());
      }
    }
    return res;
  }

  public Map<String, Double> calcSummaryForUser(List<User> userList, UUID uid) {
    Map<String, Double> costMap = new HashMap<>();
    User targetUser = new User();
    List<User> otherUsers = new ArrayList<>();

    for (User user : userList) {
      if (user.getUserId().equals(uid)) {
        targetUser = user;
        System.out.println(">>>>>> Found target user: " + targetUser.toString());
      } else {
        otherUsers.add(user);
      }
    }

    // ensure target user does not have null map
    if (targetUser.getAccumulatedCredit().isEmpty()) {
      Map<String, Double> map = new HashMap<>();
      for (User user : otherUsers) {
        map.put(user.getUserName(), 0.0);
      }
      targetUser.setAccumulatedCredit(map);
      System.out.println(">>> Setting new map for... " + targetUser.getUserName());
      System.out.println(map.toString());
    }


    System.out.println("Sanity check... " + otherUsers.toString());

    for (User user : otherUsers) {
      Map<String, Double> accCost = user.getAccumulatedCredit();

      System.out.println(">>> Currently in " + user.getUserName());
      if (accCost.isEmpty()) {
        System.out.println("It's empty");
        costMap.put(user.getUserName(), targetUser.getAccumulatedCredit().get(user.getUserName()));
      }
      for (Map.Entry<String, Double> userDebt : accCost.entrySet()) {
        System.out.println("Going thru " + user.getUserName() + "'s accumulated credit...");
        if (userDebt.getKey().equals(targetUser.getUserName())) {
          System.out.println(targetUser.getUserName() + " owes " + user.getUserName() + " " +  userDebt.getValue());
          Double debt = 0.0;
          Double credit = 0.0;
          try {
            debt = userDebt.getValue();
            credit = targetUser.getAccumulatedCredit().get(user.getUserName());
            System.out.println("Sanity check... ");
            System.out.println(targetUser.getUserName() + ": " + credit);
            System.out.println(user.getUserName() + ": -" + debt);
            costMap.put(user.getUserName(), roundToTwoDecimals(credit - debt));
          } catch (NullPointerException e) {
            credit = 0.0;
          }
          costMap.put(user.getUserName(), roundToTwoDecimals(credit - debt));
        } 
      }
    }

    return costMap;
  }
  
  public Double calcAccumulatedTotalCost(List<Expense> expList) {
    Double accCost = 0.0;
    for (Expense expense : expList) {
      Double totalCost = expense.getTotalCost();
      accCost += totalCost;
    }
    return accCost;
  }

  public Instant convertStringToInstant(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
    Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
    return instant;
  }

  public Double roundToTwoDecimals(Double num) {
    BigDecimal rounded = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
    return rounded.doubleValue();
  }
}
