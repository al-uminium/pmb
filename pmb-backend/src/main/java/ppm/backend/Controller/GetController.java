package ppm.backend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ppm.backend.Model.Expenditure;
import ppm.backend.Model.Expense;
import ppm.backend.Model.User;
import ppm.backend.Service.DataService;
import ppm.backend.Service.UtilService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping(value = "/api/get", method = RequestMethod.GET)
@CrossOrigin(origins = "https://www.paymeback.wtf")
public class GetController {
  @Autowired
  private DataService dataSvc;

  @Autowired
  private UtilService utilSvc;

  private ObjectMapper mapper = new ObjectMapper();
  
  @GetMapping("/expenditure/expenses/{path}")
  public String getExpensesForExpenditure(@PathVariable String path) {
    List<Expense> expenseList = dataSvc.getExpenseDetails(path);
    try {
      String jsonArray = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenseList);
      return jsonArray;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "Failed to convert expense list to array";
    }
  }
  
  @GetMapping("/expenditure/{path}/balance/settlements")
  public String getCostSummaryForExpenditure(@PathVariable String path) {
    List<User> userList = dataSvc.getExpenseSummaryOfAllUsers(path);
    Map<String, Map<String,Double>> settlements = new HashMap<>();

    for (User user : userList) {
      Map<String, Double> userSettlements = utilSvc.calcSummaryForUser(userList, user.getUserId());
      settlements.put(user.getUserName(), userSettlements);
    }

    try {
      String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(settlements);
      return jsonString;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "aaaa";
    }
  }

  @GetMapping("/expenditure/users/{path}")
  public String getUsersForExpenditure(@PathVariable String path) {
      List<User> userList = dataSvc.getUsersForExpenditure(path);
      
      try {
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userList);
        return jsonString;
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return "Unable to get users for %s".formatted(path);
      }
  }
  
  @GetMapping("/expenditure/{path}")
  public String getExpenditure(@PathVariable String path) {
    Expenditure expenditure = dataSvc.getExpenditureDetails(path);

    try {
      String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenditure);
      return jsonString;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "Unable to get expenditure";
    }
  }

  // railway is being annoying

  @GetMapping("/expenditure/expenses/user/{uid}&{path}")
  public String getExpensesForOwner(@PathVariable String uid, @PathVariable String path) {
    List<Expense> expenses = dataSvc.getExpensesForOwner(path, UUID.fromString(uid));
    try {
      String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);
      return jsonString;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "Unable to get expenses";
    }
  }

  @GetMapping("/expenditure/expenses/user/{uid}&{path}&owes")
  public String getExpensesWhereUserOwes(@PathVariable String path, @PathVariable String uid) {
    List<Expense> expenses = dataSvc.getExpensesWhereUserOwes(path, UUID.fromString(uid));
    try {
      String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenses);
      return jsonString;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "Unable to get expenses";
    }
  }
  
  @GetMapping("/user/{uid}")
  public String getExpendituresForUser(@PathVariable String uid) {
    UUID userId = UUID.fromString(uid);
    List<Expenditure> expenditures = dataSvc.getExpendituresForUser(userId);
    try {
      String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(expenditures);
      return jsonString;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "Unable to get expenditures";
    }
  }  
}
