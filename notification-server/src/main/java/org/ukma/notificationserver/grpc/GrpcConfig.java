package org.ukma.notificationserver.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ukma.notificationserver.user.UserServerClient;

@RequiredArgsConstructor
@Configuration
public class GrpcConfig {

    private final UserServerClient userServerClient;

    @Bean
    @GrpcGlobalClientInterceptor
    public ClientInterceptor jwtAuthInterceptor() {
        return new GrpcClientRequestInterceptor(userServerClient);
    }
}
