package br.edu.ifsp.tcc.gbarzagli.embrapa.share.controller;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifsp.tcc.gbarzagli.embrapa.share.BeanConfiguration;
import br.edu.ifsp.tcc.gbarzagli.embrapa.share.model.Authorization;
import br.edu.ifsp.tcc.gbarzagli.embrapa.share.model.Researcher;
import br.edu.ifsp.tcc.gbarzagli.embrapa.share.model.Token;
import br.edu.ifsp.tcc.gbarzagli.embrapa.share.model.Tokens;
import br.edu.ifsp.tcc.gbarzagli.embrapa.share.repository.ResearcherRepository;

@RestController
public class AuthController {

    @Autowired
    ResearcherRepository researcherRepository;

    private static ApplicationContext context;
    static {
        context = new AnnotationConfigApplicationContext(BeanConfiguration.class);
    }

    /**
     * Authenticate the researcher
     * 
     * @param username
     * @param password
     * @return a authorization to use the system containing the token and an expiration time
     */
    @RequestMapping(value = "/auth", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Authorization authenticate(@RequestParam String username, @RequestParam String password) throws Exception {
        Authorization auth = null;

        Researcher researcher = researcherRepository.findByUsername(username);
        if (researcher != null && researcher.getPassword().equals(password)) {
            Date today = Calendar.getInstance().getTime();

            // Calculate the expiration time
            Calendar validThru = Calendar.getInstance();
            validThru.add(Calendar.MINUTE, 30);
            Date validDate = validThru.getTime();
            long expiration = today.getTime() + validDate.getTime();

            // Create the token
            String key = tokenize(researcher.getUsername(), expiration);
            Token token = new Token(researcher.getId(), key);
            Tokens tokens = context.getBean(Tokens.class);
            tokens.addToken(token);

            auth = new Authorization(key, expiration);
        }

        return auth;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String name, @RequestParam String username, @RequestParam String password) {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);

        Researcher researcher = researcherRepository.findByUsername(username);

        if (researcher == null) {
            researcher = new Researcher(name, username, password);
            researcherRepository.save(researcher);
        } else {
            response = new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return response;
    }

    private String tokenize(String username, long expiration) throws Exception {
        String token = null;

        try {
            Date today = Calendar.getInstance().getTime();
            String todayHexString = Long.toHexString(today.getTime());
            String expirationHexString = Long.toHexString(expiration);
            token = username + todayHexString + expirationHexString;

            byte[] bytes = getMD5Hash(token).getBytes();
            byte[] encodedToken = Base64.getEncoder().encode(bytes);

            token = Base64.getUrlEncoder().encodeToString(encodedToken);
        } catch (Throwable e) {
            token = null;
            throw new Exception(e);
        }

        return token;
    }

    private String getMD5Hash(String string) throws Exception {
        String md5Hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(string.getBytes());
            byte[] digest = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 64).substring(1));
            }

            md5Hash = sb.toString();
        } catch (Throwable e) {
            throw new Exception(e);
        }

        return md5Hash;
    }

}
