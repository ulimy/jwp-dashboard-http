package nextstep.jwp.controller;

import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.util.FileReader;

public class RegisterController implements Controller {

    private static final Controller instance = new RegisterController();

    private RegisterController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return HttpResponse.ok("/register.html", FileReader.read("/register.html"));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        Map<String, String> body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        if (!InMemoryUserRepository.isAlreadySignIn(account, password, email)) {
            System.out.println("로그인 성공! 아이디 : " + account);
            InMemoryUserRepository.save(new User(account, password, email));
        }

        return HttpResponse.found("/index.html");
    }

}
