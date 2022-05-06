package net.absoft;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdk.jfr.Description;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest {

  //Move it outside in a future
  private void validateErrorResponse(Response response, int code, String message) {
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertEquals(response.getCode(), code, "Response code unexpected");
    softAssert.assertEquals(response.getMessage(), message);
    softAssert.assertAll();
  }

  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }

  @Test
  public void testSuccessfulAuthentication() {
    Response response = new AuthenticationService().authenticate("user1@test.com", "password1");
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertEquals(response.getCode(), 200, "Response code should be 200");
    softAssert.assertTrue(validateToken(response.getMessage()),
            "Token should be the 32 digits string. Got: " + response.getMessage());
    softAssert.assertAll();
  }

  @Test
  public void testAuthenticationWithWrongPassword() {
    validateErrorResponse(
            new AuthenticationService()
            .authenticate("user1@test.com", "wrong_password1"),
            401,
            "Invalid email or password");
  }

  @Test
  public void testAuthenticationWithEmptyEmail() {
    validateErrorResponse(
            new AuthenticationService()
            .authenticate("", "password1"),
            400,
            "Email should not be empty string");
  }

  @Test
  public void testAuthenticationWithInvalidEmail() {
    validateErrorResponse(
            new AuthenticationService()
            .authenticate("user1", "password1"),
            400,
            "Invalid email");
  }

  @Test
  public void testAuthenticationWithEmptyPassword() {
    validateErrorResponse(
            new AuthenticationService()
            .authenticate("user1@test", ""),
            400,
            "Password should not be empty string");
  }
}
