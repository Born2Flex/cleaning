package ua.edu.ukma.cleaning.grpc;

import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.ukma.cleaning.security.JwtService;

@RequiredArgsConstructor
@Configuration
public class GrpcConfig {

    private final JwtService jwtService;

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor jwtAuthInterceptor() {
        return new JwtAuthInterceptor(jwtService);
    }
}
