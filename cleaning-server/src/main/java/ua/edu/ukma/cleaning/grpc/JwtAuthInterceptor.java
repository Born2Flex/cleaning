package ua.edu.ukma.cleaning.grpc;

import io.grpc.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ua.edu.ukma.cleaning.security.JwtService;
import ua.edu.ukma.cleaning.user.AuthenticatedUser;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthInterceptor implements ServerInterceptor {

    private final JwtService jwtService;

    private static final Metadata.Key<String> AUTHORIZATION_METADATA_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String authHeader = headers.get(AUTHORIZATION_METADATA_KEY);
        String token = null;
        AuthenticatedUser user = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                user = jwtService.extractUser(token);
            } catch (Exception ex) {
                call.close(Status.UNAUTHENTICATED.withDescription("Invalid or missing JWT token"), headers);
                log.info("No token!");
                return new ServerCall.Listener<>() {};
            }
        }
        if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                //authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                return Contexts.interceptCall(Context.current(), call, headers, next);
            }
            log.info("Validation failed");
        }

        log.info("auth header: {}, user: {}", authHeader, user);
        //call.close(Status.UNAUTHENTICATED.withDescription("Invalid or missing JWT token"), headers);
        return Contexts.interceptCall(Context.current(), call, headers, next);
        //return new ServerCall.Listener<>() {};

    }
}
