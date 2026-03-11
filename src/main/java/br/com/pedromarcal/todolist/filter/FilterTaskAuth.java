package br.com.pedromarcal.todolist.filter;

import java.io.IOException;
import java.util.Base64;


import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.pedromarcal.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {


    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        var servletPath = request.getServletPath();
        if (servletPath.startsWith("/tasks/")) {

            var authorization = request.getHeader("Authorization");
            var authEncoded = authorization.substring("basic".length()).trim();

            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);


            String[] credential = authString.split(":");
            String username = credential[0];
            String password = credential[1];

            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401, "usuario sem autorização");
            } else {

                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
                {
                    filterChain.doFilter(request, response);
                }
            }

        }else{
            filterChain.doFilter(request, response);
        }
    }
}

