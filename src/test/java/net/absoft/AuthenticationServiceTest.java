package net.absoft;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

public class AuthenticationServiceTest {

    //Move it outside in a future
    private void validateResponse(Response response, int code, String message) {
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

    private final AuthenticationService authenticationService = new AuthenticationService();

    @Test(
            description = "Successful Authentication",
            groups = "positive",
            threadPoolSize = 5
    )
    @Parameters({"email-address", "password"})
    public void testSuccessfulAuthentication(@Optional("user1@test.com1") String email, @Optional("password1") String pass) {
        Response response = authenticationService.authenticate(email, pass);
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getCode(), 200, "Response code should be 200");
        softAssert.assertTrue(validateToken(response.getMessage()),
                "Token should be the 32 digits string. Got: " + response.getMessage());
        softAssert.assertAll();
    }

    @DataProvider(name = "invalidData", parallel = true)
    public Object[][] invalidLogins() {
        return new Object[][]{
                new Object[]{"user1@test.com", "wrong_password", new Response(401, "Invalid email or password")},
                new Object[]{"", "password1", new Response(400, "Email should not be empty string")},
                new Object[]{"user1", "password1", new Response(400, "Invalid email")},
                new Object[]{"user1@test", "", new Response(400, "Password should not be empty string")},
        };
    }

    @Test(
            groups = "negative",
            dataProvider = "invalidData"
    )
    public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
        validateResponse(
                authenticationService.authenticate(email, password),
                expectedResponse.getCode(),
                expectedResponse.getMessage());
    }

    @Test(
            groups = "negative",
            enabled = false
    )
    public void failedTest() {
        int i = 0;
        System.out.println("Negative case executed");
        assertEquals(i, 5);
    }
}

