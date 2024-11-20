package org.ukma.notificationserver.grpc;

import io.grpc.ClientInterceptor;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ukma.notificationserver.security.JwtService;
import org.ukma.notificationserver.user.UserServerClientFeign;

@RequiredArgsConstructor
@Configuration
public class GrpcConfig {
    private final UserServerClientFeign userServerClient;
    private final JwtService jwtService;

    @Bean
    @GrpcGlobalClientInterceptor
    public ClientInterceptor jwtAuthInterceptor() {
        return new GrpcClientRequestInterceptor(userServerClient, jwtService);
    }
}
