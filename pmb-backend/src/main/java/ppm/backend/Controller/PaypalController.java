package ppm.backend.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.openidconnect.Userinfo;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payee;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import ppm.backend.Model.User;
import ppm.backend.Service.DataService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/paypal")
@CrossOrigin(origins = "https://www.paymeback.wtf")
public class PaypalController {
  @Value("${paypal.client.id}")
  private String clientId;

  @Value("${paypal.client.secret}")
  private String clientSecret;

  @Value("${paypal.redirect.uri}")
  private String redirectUri;

  @Autowired
  private DataService dataSvc;

  // https://developer.paypal.com/docs/log-in-with-paypal/integrate/
  // https://developer.paypal.com/docs/api/identity/v1/

  // flow
  // frontend -> user logs into paypal
  // paypal -> returns auth code to front end
  // frontend -> sends auth code to backend along with user for db storage
  // backend -> receives auth code and user
  // backend -> gen access code from auth code
  // backend -> use access code to access open id api
  // backend -> store user info into db for future use
  

  // LOGIN

  @GetMapping("link-account")
  public Map<String, String> linkAccount() {
    Map<String, String> response = new HashMap<>();
    String scope = "openid email https://uri.paypal.com/services/paypalattributes";
    String encondedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8).toString();
    String redirectUrl = "https://www.sandbox.paypal.com/signin/authorize?flowEntry=static&client_id=" + 
      clientId + 
      "&scope="+
      scope +
      "&redirect_uri=" +
      encondedRedirectUri;
      response.put("approvalUrl", redirectUrl);
    return response;
  }

  private String getAccessToken(String code) throws JsonMappingException, JsonProcessingException {
    String url = "https://api-m.sandbox.paypal.com/v1/oauth2/token";
    String credentials = clientId + ":" + clientSecret;
    String encodedCreds = Base64.getEncoder().encodeToString(credentials.getBytes());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Authorization", "Basic " + encodedCreds);

    String body = "grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirectUri;
    HttpEntity<String> request = new HttpEntity<>(body, headers);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

    String responseBody = response.getBody();
    JsonNode jsonResponse = new ObjectMapper().readTree(responseBody);
    return jsonResponse.get("access_token").asText();
  }

  private Userinfo getUserInfo(String accessToken) {
    String url = "https://api-m.sandbox.paypal.com/v1/identity/openidconnect/userinfo?schema=openid";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);

    HttpEntity<String> request = new HttpEntity<>(headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Userinfo> response = restTemplate.exchange(url, HttpMethod.GET, request, Userinfo.class);

    return response.getBody();
  }

  @PostMapping("/link-account/code={authCode}")
  public ResponseEntity<String> completeLinkAccount(@PathVariable String authCode, @RequestBody User authUser) throws JsonProcessingException {
    System.out.println(authCode);
    String accessCode = getAccessToken(authCode);
    Userinfo userinfo = getUserInfo(accessCode);
    System.out.println(userinfo.toString());

    dataSvc.insertIntoPaypalInfo(authUser.getUserId(), userinfo);
    return ResponseEntity.ok(userinfo.toJSON());
  }

  // PAYMENT
  // using v1 because can't find resource that allows P2P payment :(
  @PostMapping("/create-order/{path}")
  public Map<String, String> createOrder(@RequestBody Map<String,String> data, @PathVariable String path) {
    Map<String, String> response = new HashMap<>();
    String redirectUrl = "https://www.paymeback.wtf/expenditure/"+path+"/balance";
    APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
    try {
      Amount amount = new Amount();
      amount.setCurrency("USD");
      amount.setTotal(data.get("amount"));

      Payee payee = new Payee();
      payee.setEmail(data.get("email"));

      Payer payer = new Payer();
      payer.setPaymentMethod("paypal");

      Transaction transaction = new Transaction();
      transaction.setAmount(amount);
      transaction.setPayee(payee);

      Payment payment = new Payment();
      payment.setIntent("order");
      payment.setPayer(payer);
      payment.setTransactions(Collections.singletonList(transaction));

      RedirectUrls redirectUrls = new RedirectUrls();
      redirectUrls.setCancelUrl(redirectUrl);
      redirectUrls.setReturnUrl(redirectUrl);
      payment.setRedirectUrls(redirectUrls);

      Payment createdPayment = payment.create(apiContext);
      for (Links link : createdPayment.getLinks()) {
        if (link.getRel().equalsIgnoreCase("approval_url")) {
          response.put("approvalUrl", link.getHref());
          break;
        }
      }

    } catch (PayPalRESTException e) {
      e.printStackTrace();;
    }
    
    return response;
  }
  
  @PostMapping("/capture-order")
  // paymentId: abcd
  // token: abcd
  // payerId: abcd
  public Map<String,String> captureOrder(@RequestBody Map<String,String> data) {
    Map<String, String> response = new HashMap<>();
    APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
    try {
      Payment payment = Payment.get(apiContext, data.get("paymentId"));
      payment.setIntent("sale");
      PaymentExecution paymentExecution = new PaymentExecution();
      paymentExecution.setPayerId(data.get("payerId"));
      Payment executedPayment = payment.execute(apiContext, paymentExecution);
      response.put("status", executedPayment.getState());
    } catch (PayPalRESTException e) {
      e.printStackTrace();
    }

    return response;
  }

}