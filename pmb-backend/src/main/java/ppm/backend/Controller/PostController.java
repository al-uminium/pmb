package ppm.backend.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ppm.backend.Model.Expenditure;
import ppm.backend.Model.Expense;
import ppm.backend.Model.User;
import ppm.backend.Service.DataService;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(value = "/api/post", method = RequestMethod.POST)
@CrossOrigin(origins = "https://www.paymeback.wtf")
public class PostController {

  @Autowired
  private DataService dataSvc;

  private ObjectMapper mapper = new ObjectMapper();

  @PostMapping(value = "/initializeexpenditure")
  public ResponseEntity<String> createExpenditure(@RequestBody Expenditure expenditure) {
    System.out.println(expenditure.toString());
    try {
      System.out.println(expenditure);
      dataSvc.initializeNewExpenditure(expenditure.getExpenditureName(),
          expenditure.getDefaultCurrency(),
          expenditure.getUsers(),
          expenditure.getInviteToken());
      return ResponseEntity.ok(mapper.writeValueAsString("Expenditure created"));
    } catch (SQLException e) {
      e.printStackTrace();
      ResponseEntity.badRequest().body("Failed to create expenditure");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      System.out.println("Error parsing JSON");
    }
    return null;

  }
  
  @PostMapping("/expense/{path}")
  public ResponseEntity<String> createExpense(@RequestBody Expense expense, @PathVariable String path) throws JsonProcessingException {
    System.out.println(">>> Printing expense received... "+ expense.toString());
    UUID exid = dataSvc.getExpenditureFromPath(path);
    UUID eid = UUID.randomUUID();
    expense.setEid(eid);
    expense.setExid(exid);
    dataSvc.createExpenseInDB(expense);
    dataSvc.createExpenseInMongo(expense.getExpenseSplit(), eid, exid);
    return ResponseEntity.ok(mapper.writeValueAsString("Expense created"));
  }

  @PostMapping("/register")
  public ResponseEntity<String> createUserViaRegister(@RequestBody User user) throws JsonProcessingException {
    User createdUser = dataSvc.createLoginUser(user);
      
    return ResponseEntity.ok(mapper.writeValueAsString(createdUser));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody User user) throws JsonProcessingException {
    User loginUser = dataSvc.attemptLogin(user);
      
    return ResponseEntity.ok(mapper.writeValueAsString(loginUser));
  }
  
  @PostMapping("/link-account")
  public void postMethodName(@RequestBody List<User> data) {
    User loginUser = data.getFirst();
    User selectedUser = data.getLast();
    dataSvc.patchLinkedUserId(loginUser.getUserId(), selectedUser.getUserId());
    dataSvc.updateLinkedPaypalEmail(loginUser, selectedUser); //if it exists
  }
  
  
}

